package io.yapix.base.sdk.rap2.model;

/**
 * 用户信息
 */
public class Rap2User {

    private Long id;

    private String fullname;

    private String email;

    public Rap2User() {
    }

    public Rap2User(Long id, String fullname, String email) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
    }

    public Rap2User(Long userId) {
        this.id = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
