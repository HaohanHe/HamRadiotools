package top.hsyscn.hamradiotools.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 位置管理器类
 * 用于获取设备的位置信息
 */
class LocationManager(private val context: Context) {
    
    // 位置管理器实例
    private val locationManager: LocationManager = 
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    
    // 当前位置的状态流
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation
    
    // 位置监听器
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            _currentLocation.value = location
        }
        
        override fun onProviderEnabled(provider: String) {
            // 位置提供者启用时的处理
        }
        
        override fun onProviderDisabled(provider: String) {
            // 位置提供者禁用时的处理
        }
        
        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            // 位置提供者状态变化时的处理
        }
    }
    
    /**
     * 检查位置权限是否已授予
     */
    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * 请求位置更新
     */
    fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            return
        }
        
        // 尝试使用GPS提供者
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000, // 最小更新时间（毫秒）
                10f,   // 最小更新距离（米）
                locationListener
            )
        }
        
        // 尝试使用网络提供者
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                10000, // 最小更新时间（毫秒）
                10f,   // 最小更新距离（米）
                locationListener
            )
        }
        
        // 尝试获取最后已知位置
        getLastKnownLocation()
    }
    
    /**
     * 停止位置更新
     */
    fun stopLocationUpdates() {
        locationManager.removeUpdates(locationListener)
    }
    
    /**
     * 获取最后已知位置
     */
    private fun getLastKnownLocation() {
        if (!hasLocationPermission()) {
            return
        }
        
        // 尝试获取GPS提供者的最后已知位置
        val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (gpsLocation != null) {
            _currentLocation.value = gpsLocation
            return
        }
        
        // 尝试获取网络提供者的最后已知位置
        val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (networkLocation != null) {
            _currentLocation.value = networkLocation
        }
    }
}