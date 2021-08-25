# Yapi X
一键生成接口文档, 上传到Yapi, Rap2, Eolinker等.

- [x] 一键从源代码生成api文档
- [x] 上传到Yapi平台
- [x] 上传到Rap2平台
- [x] 上传到Eolinker平台

<img src="screenshots.gif" height="360">

原理：这是一个Idea插件，从源码解析标准Javadoc文档, 并结合spring等常见注解.

## 轮子
一直在使用[YapiUpload](https://github.com/diwand/YapiIdeaUploadPlugin) 插件生成文档非常方便，
鉴于该项目维护缓慢，并在功能设计上与个人理念存在差异，因此决定重写该项目。

- 支持任意泛型、批量上传
- 支持账户密码登录
- 几乎兼容YapiUpload规则
- 代码逻辑简单，易于二次开发
- ...

## 使用
1. 安装: 打开Idea -> File -> Settings -> Plugins, 搜索: `Yapi X`
2. 配置: 项目根目录创建`.yapix`文件, 内容: `yapiProjectId=110`
3. 上传: 选中你的控制类或方法，右键执行: `Upload To Yapi`

## 配置
```properties
# 项目id
yapiProjectId=
rap2ProjectId=
eolinkerProjectId=
# 包装类全称: 场景是有配置spring全局过滤器包装统一返回值的情况
returnWrapType=
# 解包装类型全称: 场景是有些返回类型不希望被统一spring过滤器包装
returnUnwrapTypes=
# 参数忽略类型全称: 场景是自定义注入了一些参数类型不是由客户端上传
parameterIgnoreTypes=
```
备注: 对于list类型值支持英文逗号分隔，并允许空格.

## 贡献
欢迎提交需求，发现问题，提交代码来参与贡献。
