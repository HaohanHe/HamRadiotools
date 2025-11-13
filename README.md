# Hrt-for-Vela 项目

<div align="center">
  <img src="./src/common/logo.png" alt="项目Logo" width="120" height="120">
  <h3>基于小米快应用Vela平台开发的Hrt项目</h3>
</div>

## 快速上手

### 1. 开发

```bash
npm install
npm run start
```

执行以上命令安装依赖并启动开发服务器。

### 2. 构建

```bash
npm run build
npm run release
```

执行构建命令生成生产环境代码，然后发布到快应用平台。

### 3. 调试

```bash
npm run watch
```

启动监视模式，可以实时查看代码修改效果。

### 4. 代码规范化配置

代码规范化可以帮助开发者在git commit前进行代码校验、格式化、commit信息校验。

**使用前提**：必须先关联git仓库

macOS 或 Linux 系统：
```bash
sh husky.sh
```

Windows 系统：
```bash
./husky.sh
```

## 了解更多

你可以通过我们的[官方文档](https://iot.mi.com/vela/quickapp)熟悉和了解快应用。

## 项目结构

```
src/
├── app.ux             # 应用入口文件
├── common/            # 公共资源
│   └── logo.png       # 项目Logo
├── i18n/              # 国际化资源
│   ├── defaults.json
│   ├── en.json
│   └── zh-CN.json
├── manifest.json      # 应用配置文件
└── pages/             # 页面目录
    ├── detail/        # 详情页
    └── index/         # 首页
```

## 开发规范

- 遵循 ESLint 和 Prettier 规范
- 使用 commitlint 进行提交信息校验
- 使用 husky 进行 git hooks 管理

## 许可证

本项目仅供学习和开发使用。
