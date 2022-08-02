package io.yapix.base.sdk.showdoc.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthCookies {

    private String cookies;
    private long ttl;

}
