package io.apidocx.base.sdk.rap2.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthCookies {

    private String cookies;
    private long ttl;

}
