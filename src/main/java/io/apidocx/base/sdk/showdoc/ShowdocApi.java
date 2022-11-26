package io.apidocx.base.sdk.showdoc;

import feign.Feign;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import io.apidocx.base.sdk.showdoc.model.ShowdocProjectToken;
import io.apidocx.base.sdk.showdoc.model.ShowdocUpdateResponse;
import java.net.URI;
import java.util.Map;


public interface ShowdocApi {

    static Feign.Builder feignBuilder() {
        return Feign.builder()
                .encoder(new FormEncoder(new GsonEncoder()))
                .decoder(new GsonDecoder());
    }

    /**
     * 登录
     */
    @RequestLine("POST")
    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
            "Cookie: {captchaCookie}"
    })
    Response<?> login(URI uri, Map<String, ?> params, @Param("captchaCookie") String captchaCookie);

    /**
     * 获取当前登录用户信息
     */
    @RequestLine("GET")
    Response<?> getCurrentUserInfo(URI uri);

    /**
     * 获取当前登录用户信息
     */
    @RequestLine("GET")
    feign.Response getCaptcha(URI uri);

    /**
     * 获取项目授权token
     */
    @RequestLine("POST")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Response<ShowdocProjectToken> getProjectToken(URI uri, @Param("item_id") String itemId);

    /**
     * 保存文档
     */
    @RequestLine("POST")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Response<ShowdocUpdateResponse> savePage(URI uri, Map<String, ?> params);
}
