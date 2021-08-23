# Yapi X
一键生成接口文档, 上传到yapi, rap2, eolinker等.

- [x] 一键从源代码生成api文档
- [x] 支持外部配置文件
- [x] 上传到Yapi平台
- [x] 上传到Rap2平台
- [x] 上传到Eolinker平台
- [ ] 生成markdown接口文档

<img src="screenshots.gif" height="360">

原理：这是一个Idea插件，从源码解析标准Javadoc文档, 并结合spring等常见注解.

## 轮子
我一直在使用[YapiUpload](https://github.com/diwand/YapiIdeaUploadPlugin) 插件生成文档非常方便，
在这之前一直维护公司分支版本，鉴于该开源项目维护缓慢，并在功能设计上与个人理念存在差异，因此决定重写该项目。

- 支持任意泛型
- 支持账户密码登录
- 批量上传，多线程上传
- 几乎兼容YapiUpload规则

## 使用
1. 安装插件: 打开Idea -> File -> Settings -> Plugins, 搜索: `Yapi X`
2. 项目配置: 项目根目录创建`.yapix`文件, 内容: `projectId=110`
3. 执行上传: 选中你的控制类或方法，右键执行: `Upload To Yapi`

## 配置文件(.yapix)
```properties
# 项目id
yapiProjectId=
rap2ProjectId=
eolinkerProjectId=
# 包装类全称: String
returnWrapType=
# 解包装类型全称: List<String>
returnUnwrapTypes=
# 参数忽略类型全称: List<String>
parameterIgnoreTypes=
```
备注: 对于list类型值支持英文逗号分隔，并允许空格.

## 规则
大多数情况下只需要使用标准的Javadoc就能非常完美的生成文档.
1. 标准的Javadoc生成   
    接口: GET /user/list  
    分类: 用户管理    
    接口: 获取用户列表  
    参数: @param注解, 参数实体中解析字段注释
    ```java
    /**
     * 用户管理
     */
    @RestController
    @RequestMapping("/user")
    public class UserController {

        /**
         * 获取用户列表
         * 
         * @param page 页码
         * @param size 每页大小
         */
        @GetMapping("/list")
        public User list(Integer page, Integer size) {}

        /**
         * 新增用户
         */
        @PostMapping("/add")
        public User add(@RequestBody UserAddRequest request) {}
    }
    ```
2. 验证框架注解
    ```
    @NotNull
    @NotEmpty
    @NotBlank
    ```
3. 其他spring注解
    ```
    @RequestParam
    @RequestHeader
    @PathVariable
    @RequestAttribute
    ```
