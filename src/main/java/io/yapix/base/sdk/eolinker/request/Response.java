package io.yapix.base.sdk.eolinker.request;

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

    public static boolean isOkCode(String code) {
        return SUCCESS_CODES.contains(code);
    }
}
