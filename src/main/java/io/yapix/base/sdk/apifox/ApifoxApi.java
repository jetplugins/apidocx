package io.yapix.base.sdk.apifox;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.Request.Options;
import feign.RequestLine;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.yapix.base.sdk.apifox.model.ApiDetail;
import io.yapix.base.sdk.apifox.model.ApiFolder;
import io.yapix.base.sdk.apifox.model.ApiTreeItem;
import io.yapix.base.sdk.apifox.model.ApifoxResponse;
import io.yapix.base.sdk.apifox.model.CreateFolderRequest;
import io.yapix.base.sdk.apifox.model.LoginRequest;
import io.yapix.base.sdk.apifox.model.LoginResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Headers({
        "Content-Type: application/json",
        "X-Project-Id: {projectId}"
})
public interface ApifoxApi {

    static Feign.Builder feignBuilder() {
        return Feign.builder()
                .options(new Options(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, true))
                .encoder(new FormEncoder(new JacksonEncoder()))
                .decoder(new JacksonDecoder());
    }

    /**
     * 登录授权
     */
    @RequestLine("POST /api/v1/login")
    ApifoxResponse<LoginResponse> login(LoginRequest request);

    /**
     * 获取当前登录用户
     */
    @RequestLine("GET /api/v1/current-user")
    ApifoxResponse<?> getCurrentUser();

    /**
     * 获取接口树列表
     */
    @RequestLine("GET /api/v1/api-tree-list")
    ApifoxResponse<List<ApiTreeItem>> getApiTreeList(@Param("projectId") Long projectId);

    /**
     * 获取接口目录列表
     */
    @RequestLine("GET /api/v1/api-detail-folders")
    ApifoxResponse<List<ApiFolder>> getApiFolders(@Param("projectId") Long projectId);

    /**
     * 创建接口目录
     */
    @RequestLine("POST /api/v1/api-detail-folders")
    ApifoxResponse<ApiFolder> createApiFolders(CreateFolderRequest request);

    /**
     * 获取接口详情
     */
    @RequestLine("GET /api/v1/api-details/{id}")
    ApifoxResponse<ApiDetail> getApiDetail(@Param("id") Long id);

    /**
     * 创建接口
     */
    @RequestLine("POST /api/v1/api-details")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    ApifoxResponse<ApiDetail> createApiDetail(Map<String, ?> apiDetail);

    /**
     * 修改接口
     */
    @RequestLine("PUT /api/v1/api-details/{id}")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    ApifoxResponse<?> updateApiDetail(@Param("id") Long id, Map<String, ?> apiDetail);
}
