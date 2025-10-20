package top.hsyscn.hamradiotools

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import top.hsyscn.hamradiotools.ui.theme.HamRadioToolsTheme
import top.hsyscn.hamradiotools.utils.BearingCalculator
import kotlin.math.roundToInt
import top.hsyscn.hamradiotools.utils.CompassManager
import top.hsyscn.hamradiotools.utils.LocationManager
import top.hsyscn.hamradiotools.utils.MaidenheadLocator
import androidx.compose.ui.graphics.vector.ImageVector
import top.hsyscn.hamradiotools.utils.MapLinkGenerator

// 导航路线定义
sealed class Screen(val route: String, val title: String, val iconResource: ImageVector)
object AntennaPointing : Screen("antenna_pointing", "天线指向", Icons.Default.North)
object MapIntegration : Screen("map_integration", "经纬地图", Icons.Default.Map)
object Maidenhead : Screen("maidenhead", "梅登黑德网格", Icons.Default.GpsFixed)

// 所有导航路线列表 - 移到Composable函数内部

class MainActivity : ComponentActivity() {
    // 位置管理器
    private lateinit var locationManager: LocationManager
    
    // 指南针管理器
    private lateinit var compassManager: CompassManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = LocationManager(this)
        compassManager = CompassManager(this)
        
