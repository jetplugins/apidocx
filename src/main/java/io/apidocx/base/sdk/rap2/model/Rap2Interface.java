package io.apidocx.base.sdk.rap2.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 接口信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Rap2Interface extends Rap2InterfaceBase {

    /** 所有参数请求和响应 */
    private List<Rap2Property> properties;

    /** 请求参数 */
    private List<Rap2Property> requestProperties;

    /** 响应参数 */
    private List<Rap2Property> responseProperties;

}
