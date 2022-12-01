# 使用指南
重要的事情说三遍！无依赖、无成本、开箱即用，只需要花几分钟了解下。

## 快速开始
1. 安装插件: [Apidocx](https://plugins.jetbrains.com/plugin/17425-yapi-x)
2. 选中上传的方法: 执行"Upload To YApi"

- 支持整个类、多个类、当个方法API文档上传
- 插件配置路径: File -> Settings -> Other Settings -> Apidocx

提示: 默认情况下右键菜单是"Upload To YApi", 可通过插件菜单配置为Rap2, Eolink等

## 文档生成规则
标准的Javadoc文档就能生成很好的API文档，对于生成文档的准确性有两个建议。
1. 文档注释: 类、方法、字段文档注释应完善
2. 使用实体类: 接收参数、响应参数，避免使用map等类型

| 目标 | 描述 |
| :---  | :--- |
| 接口分类 |  文档标记@menu > 类文档注释第一行(推荐)|
| 接口名称 |  文档标记@description > 方法文档注释第一行(推荐) |
| 字段名称 | 字段描述 （也兼容swagger） |
| 文档标记 @ignore | 标记的类、方法、字段会被忽略（有浸入性） |

## 平台
目前支持多个平台YApi, Rap2, Eolinker，包括公有部署和私有部署，支持账户密码登录。

#### YApi
对于YApi支持3种登录方式：默认, LDAP, 项目token, 推荐用账户密码方式，对于统一登录无法支持，请使用projectToken方式上传。
- YApi服务地址: 示范: http://yapi.smart-xwork.cn
- 获取项目id: http://yapi.smart-xwork.cn/project/35523/interface/api
- 获取项目token: 打开项目 -> 设置 -> token配置

#### Rap2
- 获取项目id: http://rap2.taobao.org/repository/editor?id=290411 , 290411

#### Eolinker
目前支持eolinker5.0版本，仅测试了官方提供的sass版本: https://www.eolink.com

#### ShowDoc

## 配置文件
大多数情况下您只需要配置关联的项目标识（xxxProjectId）即可，更多配置项可满足你的99%的场景。

- 文件目录：项目模块根目录
- 文件名称: .yapix
- 文件格式: properties
#### 配置项
| 名称                   | 类型                | 描述                                              | 备注                                        |
|:---------------------|:------------------|:------------------------------------------------|:------------------------------------------|
| yapiProjectId        | integer           | YApi项目id                                        |
| rap2ProjectId        | integer           | Rap2项目id                                        |
| eolinkerProjectId    | string            | Eolinker项目id                                    |
| showdocProjectId     | string            | ShowDoc项目id                                     |
|                      |                   |                                                 |
| yapiUrl              | string            | YApi服务地址                                        | 场景：插件无法支持YApi统一登录方式，此时可使用项目token方式        |
| yapiProjectToken     | string            | YApi项目访问token                                   |
|                      |                   |                                                 |
| strict               | boolean           | 是否开启严格模式, true(默认), false                       | 严格模式下不会解析无分类、无接口名的                        |
| path                 | string            | 全局接口路径前缀                                        | 严格模式下不会解析无分类、无接口名的                        |
|                      |                   |                                                 |
| returnWrapType       | string            | 方法返回值，统一包装类限定名                                  | 场景: spring统一配置了返回包装类                      |
| returnUnwrapTypes    | string            | 方法返回值，指定不需要包装的类                                 | 场景: 某些类不需要spring统一包装, 多个用英文逗号分割           |
| parameterIgnoreTypes | list&lt;string>   | 方法参数忽略的类全称                                      | 场景: 某些方法参数不是由浏览器客户端上传到                    |
| requestBodyParamType | string            | 简化请求参数json自定义注解（io.your.RequestBodyParam#value） | 场景: 自定义注解，实现简单json请求参数避免使用@RequestBody需要包装一个实体                    |
|                      |                   |                                                 |
| dateTimeFormatMvc    | string            | 默认返回时间格式(表单)                                    | 默认格式: yyyy-MM-dd HH:mm:ss, 时间轴配置: integer |
| dateTimeFormatJson   | string            | 默认返回时间格式(json)                                  | 默认格式: yyyy-MM-dd HH:mm:ss, 时间轴配置: integer |
| dateFormat           | string            | 默认返回时间格式(LocalDate)                             | 默认格式: yyyy-MM-dd                          |
| timeFormat           | string            | 默认返回时间格式(LocalTime)                             | 默认格式: HH:mm:ss                            |
|                      |                   |                                                 |
| beans[X]             | BeanCustom        | 自定义bean配置                                       |
| mockRules            | List&lt;MockRule> | 自定义mock规则                                       |

#### beans
- 概述: 自定义bean配置, Map类型: Key是类限定名, Value是配置项
- 场景: 引入的第三方包希望重写字段名称、备注等.
- 文档:
    ```
    {
    	"includes": [],		// 只包含的字段名
    	"excludes": [],		// 排除的字段名
    	"fileds": {
    		"pageNumber": { 	// 指定字段配置
    			"name": "",			// 参数名
    			"description": "",	// 参数描述
    			"type": "",			// 参数类型: boolean, integer, string, float, object
    			"required": true, 	// 是否必须
    			"mock": "", 		// mock: 参见mock.js
    			"defaultValue":"" 	// 默认值
    		}
    	}
    }
    ```
- 示例:
    ```properties
    beans[org.springframework.data.domain.Pageable]= { \
            "includes": ["pageNumber", "pageSize", "sort"], \
            "fields": { \
                "pageNumber": { "name": "page", "description": "页码" }, \
                "pageSize": { "name": "size", "description": "每页大小" }, \
                "sort": { "name": "sort", "type": "string", "description": "排序" } \
            } \
    }
    ```
### mockRules
- 概述: 根据业务特点可以自定义mock规则
- 文档:
    ```
    [{
    	"name": "图标",               // 备注
    	"type": "string",            // 匹配类型
    	"match": "avatar|icon",      // 匹配字段名: 正则表达式，忽略大小写
    	"mock": "@image('100x100')"  // mock值
    }]
    ```
