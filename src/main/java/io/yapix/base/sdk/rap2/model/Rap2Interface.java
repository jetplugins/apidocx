package io.yapix.base.sdk.rap2.model;

import java.util.List;

/**
 * 接口信息
 */
public class Rap2Interface extends Rap2InterfaceBase {

    /** 所有参数请求和响应 */
    private List<Rap2Property> properties;

    /** 请求参数 */
    private List<Rap2Property> requestProperties;

    /** 响应参数 */
    private List<Rap2Property> responseProperties;

    public List<Rap2Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Rap2Property> properties) {
        this.properties = properties;
    }

    public List<Rap2Property> getRequestProperties() {
        return requestProperties;
    }

    public void setRequestProperties(List<Rap2Property> requestProperties) {
        this.requestProperties = requestProperties;
    }

    public List<Rap2Property> getResponseProperties() {
        return responseProperties;
    }

    public void setResponseProperties(List<Rap2Property> responseProperties) {
        this.responseProperties = responseProperties;
    }
}
