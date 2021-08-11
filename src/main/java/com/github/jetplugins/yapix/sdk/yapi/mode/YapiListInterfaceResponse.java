package com.github.jetplugins.yapix.sdk.yapi.mode;

import java.util.List;

/**
 * 获取接口列表响应参数
 */
public class YapiListInterfaceResponse {

    private Integer count;

    private Integer total;

    private List<InterfaceVo> list;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<InterfaceVo> getList() {
        return list;
    }

    public void setList(List<InterfaceVo> list) {
        this.list = list;
    }
}
