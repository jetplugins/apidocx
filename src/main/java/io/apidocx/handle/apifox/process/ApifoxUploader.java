package io.apidocx.handle.apifox.process;

import io.apidocx.base.sdk.apifox.ApifoxClient;
import io.apidocx.base.sdk.apifox.model.ApiDetail;
import io.apidocx.base.sdk.apifox.model.ApiFolder;
import io.apidocx.base.sdk.apifox.model.ApiTreeItem;
import io.apidocx.base.sdk.apifox.model.CreateFolderRequest;
import io.apidocx.model.Api;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Apifox上传
 */
public class ApifoxUploader {

    private final ApifoxClient client;
    private final Map<String, Long> folderCache = new ConcurrentHashMap<>();

    public ApifoxUploader(ApifoxClient client) {
        this.client = client;
    }

    public Long upload(Long projectId, Api api) {
        ApiDetail data = new ApifoxDataConvector().convert(projectId, api);
        Long folderId = getOrCreateFolder(projectId, api.getCategory());
        data.setFolderId(folderId);
        return saveApi(api, data);
    }

    private Long saveApi(Api api, ApiDetail data) {
        ApiDetail originalApi = getApiDetail(data.getProjectId(), api.getCategory(), data.getName(), data.getMethod(), data.getPath());
        if (originalApi != null) {
            data.setId(originalApi.getId());
            data.setType(originalApi.getType());
            data.setStatus(originalApi.getStatus());
        }
        return client.saveApiDetail(data);
    }

    private ApiDetail getApiDetail(Long projectId, String category, String title, String method, String path) {
        List<ApiTreeItem> apiTreeList = client.getApiTreeList(projectId);
        List<ApiTreeItem> folders = apiTreeList.stream().filter(ApiTreeItem::isFolderType).collect(Collectors.toList());
        for (ApiTreeItem folder : folders) {
            if (folder.getChildren() == null || folder.getChildren().isEmpty()) {
                continue;
            }
            if (!Objects.equals(folder.getName(), category)) {
                continue;
            }
            ApiTreeItem apiItem = folder.getChildren().stream().filter(ApiTreeItem::isApiType)
                    .filter(item -> StringUtils.equals(item.getName(), title)
                            && StringUtils.equals(item.getApi().getMethod(), method)
                            && StringUtils.equals(item.getApi().getPath(), path))
                    .filter(item -> StringUtils.equals(item.getApi().getMethod(), method)
                            && StringUtils.equals(item.getApi().getPath(), path))
                    .filter(item -> StringUtils.equals(item.getName(), title))
                    .findFirst()
                    .orElse(null);
            if (apiItem != null) {
                return client.getApiDetail(apiItem.getApi().getId());
            }
        }
        return null;
    }

    private Long getOrCreateFolder(Long projectId, String name) {
        if (folderCache.containsKey(name)) {
            return folderCache.get(name);
        }
        List<ApiFolder> folders = client.getApiFolders(projectId);
        if (folders != null) {
            folders.stream()
                    .filter(f -> !f.isRoot())
                    .forEach(folder -> {
                        folderCache.put(folder.getName(), folder.getId());
                    });
        }
        return folderCache.computeIfAbsent(name, key -> {
            CreateFolderRequest createFolderRequest = CreateFolderRequest.builder()
                    .name(name)
                    .build();
            ApiFolder folder = client.createApiFolder(createFolderRequest);
            return folder.getId();
        });
    }
}
