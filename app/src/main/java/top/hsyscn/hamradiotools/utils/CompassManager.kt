package top.hsyscn.hamradiotools.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

/**
 * 指南针管理器类
 * 用于获取设备的方位角（指南针方向）
 */
class CompassManager(private val context: Context) {
    
    // 传感器管理器实例
    private val sensorManager: SensorManager = 
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    // 方向传感器
    private val orientationSensor: Sensor? = 
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    
    // 当前方位角的状态流
    private val _currentHeading = MutableStateFlow(0f)
    val currentHeading: StateFlow<Float> = _currentHeading
    
    // 传感器监听器
    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event ?: return
            
            if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                // 从旋转向量计算方位角
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)
                
                // 计算方位角（弧度转角度，调整为0-360度）
                val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                val adjustedAzimuth = if (azimuth < 0) azimuth + 360 else azimuth
                
                _currentHeading.value = adjustedAzimuth
            }
        }
        
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // 传感器精度变化时的处理
        }
    }
    
    /**
     * 检查设备是否有指南针传感器
     */
    fun hasCompass(): Boolean {
        return orientationSensor != null
    }
    
    /**
     * 开始监听指南针传感器
     */
    fun startCompassListening() {
        orientationSensor?.let {
            sensorManager.registerListener(
                sensorEventListener,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }
    
    /**
     * 停止监听指南针传感器
     */
    fun stopCompassListening() {
        sensorManager.unregisterListener(sensorEventListener)
    }
    
    /**
     * 获取当前方位角（度），四舍五入到最接近的整数
     */
    fun getCurrentHeadingRounded(): Int {
        return currentHeading.value.roundToInt()
    }
}