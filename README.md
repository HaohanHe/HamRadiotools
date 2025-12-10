# <img src="app/src/main/ic_launcher-playstore.png" alt="Logo" width="40" height="40" align="middle" /> HamRadioTools

一款专为业余无线电爱好者设计的Android工具应用，提供全方位的无线电通信辅助功能。

## 📱 应用简介

<div align="center">
</div>

HamRadioTools 是一款功能强大的业余无线电辅助工具，旨在为火腿爱好者提供便捷的方位角计算、地图集成和梅登黑德定位系统转换等实用功能。

## ✨ 核心功能

### 1. 天线指向计算器
- **实时位置获取**：自动获取当前GPS位置，确保计算准确性
- **精确方位角计算**：输入目标经纬度，获取精确的方位角和大圆距离
- **天线指向可视化**：直观显示天线应指向的方向，配备指南针功能
- **方位辅助**：将方位角转换为直观的方向文本（如N、NE、E等16个方位）

### 2. 多地图集成
- **支持多种主流地图**：
  - Google Maps
  - 高德地图
  - 腾讯地图
  - 百度地图
- **一键跳转**：生成地图链接，直接打开对应地图应用查看位置或导航
- **当前位置与目标位置**：同时支持查看自己的位置和目标位置

### 3. 梅登黑德定位系统
- **双向转换**：经纬度与梅登黑德网格坐标（如BL12xx）之间的相互转换
- **高精度支持**：支持6位精度的梅登黑德网格坐标
- **实时转换**：自动获取当前位置并转换为梅登黑德坐标

## 🛠️ 技术特点

- **基于Jetpack Compose构建的现代化UI**
- **Kotlin语言开发，性能优越**
- **响应式设计，适配各种屏幕尺寸**
- **权限管理优化，保护用户隐私**
- **高效的计算算法，包括半正矢公式计算距离**

## 📋 系统要求

- Android 8.0 (API 26) 及以上版本
- 设备需配备GPS和指南针传感器
- 建议在户外或开阔环境中使用以获得最佳定位效果

## 🚀 快速开始

### 安装

1. 从GitHub仓库克隆或下载项目
2. 在Android Studio中打开项目
3. 构建并运行到您的Android设备或模拟器

### 基本使用

1. **天线指向计算**
   - 进入天线指向计算界面
   - 授予位置权限，等待获取当前位置
   - 输入目标位置的经纬度
   - 查看计算结果并调整天线

2. **地图导航**
   - 进入地图集成界面
   - 输入目标经纬度或梅登黑德坐标
   - 选择地图应用并点击跳转

3. **梅登黑德转换**
   - 进入梅登黑德定位界面
   - 自动获取位置或手动输入坐标
   - 查看转换结果

## 🔧 代码结构

```
app/src/main/java/top/hsyscn/hamradiotools/
├── MainActivity.kt         # 主界面和导航
├── utils/
│   ├── BearingCalculator.kt       # 方位角计算器
│   ├── LocationManager.kt         # 位置管理
│   ├── CompassManager.kt          # 指南针管理
│   ├── MaidenheadLocator.kt       # 梅登黑德定位系统
│   └── MapLinkGenerator.kt        # 地图链接生成器
└── ui/
    └── theme/                     # UI主题设置
```

## ⚠️ 注意事项

- 确保设备有内置指南针传感器并校准
- 在户外或远离强磁场的环境中使用指南针功能
- 远离正在发射的电台，避免电磁干扰影响指南针精度
- 长时间使用位置服务可能会消耗较多电量
- 在建筑物内或信号较弱区域，GPS定位可能不够准确

## 📝 版本历史

- **v1.0**：初始版本，包含天线指向计算、地图集成和梅登黑德定位转换功能
 **v1.1**：上新，多色彩，多语言，多选择



## 📬 联系与反馈

如有任何问题或建议，请随时联系我们：

- GitHub: [HaohanHe](https://github.com/HaohanHe)
- 美好的73送给各位友台，
小米手环pro系列，红米手表4以上等Vela设备请下载[Hrt-for-Vela](https://github.com/HaohanHe/Hrt-for-Vela/tree/main)
---

<div align="center">
  <p>Powered By BI4MIB</p>
  <p><a href="https://github.com/HaohanHe/HamRadiotools"><img src="https://img.shields.io/github/stars/HaohanHe/HamRadiotools.svg?style=social&label=Star" alt="GitHub stars" /></a></p>
</div>
