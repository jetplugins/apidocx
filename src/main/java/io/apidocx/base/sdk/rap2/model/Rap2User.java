package io.apidocx.base.sdk.rap2.model;

import lombok.Data;

/**
 * 用户信息
 */
@Data
public class Rap2User {

    private Long id;

    private String fullname;

    private String email;

    public Rap2User() {
    }

    public Rap2User(Long id) {
        this.id = id;
    }

    public Rap2User(Long id, String fullname, String email) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
    }

}
