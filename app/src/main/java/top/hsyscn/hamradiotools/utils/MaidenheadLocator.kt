package top.hsyscn.hamradiotools.utils

/**
 * 梅登黑德定位系统工具类
 * 用于经纬度与梅登黑德网格之间的转换
 */
object MaidenheadLocator {
    
    /**
     * 将经纬度转换为梅登黑德网格
     * @param latitude 纬度（-90 到 90）
     * @param longitude 经度（-180 到 180）
     * @return 梅登黑德网格字符串
     */
    fun toMaidenhead(latitude: Double, longitude: Double, precision: Int = 6): String {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw IllegalArgumentException("Coordinates out of valid range")
        }
        
        // 计算Field部分（前两个字符）
        val field1 = Math.floor((180.0 + longitude) / 20.0).toInt()
        val field2 = Math.floor((90.0 + latitude) / 10.0).toInt()
        val field = "" + index2Char(field1) + index2Char(field2)
        
        // 计算Square部分（第三、四个字符）
        val square1 = Math.floor((Math.floor(longitude + 180.0) % 20.0) / 2.0).toInt()
        val square2 = Math.floor(latitude + 90.0) % 10.0
        val code = field + square1 + square2.toInt()
        
        // 计算Block部分（第五、六个字符）
        val block1 = Math.floor((longitude - Math.floor(longitude / 2.0) * 2.0) * 60.0 / 5.0).toInt()
        val block2 = Math.floor((latitude - Math.floor(latitude)) * 60.0 / 2.5).toInt()
        val finalCode = code + index2Char(block1) + index2Char(block2)
        
        return finalCode.toString()
    }
    
    /**
     * 将梅登黑德网格转换为经纬度（网格中心点）
     * @param locator 梅登黑德网格字符串
     * @return 经纬度对（纬度，经度）
     */
    fun fromMaidenhead(locator: String): Pair<Double, Double> {
        val normalizedLocator = locator.uppercase().trim()
        if (normalizedLocator.length < 6) {
            throw IllegalArgumentException("Invalid Maidenhead grid format")
        }
        
        // 根据用户提供的getBound函数算法计算
        val lon = char2Index(normalizedLocator[0]) * 20.0 + 
                  normalizedLocator[2].digitToInt() * 2.0 + 
                  char2Index(normalizedLocator[4]) * 5.0 / 60.0 - 180.0
        
        val lat = char2Index(normalizedLocator[1]) * 10.0 + 
                  normalizedLocator[3].digitToInt() * 1.0 + 
                  char2Index(normalizedLocator[5]) * 2.5 / 60.0 - 90.0
        
        // 添加网格半宽/半高，使其指向网格中心
        val centerLon = lon + 2.5 / 60.0
        val centerLat = lat + 1.25 / 60.0
        
        return Pair(centerLat, centerLon)
    }
    
    /**
     * 索引转换为字符（A-R）
     */
    private fun index2Char(idx: Int): Char {
        return 'A' + idx
    }
    
    /**
     * 字符转换为索引
     */
    private fun char2Index(c: Char): Int {
        return c - 'A'
    }
    
    /**
     * 验证梅登黑德网格字符串是否有效
     */
    fun isValidLocator(locator: String): Boolean {
        val normalized = locator.uppercase().trim()
        if (normalized.length < 6) {
            return false
        }
        
        // 检查第一个字符（A-R）
        if (normalized[0] !in 'A'..'R') {
            return false
        }
        
        // 检查第二个字符（A-R）
        if (normalized[1] !in 'A'..'R') {
            return false
        }
        
        // 检查第三个字符（0-9）
        if (!normalized[2].isDigit()) {
            return false
        }
        
        // 检查第四个字符（0-9）
        if (!normalized[3].isDigit()) {
            return false
        }
        
        // 检查第五个字符（A-X）
        if (normalized[4] !in 'A'..'X') {
            return false
        }
        
        // 检查第六个字符（A-X）
        if (normalized[5] !in 'A'..'X') {
            return false
        }
        
        return true
    }
}