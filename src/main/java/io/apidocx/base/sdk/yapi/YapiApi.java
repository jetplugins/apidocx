package io.apidocx.base.sdk.yapi;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import io.apidocx.base.sdk.yapi.model.ApiCategory;
import io.apidocx.base.sdk.yapi.model.ApiInterface;
import io.apidocx.base.sdk.yapi.model.CategoryCreateRequest;
import io.apidocx.base.sdk.yapi.model.CreateInterfaceResponseItem;
import io.apidocx.base.sdk.yapi.model.ListInterfaceResponse;
import io.apidocx.base.sdk.yapi.model.LoginRequest;
import java.util.List;


@Headers({
        "Content-Type: application/json",
})
public interface YapiApi {

    static Feign.Builder feignBuilder() {
        return Feign.builder()
                .encoder(new FormEncoder(new GsonEncoder()))
                .decoder(new GsonDecoder());
    }

    /**
     * 普通登录
     */
    @RequestLine("POST /api/user/login")
    Response<?> login(LoginRequest request);

    /**
     * LDAP登录
     */
    @RequestLine("POST /api/user/login_by_ldap")
    Response<?> loginLdap(LoginRequest request);

    /**
     * 获取接口分类列表
     */
    @RequestLine("GET /api/interface/getCatMenu?project_id={projectId}")
    Response<List<ApiCategory>> getCategories(@Param("projectId") Integer projectId);

    /**
     * 添加接口分类
     */
    @RequestLine("POST api/interface/add_cat")
    Response<ApiCategory> createCategory(CategoryCreateRequest request);


    /**
     * 获取接口信息
     */
    @RequestLine("GET /api/interface/get?id={id}")
    Response<ApiInterface> getInterface(@Param("id") Integer id);

    /**
     * 创建接口信息
     */
    @RequestLine("POST /api/interface/save")
    Response<List<CreateInterfaceResponseItem>> createInterface(ApiInterface api);

    /**
     * 修改接口信息
     */
    @RequestLine("POST /api/interface/up")
    Response<?> updateInterface(ApiInterface api);

    /**
     * 获取目录下接口列表
     */
    @RequestLine("GET /api/interface/list_cat?catid={catId}&page={page}&limit={limit}")
    Response<ListInterfaceResponse> listInterfaceByCategory(@Param("catId") Integer catId,
                                                            @Param("page") int page,
                                                            @Param("limit") int limit);

    @RequestLine("GET /api/project/get")
    Response<?> getProjects();

    @RequestLine("GET /api/user/status")
    Response<?> getUserStatus();
}
