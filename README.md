<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hrt-for-Vela 项目说明文档</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
            line-height: 1.6;
            color: #333;
            background-color: #f5f5f5;
            padding: 0;
            margin: 0;
        }
        
        .container {
            max-width: 900px;
            margin: 0 auto;
            background-color: white;
            padding: 40px;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
            min-height: 100vh;
        }
        
        h1 {
            font-size: 32px;
            color: #333;
            margin-bottom: 40px;
            text-align: center;
            padding-bottom: 20px;
            border-bottom: 1px solid #e8e8e8;
        }
        
        h2 {
            font-size: 24px;
            color: #1890ff;
            margin-top: 40px;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 1px solid #e8e8e8;
        }
        
        h3 {
            font-size: 18px;
            color: #666;
            margin-top: 30px;
            margin-bottom: 15px;
        }
        
        p {
            margin-bottom: 15px;
            color: #666;
        }
        
        pre {
            background-color: #f6f8fa;
            border: 1px solid #e8e8e8;
            border-radius: 6px;
            padding: 16px;
            overflow-x: auto;
            margin: 15px 0;
        }
        
        code {
            font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
            font-size: 14px;
            color: #e83e8c;
        }
        
        .section {
            margin-bottom: 40px;
        }
        
        a {
            color: #1890ff;
            text-decoration: none;
        }
        
        a:hover {
            text-decoration: underline;
        }
        
        .tip-box {
            background-color: #f0f5ff;
            border-left: 4px solid #1890ff;
            padding: 15px;
            margin: 20px 0;
            border-radius: 4px;
        }
        
        .code-title {
            background-color: #e6f7ff;
            padding: 8px 16px;
            border: 1px solid #91d5ff;
            border-bottom: none;
            border-top-left-radius: 6px;
            border-top-right-radius: 6px;
            color: #1890ff;
            font-size: 14px;
            font-weight: 500;
        }
        
        .code-wrapper {
            margin: 15px 0;
        }
        
        .code-wrapper pre {
            margin-top: 0;
            border-top-left-radius: 0;
            border-top-right-radius: 0;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Hrt-for-Vela 项目</h1>
        
        <div class="section">
            <h2>快速上手</h2>
            
            <h3>1. 开发</h3>
            <div class="code-wrapper">
                <div class="code-title">开发命令</div>
                <pre><code>npm install
npm run start</code></pre>
            </div>
            <p>执行以上命令安装依赖并启动开发服务器。</p>
            
            <h3>2. 构建</h3>
            <div class="code-wrapper">
                <div class="code-title">构建命令</div>
                <pre><code>npm run build
npm run release</code></pre>
            </div>
            <p>执行构建命令生成生产环境代码，然后发布到快应用平台。</p>
            
            <h3>3. 调试</h3>
            <div class="code-wrapper">
                <div class="code-title">调试命令</div>
                <pre><code>npm run watch</code></pre>
            </div>
            <p>启动监视模式，可以实时查看代码修改效果。</p>
            
            <h3>4. 代码规范化配置</h3>
            <p>代码规范化可以帮助开发者在git commit前进行代码校验、格式化、commit信息校验。</p>
            <div class="tip-box">
                <strong>使用前提：</strong>必须先关联git仓库
            </div>
            
            <p>macOS 或 Linux 系统：</p>
            <div class="code-wrapper">
                <pre><code>sh husky.sh</code></pre>
            </div>
            
            <p>Windows 系统：</p>
            <div class="code-wrapper">
                <pre><code>./husky.sh</code></pre>
            </div>
        </div>
        
        <div class="section">
            <h2>了解更多</h2>
            <p>你可以通过我们的<a href="https://iot.mi.com/vela/quickapp" target="_blank">官方文档</a>熟悉和了解快应用。</p>
        </div>
        
        <div class="section">
            <div class="tip-box">
                <p><strong>关于项目：</strong>这是一个基于小米快应用Vela平台开发的Hrt项目。</p>
            </div>
        </div>
    </div>
</body>
</html>