        enableEdgeToEdge()
        setContent {
            HamRadioToolsTheme {
                HamRadioToolsApp(
                    context = this,
                    locationManager = locationManager,
                    compassManager = compassManager
                )
            }
        }
    }
    
    override fun onStart() {
        super.onStart()
        // 开始监听指南针
        compassManager.startCompassListening()
        
        // 如果已授予位置权限，开始获取位置更新
        if (locationManager.hasLocationPermission()) {
            locationManager.startLocationUpdates()
        }
    }
    
    override fun onStop() {
        super.onStop()
        // 停止监听指南针
        compassManager.stopCompassListening()
        
        // 停止位置更新
        locationManager.stopLocationUpdates()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HamRadioToolsApp(
    context: MainActivity,
    locationManager: LocationManager,
    compassManager: CompassManager
) {
    // 所有导航路线列表
    val allScreens = listOf(AntennaPointing, MapIntegration, Maidenhead)
    
    // 状态管理
    val navController = rememberNavController()
    val currentScreen = remember {
        mutableStateOf(allScreens[0])
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    
    // 监听导航变化
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect {
            it?.destination?.route?.let { route ->
                currentScreen.value = allScreens.find { screen -> screen.route == route } ?: allScreens[0]
            }
        }
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // 使用纯色背景的侧边栏
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                DrawerContent(
                    allScreens = allScreens,
                    currentScreen = currentScreen.value,
                    onScreenSelected = { screen ->
                        currentScreen.value = screen
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        // 使用协程作用域关闭抽屉
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "HamRadiotools")
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "菜单")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AntennaPointing.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(AntennaPointing.route) {
                AntennaPointingScreen(
                    context = context,
                    locationManager = locationManager,
                    compassManager = compassManager
                )
            }
            composable(MapIntegration.route) {
                MapIntegrationScreen(
                    context = context,
                    locationManager = locationManager
                )
            }
            composable(Maidenhead.route) {
                MaidenheadScreen(
                    locationManager = locationManager
                )
            }
        }
    }
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(
    allScreens: List<Screen>,
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        // 抽屉头部
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        ) {
            Text(
                text = "HamRadiotools",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // 导航项列表
        LazyColumn(Modifier.fillMaxSize()) {
            items(allScreens) {
                NavigationDrawerItem(
                icon = { Icon(imageVector = it.iconResource, contentDescription = null) },
                    label = {
                        Text(text = it.title)
                    },
                    selected = currentScreen == it,
                    onClick = {
                        onScreenSelected(it)
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    }
}

// 天线指向计算页面
@Composable
fun AntennaPointingScreen(
    context: MainActivity,
    locationManager: LocationManager,
    compassManager: CompassManager
) {
    // 权限状态
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            locationManager.startLocationUpdates()
        }
    }
    
    // 当前位置
    val currentLocation by locationManager.currentLocation.collectAsStateWithLifecycle()
    
    // 当前方位角
    val currentHeading by compassManager.currentHeading.collectAsStateWithLifecycle()
    
    // UI状态变量
    var myLatitude by remember { mutableStateOf("") }
    var myLongitude by remember { mutableStateOf("") }
    var myMaidenhead by remember { mutableStateOf("") }
    var targetLatitude by remember { mutableStateOf("") }
    var targetLongitude by remember { mutableStateOf("") }
    var targetMaidenhead by remember { mutableStateOf("") }
    var calculatedBearing by remember { mutableStateOf("") }
    var calculatedDistance by remember { mutableStateOf("") }
    var antennaDirection by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    // 监听位置变化
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            myLatitude = String.format("%.6f", it.latitude)
            myLongitude = String.format("%.6f", it.longitude)
            myMaidenhead = MaidenheadLocator.toMaidenhead(it.latitude, it.longitude)
        }
    }
    
    // 监听指南针变化
    LaunchedEffect(currentHeading) {
        // 如果有目标位置和当前位置，重新计算天线指向
        if (targetLatitude.isNotEmpty() && targetLongitude.isNotEmpty() && 
            myLatitude.isNotEmpty() && myLongitude.isNotEmpty()) {
            try {
                val startLat = myLatitude.toDouble()
                val startLon = myLongitude.toDouble()
                val endLat = targetLatitude.toDouble()
                val endLon = targetLongitude.toDouble()
                
                val bearing = BearingCalculator.calculateBearing(startLat, startLon, endLat, endLon)
                val distance = BearingCalculator.calculateDistance(startLat, startLon, endLat, endLon)
                val direction = BearingCalculator.calculateAntennaDirection(bearing, currentHeading.toDouble())
                
                calculatedBearing = String.format("%.1f°", bearing)
                calculatedDistance = String.format("%.1f km", distance)
                antennaDirection = String.format("%.1f°", direction)
            } catch (e: Exception) {
                errorMessage = "计算错误: ${e.message}"
            }
        }
    }
    
    // 请求权限
    LaunchedEffect(Unit) {
        if (!locationManager.hasLocationPermission()) {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }
    
    // 更新计算结果的辅助函数
    fun updateCalculations(
        myLat: String,
        myLon: String,
        targetLat: String,
        targetLon: String,
        heading: Float,
        onResult: (String, String, String) -> Unit
    ) {
        if (myLat.isNotEmpty() && myLon.isNotEmpty() && targetLat.isNotEmpty() && targetLon.isNotEmpty()) {
            try {
                val startLat = myLat.toDouble()
                val startLon = myLon.toDouble()
                val endLat = targetLat.toDouble()
                val endLon = targetLon.toDouble()
                
                val bearing = BearingCalculator.calculateBearing(startLat, startLon, endLat, endLon)
                val distance = BearingCalculator.calculateDistance(startLat, startLon, endLat, endLon)
                val direction = BearingCalculator.calculateAntennaDirection(bearing, heading.toDouble())
                
                onResult(
                    String.format("%.1f°", bearing),
                    String.format("%.1f km", distance),
                    String.format("%.1f°", direction)
                )
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "计算错误: ${e.message}"
            }
        }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            item {
                Text(
                    text = "天线指向计算器",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            
            item {
                // 我的位置卡片
                LocationCard(
                    title = "我的位置",
                    latitude = myLatitude,
                    longitude = myLongitude,
                    maidenhead = myMaidenhead,
                    showMaidenhead = true,
                    onLatitudeChange = { myLatitude = it },
                    onLongitudeChange = { myLongitude = it },
                    onMaidenheadChange = { myMaidenhead = it },
                    onGpsClick = {
                        if (!locationManager.hasLocationPermission()) {
                            permissionLauncher.launch(arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ))
                        } else {
                            locationManager.startLocationUpdates()
                        }
                    },
                    onConvertFromMaidenhead = {
                        if (myMaidenhead.isNotEmpty()) {
                            try {
                                val (lat, lon) = MaidenheadLocator.fromMaidenhead(myMaidenhead)
                                myLatitude = String.format("%.6f", lat)
                                myLongitude = String.format("%.6f", lon)
                                errorMessage = ""
                            } catch (e: Exception) {
                                errorMessage = "梅登黑德网格格式错误"
                            }
                        }
                    },
                    onConvertToMaidenhead = {
                        if (myLatitude.isNotEmpty() && myLongitude.isNotEmpty()) {
                            try {
                                val lat = myLatitude.toDouble()
                                val lon = myLongitude.toDouble()
                                myMaidenhead = MaidenheadLocator.toMaidenhead(lat, lon)
                                errorMessage = ""
                            } catch (e: Exception) {
                                errorMessage = "经纬度格式错误"
                            }
                        }
                    },
                    onMapClick = { if (myLatitude.isNotEmpty() && myLongitude.isNotEmpty()) {
                        try {
                            val lat = myLatitude.toDouble()
                            val lon = myLongitude.toDouble()
                            val mapUri = MapLinkGenerator.generateUniversalMapUri(lat, lon, "我的位置")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUri))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            errorMessage = "打开地图失败"
                        }
                    }}
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                // 目标位置卡片
                LocationCard(
                    title = "目标位置",
                    latitude = targetLatitude,
                    longitude = targetLongitude,
                    maidenhead = targetMaidenhead,
                    showMaidenhead = true,
                    onLatitudeChange = { 
                        targetLatitude = it
                        updateCalculations(
                            myLatitude, myLongitude, targetLatitude, targetLongitude,
                            currentHeading
                        ) { b, d, a ->
                            calculatedBearing = b
                            calculatedDistance = d
                            antennaDirection = a
                        }
                    },
                    onGpsClick = { /* 目标位置不需要GPS功能，留空实现 */ },
                    onLongitudeChange = { 
                        targetLongitude = it
                        updateCalculations(
                            myLatitude, myLongitude, targetLatitude, targetLongitude,
                            currentHeading
                        ) { b, d, a ->
                            calculatedBearing = b
                            calculatedDistance = d
                            antennaDirection = a
                        }
                    },
                    onMaidenheadChange = { targetMaidenhead = it },
                    onConvertFromMaidenhead = {
                        if (targetMaidenhead.isNotEmpty()) {
                            try {
                                val (lat, lon) = MaidenheadLocator.fromMaidenhead(targetMaidenhead)
                                targetLatitude = String.format("%.6f", lat)
                                targetLongitude = String.format("%.6f", lon)
                                errorMessage = ""
                                // 更新计算
                                updateCalculations(
                                    myLatitude, myLongitude, targetLatitude, targetLongitude,
                                    currentHeading
                                ) { b, d, a ->
                                    calculatedBearing = b
                                    calculatedDistance = d
                                    antennaDirection = a
                                }
                            } catch (e: Exception) {
                                errorMessage = "梅登黑德网格格式错误"
                            }
                        }
                    },
                    onConvertToMaidenhead = {
                        if (targetLatitude.isNotEmpty() && targetLongitude.isNotEmpty()) {
                            try {
                                val lat = targetLatitude.toDouble()
                                val lon = targetLongitude.toDouble()
                                targetMaidenhead = MaidenheadLocator.toMaidenhead(lat, lon)
                                errorMessage = ""
                            } catch (e: Exception) {
                                errorMessage = "经纬度格式错误"
                            }
                        }
                    },
                    onMapClick = { if (targetLatitude.isNotEmpty() && targetLongitude.isNotEmpty()) {
                        try {
                            val lat = targetLatitude.toDouble()
                            val lon = targetLongitude.toDouble()
                            val mapUri = MapLinkGenerator.generateUniversalMapUri(lat, lon, "目标位置")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUri))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            errorMessage = "打开地图失败"
                        }
                    }}
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                // 计算结果卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "天线指向计算结果",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("方位角:")
                            Text(calculatedBearing)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("距离:")
                            Text(calculatedDistance)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("天线应指向:")
                            Text(
                                text = antennaDirection,
                                color = if (antennaDirection.isNotEmpty()) Color.Blue else Color.Gray,
                                fontWeight = if (antennaDirection.isNotEmpty()) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 天线指向箭头
                        if (antennaDirection.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(Color.LightGray, RoundedCornerShape(60.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .background(Color.White, RoundedCornerShape(55.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // 使用旋转的指南针图标来指示方向
                                    val rotation = if (antennaDirection.isNotEmpty()) {
                                        antennaDirection.replace("°", "").toFloatOrNull() ?: 0f
                                    } else {
                                        0f
                                    }
                                    Icon(
                                        imageVector = Icons.Default.North,
                                        contentDescription = "天线指向",
                                        modifier = Modifier
                                            .size(48.dp)
                                            .rotate(rotation),
                                        tint = Color.Blue
                                    )
                                }
                            }
                            Text(
                                text = "箭头指示天线应指向的方向",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (errorMessage.isNotEmpty()) {
                                Text(
                                    text = errorMessage,
                                    color = Color.Red,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            
            // 权限说明
            item {
                if (!locationManager.hasLocationPermission()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "需要位置权限",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            Text(
                                text = "请授予应用位置权限以获取您的位置信息",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Button(
                                onClick = {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package", context.packageName, null)
                                    intent.data = uri
                                    context.startActivity(intent)
                                }
                            ) {
                                Text("前往设置")
                            }
                        }
                    }
                }
            }
        }
    )
}

// 地图集成页面
@Composable
fun MapIntegrationScreen(
    context: MainActivity,
    locationManager: LocationManager
) {
    // 权限状态
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            locationManager.startLocationUpdates()
        }
    }
    
    // 当前位置
    val currentLocation by locationManager.currentLocation.collectAsStateWithLifecycle()
    
    // UI状态变量
    var myLatitude by remember { mutableStateOf("") }
    var myLongitude by remember { mutableStateOf("") }
    var targetLatitude by remember { mutableStateOf("") }
    var targetLongitude by remember { mutableStateOf("") }
    var targetMaidenhead by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    // 监听位置变化
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            myLatitude = String.format("%.6f", it.latitude)
            myLongitude = String.format("%.6f", it.longitude)
        }
    }
    
    // 请求权限
    LaunchedEffect(Unit) {
        if (!locationManager.hasLocationPermission()) {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(
                text = "经纬地图",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // 目标位置输入
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "输入目标位置",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    TextField(
                        value = targetLatitude,
                        onValueChange = { targetLatitude = it },
                        label = { Text("纬度") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    TextField(
                        value = targetLongitude,
                        onValueChange = { targetLongitude = it },
                        label = { Text("经度") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // 梅登黑德网格输入和转换
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextField(
                            value = targetMaidenhead,
                            onValueChange = { targetMaidenhead = it },
                            label = { Text("梅登黑德网格") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        Button(
                            onClick = {
                                if (targetMaidenhead.isNotEmpty()) {
                                    try {
                                        val (lat, lon) = MaidenheadLocator.fromMaidenhead(targetMaidenhead)
                                        targetLatitude = String.format("%.6f", lat)
                                        targetLongitude = String.format("%.6f", lon)
                                        errorMessage = ""
                                    } catch (e: Exception) {
                                        errorMessage = "梅登黑德网格格式错误"
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .height(56.dp)
                        ) {
                            Text("网格→经纬度")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = {
                            if (targetLatitude.isNotEmpty() && targetLongitude.isNotEmpty()) {
                                try {
                                    val lat = targetLatitude.toDouble()
                                    val lon = targetLongitude.toDouble()
                                    targetMaidenhead = MaidenheadLocator.toMaidenhead(lat, lon)
                                    errorMessage = ""
                                } catch (e: Exception) {
                                    errorMessage = "经纬度格式错误"
                                }
                            }
                        }) {
                            Text("经纬度→网格")
                        }
                    }
                }
            }
        
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
            
            // 地图选择按钮组
            if (targetLatitude.isNotEmpty() && targetLongitude.isNotEmpty()) {
                Text(
                    text = "打开第三方地图",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                val lat = targetLatitude.toDoubleOrNull()
                val lon = targetLongitude.toDoubleOrNull()
                
                if (lat != null && lon != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MapButton(
                            name = "谷歌地图",
                            onClick = {
                                val mapUri = MapLinkGenerator.generateGoogleMapsLink(lat, lon, "目标位置")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUri))
                                context.startActivity(intent)
                            }
                        )
                        
                        MapButton(
                            name = "高德地图",
                            onClick = {
                                val mapUri = MapLinkGenerator.generateAmapLink(lat, lon, "目标位置")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUri))
                                context.startActivity(intent)
                            }
                        )
                        
                        MapButton(
                            name = "腾讯地图",
                            onClick = {
                                val mapUri = MapLinkGenerator.generateTencentMapsLink(lat, lon, "目标位置")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUri))
                                context.startActivity(intent)
                            }
                        )
                        
                        MapButton(
                            name = "系统地图",
                            onClick = {
                                val mapUri = MapLinkGenerator.generateUniversalMapUri(lat, lon, "目标位置")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUri))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
        
        item {
            // 错误信息
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }
        
        item {
            // 权限说明
            if (!locationManager.hasLocationPermission()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "需要位置权限",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text(
                            text = "请授予应用位置权限以获取您的位置信息",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", context.packageName, null)
                                intent.data = uri
                                context.startActivity(intent)
                            }
                        ) {
                            Text("前往设置")
                        }
                    }
                }
            }
        }
    }
}

// 梅登黑德网格转换页面
@Composable
fun MaidenheadScreen(
    locationManager: LocationManager
) {
    // 权限状态
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            it[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            locationManager.startLocationUpdates()
        }
    }
    
    // 当前位置
    val currentLocation by locationManager.currentLocation.collectAsStateWithLifecycle()
    
    // UI状态变量
    var myLatitude by remember { mutableStateOf("") }
    var myLongitude by remember { mutableStateOf("") }
    var myMaidenhead by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    // 监听位置变化
    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            myLatitude = String.format("%.6f", it.latitude)
            myLongitude = String.format("%.6f", it.longitude)
            myMaidenhead = MaidenheadLocator.toMaidenhead(it.latitude, it.longitude)
        }
    }
    
    // 请求权限
    LaunchedEffect(Unit) {
        if (!locationManager.hasLocationPermission()) {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "梅登黑德网格转换",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // 位置输入和转换卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 获取位置按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            if (!locationManager.hasLocationPermission()) {
                                permissionLauncher.launch(arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ))
                            } else {
                                locationManager.startLocationUpdates()
                            }
                        },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "获取位置", modifier = Modifier.padding(end = 8.dp))
                        Text("获取当前位置")
                    }
                }
                
                // 经纬度输入
                TextField(
                    value = myLatitude,
                    onValueChange = { myLatitude = it },
                    label = { Text("纬度") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = myLongitude,
                    onValueChange = { myLongitude = it },
                    label = { Text("经度") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 转换按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            if (myLatitude.isNotEmpty() && myLongitude.isNotEmpty()) {
                                try {
                                    val lat = myLatitude.toDouble()
                                    val lon = myLongitude.toDouble()
                                    myMaidenhead = MaidenheadLocator.toMaidenhead(lat, lon)
                                    errorMessage = ""
                                } catch (e: Exception) {
                                    errorMessage = "经纬度格式错误"
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text("经纬度 → 梅登黑德")
                    }
                    
                    Button(
                        onClick = {
                            if (myMaidenhead.isNotEmpty()) {
                                try {
                                    val (lat, lon) = MaidenheadLocator.fromMaidenhead(myMaidenhead)
                                    myLatitude = String.format("%.6f", lat)
                                    myLongitude = String.format("%.6f", lon)
                                    errorMessage = ""
                                } catch (e: Exception) {
                                    errorMessage = "梅登黑德网格格式错误"
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text("梅登黑德 → 经纬度")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 梅登黑德网格结果
                TextField(
                    value = myMaidenhead,
                    onValueChange = { myMaidenhead = it },
                    label = { Text("梅登黑德网格") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // 错误信息
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp
            )
        }
        
        // 权限说明
        if (!locationManager.hasLocationPermission()) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "需要位置权限",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "请授予应用位置权限以获取您的位置信息",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // 获取Context实例
                    val context = LocalContext.current
                    
                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", context.packageName, null)
                            intent.data = uri
                            context.startActivity(intent)
                        }
                    ) {
                        Text("前往设置")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationCard(
    title: String,
    latitude: String,
    longitude: String,
    maidenhead: String,
    showMaidenhead: Boolean,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onMaidenheadChange: (String) -> Unit,
    onGpsClick: () -> Unit,
    onConvertFromMaidenhead: () -> Unit,
    onConvertToMaidenhead: () -> Unit,
    onMapClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = latitude,
                    onValueChange = onLatitudeChange,
                    label = { Text("纬度") },
                    modifier = Modifier.weight(1f)
                )
                
                Button(
                    onClick = onGpsClick,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "获取位置")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextField(
                value = longitude,
                onValueChange = onLongitudeChange,
                label = { Text("经度") },
                modifier = Modifier.fillMaxWidth()
            )
            
            if (showMaidenhead) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextField(
                        value = maidenhead,
                        onValueChange = onMaidenheadChange,
                        label = { Text("梅登黑德网格") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    Button(
                        onClick = onConvertFromMaidenhead,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .height(56.dp)
                    ) {
                        Text("网格→经纬度")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onConvertToMaidenhead) {
                        Text("经纬度→网格")
                    }
                    
                    Button(
                        onClick = onMapClick,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row {
                            Icon(Icons.Default.Place, contentDescription = "地图")
                            Text("地图", modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapButton(name: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(name, fontSize = 16.sp)
    }
}

// 更新计算结果函数
fun updateCalculations(
    myLat: String,
    myLon: String,
    targetLat: String,
    targetLon: String,
    currentHeading: Float,
    onUpdate: (String, String, String) -> Unit
) {
    if (myLat.isNotEmpty() && myLon.isNotEmpty() && 
        targetLat.isNotEmpty() && targetLon.isNotEmpty()) {
        try {
            val startLat = myLat.toDouble()
            val startLon = myLon.toDouble()
            val endLat = targetLat.toDouble()
            val endLon = targetLon.toDouble()
            
            val bearing = BearingCalculator.calculateBearing(startLat, startLon, endLat, endLon)
            val distance = BearingCalculator.calculateDistance(startLat, startLon, endLat, endLon)
            val direction = BearingCalculator.calculateAntennaDirection(bearing, currentHeading.toDouble())
            
            onUpdate(
                String.format("%.1f°", bearing),
                String.format("%.1f km", distance),
                String.format("%.1f°", direction)
            )
        } catch (e: Exception) {
            // 忽略计算错误
        }
    }
}