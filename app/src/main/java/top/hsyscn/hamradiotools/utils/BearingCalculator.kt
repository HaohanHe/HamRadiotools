package top.hsyscn.hamradiotools.utils

import kotlin.math.*

/**
 * 方位角计算器工具类
 * 用于计算两个经纬度点之间的方位角和距离
 */
object BearingCalculator {
    
    /**
     * 计算两个经纬度点之间的方位角（从起点到终点）
     * @param startLat 起点纬度
     * @param startLon 起点经度
     * @param endLat 终点纬度
     * @param endLon 终点经度
     * @return 方位角（度），范围 0-360，0 表示正北
     */
    fun calculateBearing(startLat: Double, startLon: Double, endLat: Double, endLon: Double): Double {
        // 将经纬度转换为弧度
        val startLatRad = Math.toRadians(startLat)
        val startLonRad = Math.toRadians(startLon)
        val endLatRad = Math.toRadians(endLat)
        val endLonRad = Math.toRadians(endLon)
        
        // 计算方位角
        val deltaLon = endLonRad - startLonRad
        val y = sin(deltaLon) * cos(endLatRad)
        val x = cos(startLatRad) * sin(endLatRad) - sin(startLatRad) * cos(endLatRad) * cos(deltaLon)
        var bearing = Math.toDegrees(atan2(y, x))
        
        // 确保方位角在 0-360 范围内
        if (bearing < 0) {
            bearing += 360.0
        }
        
        return bearing
    }
    
    /**
     * 计算两个经纬度点之间的大圆距离
     * @param startLat 起点纬度
     * @param startLon 起点经度
     * @param endLat 终点纬度
     * @param endLon 终点经度
     * @return 距离（公里）
     */
    fun calculateDistance(startLat: Double, startLon: Double, endLat: Double, endLon: Double): Double {
        // 将经纬度转换为弧度
        val startLatRad = Math.toRadians(startLat)
        val startLonRad = Math.toRadians(startLon)
        val endLatRad = Math.toRadians(endLat)
        val endLonRad = Math.toRadians(endLon)
        
        // 应用半正矢公式
        val deltaLat = endLatRad - startLatRad
        val deltaLon = endLonRad - startLonRad
        val a = sin(deltaLat / 2).pow(2) + cos(startLatRad) * cos(endLatRad) * sin(deltaLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        // 地球半径约为 6371 公里
        val radius = 6371.0
        return radius * c
    }
    
    /**
     * 计算天线应指向的实际方位角
     * @param bearing 计算得到的方位角
     * @param compassHeading 指南针读数
     * @return 天线应指向的方位角（相对于正北）
     */
    fun calculateAntennaDirection(bearing: Double, compassHeading: Double): Double {
        // 天线指向方位角 = 目标方位角 - 指南针读数 + 偏移修正
        var antennaDirection = bearing - compassHeading
        
        // 确保结果在 0-360 范围内
        if (antennaDirection < 0) {
            antennaDirection += 360.0
        } else if (antennaDirection >= 360) {
            antennaDirection -= 360.0
        }
        
        return antennaDirection
    }
    
    /**
     * 将方位角转换为方向文本
     * @param bearing 方位角（度）
     * @return 方向文本（如：N, NE, E, SE, S, SW, W, NW）
     */
    fun bearingToDirection(bearing: Double): String {
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", 
                                "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = ((bearing + 11.25) % 360 / 22.5).toInt()
        return directions[index]
    }
}