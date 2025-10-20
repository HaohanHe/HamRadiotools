package top.hsyscn.hamradiotools.utils

/**
 * 地图链接生成器工具类
 * 用于生成到各种第三方地图应用的链接
 */
object MapLinkGenerator {
    
    /**
     * 生成谷歌地图链接
     * @param latitude 纬度
     * @param longitude 经度
     * @param label 标记名称
     * @return 谷歌地图链接
     */
    fun generateGoogleMapsLink(latitude: Double, longitude: Double, label: String = "定位点"): String {
        return "https://www.google.com/maps?q=$latitude,$longitude($label)"
    }
    
    /**
     * 生成高德地图链接
     * @param latitude 纬度
     * @param longitude 经度
     * @param label 标记名称
     * @return 高德地图链接
     */
    fun generateAmapLink(latitude: Double, longitude: Double, label: String = "定位点"): String {
        return "https://uri.amap.com/marker?position=$longitude,$latitude&name=$label&coordinate=gaode&callnative=1"
    }
    
    /**
     * 生成腾讯地图链接
     * @param latitude 纬度
     * @param longitude 经度
     * @param label 标记名称
     * @return 腾讯地图链接
     */
    fun generateTencentMapsLink(latitude: Double, longitude: Double, label: String = "定位点"): String {
        return "https://apis.map.qq.com/uri/v1/marker?marker=title:$label&coord_type=1&marker=coord:$latitude,$longitude"
    }
    
    /**
     * 生成百度地图链接
     * @param latitude 纬度
     * @param longitude 经度
     * @param label 标记名称
     * @return 百度地图链接
     */
    fun generateBaiduMapsLink(latitude: Double, longitude: Double, label: String = "定位点"): String {
        // 百度地图使用自己的坐标系统，这里直接使用WGS84坐标，实际应用中可能需要坐标转换
        return "http://api.map.baidu.com/marker?location=$latitude,$longitude&title=$label&content=$label&output=html&src=webapp.baidu.openAPIdemo"
    }
    
    /**
     * 生成通用地图意图链接（用于Android系统选择地图应用）
     * @param latitude 纬度
     * @param longitude 经度
     * @param label 标记名称
     * @return 通用地图URI
     */
    fun generateUniversalMapUri(latitude: Double, longitude: Double, label: String = "定位点"): String {
        return "geo:$latitude,$longitude?q=$latitude,$longitude($label)"
    }
}