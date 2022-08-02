package io.yapix.base.sdk.yapi;

import lombok.Data;

/**
 * yapi 返回结果
 *
 * @author chengsheng@qbb6.com
 * @date 2019/1/31 12:08 PM
 */
@Data
public class YapiResponse {

    /**
     * 状态码
     */
    private Integer errcode;
    /**
     * 状态信息
     */
    private String errmsg;
    /**
     * 返回结果
     */
    private Object data;
    /**
     * 分类
     */
    private String catId;

    public static YapiResponse ok() {
        YapiResponse response = new YapiResponse();
        response.setErrcode(0);
        return response;
    }

    public boolean isOk() {
        return errcode != null && errcode == 0;
    }

    public YapiResponse() {
        this.errcode = 0;
        this.errmsg = "success";
    }

    public YapiResponse(Object data) {
        this.errcode = 0;
        this.errmsg = "success";
        this.data = data;
    }

    public YapiResponse(Integer errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }
}
