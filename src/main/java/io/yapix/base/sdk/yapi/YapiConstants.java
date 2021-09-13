package io.yapix.base.sdk.yapi;

/**
 * Yapi接口常量
 */
public interface YapiConstants {

    /**
     * 登录地址
     */
    String yapiLogin = "/api/user/login";

    /** 登录地址LDAP */
    String yapiLoginLdap = "/api/user/login_by_ldap";

    /**
     * 当前用户信息
     */
    String yapiUserStatus = "/api/user/status";

    /**
     * 获取项目基本信息
     */
    String yapiProjectGet = "/api/project/get";

    /**
     * 新增或者更新接口
     */
    String yapiSave = "/api/interface/save";

    /**
     * 新增接口分类
     */
    String yapiAddCat = "/api/interface/add_cat";

    /**
     * 获取接口数据
     */
    String yapiGet = "/api/interface/get";

    /**
     * 获取菜单列表
     */
    String yapiCatMenu = "/api/interface/getCatMenu";

    /**
     * 获取某个分类下接口列表
     */
    String yapiListByCatId = "/api/interface/list_cat";
}
