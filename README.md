# Yapi X
一键生成接口文档, 上传到yapi, rap2, eolinker等.

- [x] 一键从源代码生成api文档
- [x] 支持外部配置文件
- [x] 上传到Yapi平台
- [x] 上传到Rap2平台
- [ ] 上传到Eolinker平台
- [ ] 生成markdown接口文档

## 为什么要造轮子
我一直在使用[YapiUpload](https://github.com/diwand/YapiIdeaUploadPlugin) 插件生成文档非常方便，
在这之前一直维护公司分支版本，鉴于该开源项目维护缓慢，并在功能设计上与个人理念存在差异，因此决定重写该项目。

- 支持任意泛型
- 支持账户密码登录
- 批量上传，多线程上传
- 几乎兼容YapiUpload规则

## 使用
1. 安装插件: `Yapi X`
2. 项目根目录添加配置文件: `.yapix`, 内容: `projectId=110`
3. 选中你的控制类或方法，右键执行: `Upload To Yapi`

## 配置文件(.yapix)
```properties
# 项目id
projectId=
yapiProjectId=
rap2ProjectId=
# 包装类全称: String
returnWrapType=
# 解包装类型全称: List<String>
returnUnwrapTypes=
# 参数忽略类型全称: List<String>
parameterIgnoreTypes=
```
备注: 对于list类型值支持英文逗号分隔，并允许空格.
