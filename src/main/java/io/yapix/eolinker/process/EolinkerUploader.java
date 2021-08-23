package io.yapix.eolinker.process;

import io.yapix.base.sdk.eolinker.EolinkerClient;
import io.yapix.base.sdk.eolinker.model.EolinkerApiBase;
import io.yapix.base.sdk.eolinker.model.EolinkerApiGroup;
import io.yapix.base.sdk.eolinker.model.EolinkerApiInfo;
import io.yapix.base.sdk.eolinker.request.ApiSaveResponse;
import io.yapix.base.sdk.eolinker.request.GroupAddRequest;
import io.yapix.base.util.BeanUtils;
import io.yapix.model.Api;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Eolinker上传接口
 */
public class EolinkerUploader {

    private final EolinkerClient client;

    private final Map<String, Long> groupCache = new ConcurrentHashMap<>();

    public EolinkerUploader(EolinkerClient client) {
        this.client = client;
    }

    public EolinkerApiInfo upload(String projectId, Api api) {
        EolinkerApiInfo eapi = EolinkerDataConvector.convert(projectId, api);
        EolinkerApiBase eapiBase = eapi.getBaseInfo();
        // 分组
        List<EolinkerApiGroup> groups = client.getGroupList(projectId);
        eapiBase.setGroupID(getOrCreateGroup(groups, projectId, eapiBase.getGroupName()));

        // 接口
        EolinkerApiInfo theApi = findOriginApi(projectId, eapi);
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

    private void mergeApi(EolinkerApiInfo theApi, EolinkerApiInfo eapi) {
        EolinkerApiBase baseInfo = theApi.getBaseInfo();
        BeanUtils.merge(theApi, eapi);
        BeanUtils.merge(baseInfo, eapi.getBaseInfo());
        theApi.setBaseInfo(baseInfo);
    }

    /**
     * 创建获取获取分组
     */
    private Long getOrCreateGroup(List<EolinkerApiGroup> groups, String projectId, String groupName) {
        return groupCache.computeIfAbsent(groupName, key -> {
            Optional<EolinkerApiGroup> groupOpt = groups.stream()
                    .filter(g -> g.getGroupDepth() == 1 && g.getGroupName().equals(groupName))
                    .findFirst();
            if (groupOpt.isPresent()) {
                return groupOpt.get().getGroupID();
            }
            GroupAddRequest request = new GroupAddRequest();
            request.setGroupName(groupName);
            request.setProjectHashKey(projectId);
            request.setParentGroupID("0");
            return client.addGroup(request);
        });
    }

    /**
     * 获取原接口信息
     */
    private EolinkerApiInfo findOriginApi(String projectId, EolinkerApiInfo eapi) {
        EolinkerApiBase api = eapi.getBaseInfo();
        List<EolinkerApiBase> apis = client.getApiList(projectId, eapi.getBaseInfo().getGroupID());

        // 比较条件: 接口标题， 路径，请求方式
        Optional<EolinkerApiBase> interfaceOpt = apis.stream()
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
