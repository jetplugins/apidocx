package io.yapix.base.sdk.rap2.request;

/**
 * 接口更新请求参数
 */
public class InterfaceUpdateRequest {

    /** 接口id */
    private Long id;

    /** 名称 */
    private String name;

    /** 请求地址 */
    private String url;

    /** 请求方式 */
    private String method;

    /** 描述 */
    private String description;

    /** 状态 */
    private int status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
