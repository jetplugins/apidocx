package io.apidocx.base.sdk.eolink;

import feign.Feign;
import feign.Headers;
import feign.RequestLine;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import io.apidocx.base.sdk.eolink.request.ApiListRequest;
import io.apidocx.base.sdk.eolink.request.ApiListResponse;
import io.apidocx.base.sdk.eolink.request.ApiRequest;
import io.apidocx.base.sdk.eolink.request.ApiResponse;
import io.apidocx.base.sdk.eolink.request.ApiSaveResponse;
import io.apidocx.base.sdk.eolink.request.GetUserInfoResponse;
import io.apidocx.base.sdk.eolink.request.GroupAddRequest;
import io.apidocx.base.sdk.eolink.request.GroupAddResponse;
import io.apidocx.base.sdk.eolink.request.GroupListRequest;
import io.apidocx.base.sdk.eolink.request.GroupListResponse;
import io.apidocx.base.sdk.eolink.request.LoginRequest;
import io.apidocx.base.sdk.eolink.request.LoginResponseData;
import io.apidocx.base.sdk.eolink.request.SsoResponse;
import java.net.URI;
import java.util.Map;


@Headers("Content-Type: application/x-www-form-urlencoded")
public interface EolinkApi {

    static Feign.Builder feignBuilder() {
        return Feign.builder()
                .encoder(new FormEncoder(new GsonEncoder()))
                .decoder(new GsonDecoder());
    }

    /**
     * 登录
     */
    @RequestLine("POST")
    @Headers("Content-Type: application/json")
    SsoResponse<LoginResponseData> login(URI uri, LoginRequest request);

    /**
     * 获取当前登录用户信息
     */
    @RequestLine("GET /api/common/User/getUserInfo")
    GetUserInfoResponse getCurrentUserInfo();

    /**
     * 创建分组
     */
    @RequestLine("POST /api/generalFunction/Group/addGroup")
    GroupAddResponse createGroup(GroupAddRequest request);

    /**
     * 获取分组列表
     */
    @RequestLine("GET /api/apiManagementPro/ApiGroup/getApiGroupData")
    GroupListResponse getGroupList(GroupListRequest request);

    /**
     * 获取接口列表
     */
    @RequestLine("GET /api/apiManagementPro/Api/getApiListByCondition")
    ApiListResponse getApiList(ApiListRequest request);

    /**
     * 获取接口信息
     */
    @RequestLine("GET /api/apiManagementPro/Api/getApi")
    ApiResponse getApi(ApiRequest request);

    /**
     * 创建接口
     */
    @RequestLine("POST /api/apiManagementPro/Api/addApi")
    ApiSaveResponse createApi(Map<String, ?> params);

    /**
     * 更新接口
     */
    @RequestLine("POST /api/apiManagementPro/Api/editApi")
    ApiSaveResponse updateApi(Map<String, ?> params);
}
