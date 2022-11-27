package io.apidocx.handle.eolink.process;

import io.apidocx.base.sdk.eolink.EolinkClient;
import io.apidocx.base.sdk.eolink.model.ApiBase;
import io.apidocx.base.sdk.eolink.model.ApiGroup;
import io.apidocx.base.sdk.eolink.model.ApiInfo;
import io.apidocx.base.sdk.eolink.request.ApiSaveResponse;
import io.apidocx.base.sdk.eolink.request.GroupAddRequest;
import io.apidocx.base.util.BeanUtils;
import io.apidocx.model.Api;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Eolinker上传接口
 */
public class EolinkUploader {

    private final EolinkClient client;

    private final Map<String, Long> groupCache = new ConcurrentHashMap<>();

    public EolinkUploader(EolinkClient client) {
        this.client = client;
    }

    public ApiInfo upload(String projectId, Api api) {
        ApiInfo eapi = EolinkDataConvector.convert(projectId, api);
        ApiBase eapiBase = eapi.getBaseInfo();
        // 分组
        List<ApiGroup> groups = client.getGroupList(projectId);
        eapiBase.setGroupID(getOrCreateGroup(groups, projectId, eapiBase.getGroupName()));

        // 接口
        ApiInfo theApi = findOriginApi(projectId, eapi);
        if (theApi != null) {
            mergeApi(theApi, eapi);
        } else {
            theApi = eapi;
        }

        // 保存
        ApiSaveResponse apiSaveResponse = client.saveApi(projectId, theApi);
        theApi.getBaseInfo().setApiID(apiSaveResponse.getApiID());
        theApi.getBaseInfo().setGroupID(apiSaveResponse.getGroupID());
        return theApi;
    }

    private void mergeApi(ApiInfo theApi, ApiInfo eapi) {
        ApiBase baseInfo = theApi.getBaseInfo();
        BeanUtils.merge(theApi, eapi);
        BeanUtils.merge(baseInfo, eapi.getBaseInfo());
        theApi.setBaseInfo(baseInfo);
    }

    /**
     * 创建获取获取分组
     */
    private Long getOrCreateGroup(List<ApiGroup> groups, String projectId, String groupName) {
        return groupCache.computeIfAbsent(groupName, key -> {
            Optional<ApiGroup> groupOpt = groups.stream()
                    .filter(g -> g.getGroupDepth() == 1 && g.getGroupName().equals(groupName))
                    .findFirst();
            if (groupOpt.isPresent()) {
                return groupOpt.get().getGroupID();
            }
            GroupAddRequest request = new GroupAddRequest();
            request.setGroupName(groupName);
            request.setProjectHashKey(projectId);
            request.setParentGroupID("0");
            return client.createGroup(request);
        });
    }

    /**
     * 获取原接口信息
     */
    private ApiInfo findOriginApi(String projectId, ApiInfo eapi) {
        ApiBase api = eapi.getBaseInfo();
        List<ApiBase> apis = client.getApiList(projectId, eapi.getBaseInfo().getGroupID());

        // 比较条件: 接口标题， 路径，请求方式
        Optional<ApiBase> interfaceOpt = apis.stream()
                .filter(item -> Objects.equals(item.getApiName(), api.getApiName())
                        && Objects.equals(item.getApiURI(), api.getApiURI())
                        && Objects.equals(item.getApiRequestType(), api.getApiRequestType()))
                .findFirst();

        // 比较条件: 路径，请求方式
        if (!interfaceOpt.isPresent()) {
            interfaceOpt = apis.stream()
                    .filter(item -> Objects.equals(item.getApiURI(), api.getApiURI())
                            && Objects.equals(item.getApiRequestType(), api.getApiRequestType()))
                    .findFirst();
        }
        // 比较条件: 路径，请求方式
        if (!interfaceOpt.isPresent()) {
            interfaceOpt = apis.stream()
                    .filter(item -> Objects.equals(item.getApiName(), api.getApiName()))
                    .findFirst();
        }

        if (interfaceOpt.isPresent()) {
            return client.getApi(projectId, interfaceOpt.get().getApiID());
        }
        return null;
    }

}
