package io.yapix.rap2.process;

import com.google.gson.Gson;
import io.yapix.base.sdk.rap2.Rap2Client;
import io.yapix.base.sdk.rap2.model.Rap2Interface;
import io.yapix.base.sdk.rap2.model.Rap2InterfaceBase;
import io.yapix.base.sdk.rap2.model.Rap2Module;
import io.yapix.base.sdk.rap2.model.Rap2Property;
import io.yapix.base.sdk.rap2.model.Rap2Repository;
import io.yapix.base.sdk.rap2.request.CreateModuleRequest;
import io.yapix.base.sdk.rap2.request.UpdatePropertiesRequest;
import io.yapix.base.sdk.rap2.request.UpdatePropertiesRequest.Summary;
import io.yapix.model.Api;
import java.util.List;
import java.util.Objects;

/**
 * Yapi上传
 */
public class Rap2Uploader {

    private final Rap2Client client;
    private final Gson gson = new Gson();

    public Rap2Uploader(Rap2Client client) {
        this.client = client;
    }

    public Rap2Interface upload(long repositoryId, Api api) {
        Rap2Interface rapApi = Rap2DataConvector.convert(Long.valueOf(repositoryId).intValue(), api);
        rapApi.setRepositoryId(repositoryId);

        Rap2Repository repository = client.getRepository(repositoryId);
        Rap2Module module = getOrCreateModule(repository, api.getCategory());
        rapApi.setModuleId(module.getId());
        // 创建基本接口
        Rap2InterfaceBase originRapApi = module.getInterfaces().stream()
                .filter(item -> Objects.equals(item.getUrl(), rapApi.getUrl()) && Objects
                        .equals(item.getMethod(), rapApi.getMethod()))
                .findFirst().orElse(null);
        if (originRapApi != null) {
            rapApi.setId(originRapApi.getId());
            client.updateInterface(rapApi);
        } else {
            originRapApi = client.createInterface(rapApi);
            rapApi.setId(originRapApi.getId());
            rapApi.setModuleId(originRapApi.getModuleId());
            rapApi.setRepositoryId(originRapApi.getRepositoryId());
        }
        setProperties(rapApi, rapApi.getProperties());
        setProperties(rapApi, rapApi.getRequestProperties());
        setProperties(rapApi, rapApi.getResponseProperties());

        UpdatePropertiesRequest propertiesRequest = new UpdatePropertiesRequest();
        propertiesRequest.setInterfaceId(rapApi.getId());
        propertiesRequest.setProperties(rapApi.getProperties());
        propertiesRequest.setSummary(new Summary(rapApi.getBodyOption(), 0));
        client.updateInterfaceProperties(propertiesRequest);
        return rapApi;
    }

    private void setProperties(Rap2Interface rapi, List<Rap2Property> properties) {
        if (properties == null) {
            return;
        }
        properties.forEach(p -> {
            p.setInterfaceId(rapi.getId());
            p.setModuleId(rapi.getModuleId());
            p.setRepositoryId(rapi.getRepositoryId());
        });
    }

    /**
     * 获取或创建模块
     */
    private Rap2Module getOrCreateModule(Rap2Repository repository, String category) {
        Rap2Module module = repository.getModules().stream().filter(m -> m.getName().equals(category)).findFirst()
                .orElse(null);
        if (module != null) {
            return module;
        }
        CreateModuleRequest create = new CreateModuleRequest();
        create.setRepositoryId(repository.getId());
        create.setName(category);
        return client.createModule(create);
    }

}
