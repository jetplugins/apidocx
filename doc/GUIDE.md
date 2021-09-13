# Yapi X 使用指南
## 开始
## 文档
## 平台
## 配置
大多数情况下您只需要配置关联的项目标识（xxxProjectId）即可，更多配置项可满足你的99%的场景。
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
# 自定义bean配置: 参见本项目resources/.yapix
beans[xxx]=<JSON of bean: BeanCustom>
# 自定义配置mock规则: 参见内置resource/.yapix
mockRules=<JSON Array: List<MockRule>
# 时间默认格式: @DateTimeFormat, @JsonFormat优先, 如果是数字类型值: integer
dateTimeFormatMvc=yyyy-MM-dd HH:mm:ss
dateTimeFormatJson=yyyy-MM-dd HH:mm:ss
```
备注: 对于list类型值支持英文逗号分隔、允许空格.
