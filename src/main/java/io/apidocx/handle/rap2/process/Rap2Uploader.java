package io.apidocx.handle.rap2.process;

import static java.util.Objects.nonNull;

import io.apidocx.base.sdk.rap2.Rap2Client;
import io.apidocx.base.sdk.rap2.dto.InterfacePropertiesUpdateRequest;
import io.apidocx.base.sdk.rap2.dto.InterfacePropertiesUpdateRequest.Summary;
import io.apidocx.base.sdk.rap2.dto.InterfaceUpdateRequest;
import io.apidocx.base.sdk.rap2.dto.ModuleCreateRequest;
import io.apidocx.base.sdk.rap2.model.Rap2Interface;
import io.apidocx.base.sdk.rap2.model.Rap2InterfaceBase;
import io.apidocx.base.sdk.rap2.model.Rap2Module;
import io.apidocx.base.sdk.rap2.model.Rap2Property;
import io.apidocx.base.sdk.rap2.model.Rap2Repository;
import io.apidocx.model.Api;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.collections.CollectionUtils;

/**
 * Rap2上传
 *
 * @see #upload(long, Api)
 */
public class Rap2Uploader {

    private final Rap2Client client;
    private final Map<String, Rap2Module> moduleCache = new ConcurrentHashMap<>();

    public Rap2Uploader(Rap2Client client) {
        this.client = client;
    }

    public Rap2Interface upload(long repositoryId, Api api) {
        Rap2Interface rapApi = Rap2DataConvector.convert(Long.valueOf(repositoryId).intValue(), api);
        rapApi.setRepositoryId(repositoryId);

        // 模块
        Rap2Repository repository = client.getRepository(repositoryId);
        Rap2Module module = getOrCreateModule(repository, api.getCategory());
        rapApi.setModuleId(module.getId());

        // 接口基本信息
        Rap2InterfaceBase originRapApi = findInterface(module, rapApi);
        boolean isModify = true;
        if (originRapApi != null) {
            Rap2Interface theOriginApi = client.getInterface(originRapApi.getId());
            isModify = Rap2InterfaceModifyJudge.isModify(theOriginApi, rapApi);
            if (isModify) {
                InterfaceUpdateRequest request = doConvertUpdateInterfaceRequest(rapApi, originRapApi);
                client.updateInterface(request);
            }
        } else {
            originRapApi = client.createInterface(rapApi);
        }
        rapApi.setId(originRapApi.getId());
        rapApi.setRepositoryId(originRapApi.getRepositoryId());

        // 接口参数信息
        if (isModify) {
            List<Rap2Property> properties =
                    nonNull(rapApi.getProperties()) ? rapApi.getProperties() : Collections.emptyList();
            properties.forEach(p -> {
                p.setInterfaceId(rapApi.getId());
                p.setModuleId(rapApi.getModuleId());
                p.setRepositoryId(rapApi.getRepositoryId());
            });
            InterfacePropertiesUpdateRequest propertiesRequest = new InterfacePropertiesUpdateRequest();
            propertiesRequest.setInterfaceId(rapApi.getId());
            propertiesRequest.setProperties(properties);
            propertiesRequest.setSummary(new Summary(rapApi.getBodyOption(), 0));
            client.updateInterfaceProperties(propertiesRequest);
        }
        return rapApi;
    }

    /**
     * 获取或创建模块
     */
    private Rap2Module getOrCreateModule(Rap2Repository repository, String category) {
        return moduleCache.computeIfAbsent(category, key -> {
            Rap2Module module = repository.getModules().stream().filter(m -> m.getName().equals(category)).findFirst()
                    .orElse(null);
            if (module != null) {
                return module;
            }
            ModuleCreateRequest create = new ModuleCreateRequest();
            create.setRepositoryId(repository.getId());
            create.setName(category);
            return client.createModule(create);
        });
    }

    /**
     * 获取原接口信息
     */
    private Rap2InterfaceBase findInterface(Rap2Module module, Rap2Interface rapApi) {
        if (CollectionUtils.isEmpty(module.getInterfaces())) {
            return null;
        }
        // 比较条件: 接口标题， 路径，请求方式
        Optional<Rap2InterfaceBase> interfaceOpt = module.getInterfaces().stream()
                .filter(item -> Objects.equals(item.getName(), rapApi.getName())
                        && Objects.equals(item.getUrl(), rapApi.getUrl())
                        && Objects.equals(item.getMethod(), rapApi.getMethod()))
                .findFirst();
        if (interfaceOpt.isPresent()) {
            return interfaceOpt.get();
        }

        // 比较条件: 路径，请求方式
        interfaceOpt = module.getInterfaces().stream()
                .filter(item -> Objects.equals(item.getUrl(), rapApi.getUrl())
                        && Objects.equals(item.getMethod(), rapApi.getMethod()))
                .findFirst();
        if (interfaceOpt.isPresent()) {
            return interfaceOpt.get();
        }

        // 比较条件: 标题
        interfaceOpt = module.getInterfaces().stream()
                .filter(item -> Objects.equals(item.getName(), rapApi.getName()))
                .findFirst();
        return interfaceOpt.orElse(null);
    }

    private InterfaceUpdateRequest doConvertUpdateInterfaceRequest(Rap2Interface rapApi,
            Rap2InterfaceBase originRapApi) {
        InterfaceUpdateRequest request = new InterfaceUpdateRequest();
        request.setId(originRapApi.getId());
        request.setName(rapApi.getName());
        request.setDescription(rapApi.getDescription());
        request.setMethod(rapApi.getMethod());
        request.setUrl(rapApi.getUrl());
        request.setStatus(rapApi.getStatus());
        return request;
    }

}
