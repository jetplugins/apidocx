package io.apidocx.base.sdk.eolink.request;

import com.google.common.collect.Sets;
import java.util.Set;
import lombok.Data;

@Data
public class Response {

    protected String statusCode;

    private static final Set<String> SUCCESS_CODES;

    static {
        SUCCESS_CODES = Sets.newHashSet("000000", "400500");
    }

    public boolean isSuccess() {
        return SUCCESS_CODES.contains(this.statusCode);
    }

    public boolean isNeedAuth() {
        return "200001".equals(statusCode);
    }

}
