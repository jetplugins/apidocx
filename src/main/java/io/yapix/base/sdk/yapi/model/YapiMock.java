package io.yapix.base.sdk.yapi.model;

import lombok.Data;

@Data
public class YapiMock {

    private String mock;

    public YapiMock(String mock) {
        this.mock = mock;
    }

}
