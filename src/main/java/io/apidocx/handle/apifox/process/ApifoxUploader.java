package io.apidocx.handle.apifox.process;

import io.apidocx.base.sdk.apifox.ApifoxClient;
import io.apidocx.base.sdk.apifox.model.ApiDetail;
import io.apidocx.base.sdk.apifox.model.ApiFolder;
import io.apidocx.base.sdk.apifox.model.ApiTreeItem;
import io.apidocx.base.sdk.apifox.model.CreateFolderRequest;
import io.apidocx.model.Api;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
        api.setCategory(ApifoxUtils.folderPath(api.getCategory()));
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

    private ApiDetail getApiDetail(Long projectId, String folderPath, String title, String method, String path) {
        List<ApiTreeItem> apiTree = client.getApiTreeList(projectId);
        List<ApiTreeItem> apiTreeItems = ApifoxUtils.flatApiTree(apiTree);
        Map<String, List<ApiTreeItem>> folderApisMap = apiTreeItems.stream().collect(Collectors.groupingBy(ApiTreeItem::getFolderPath));
        List<ApiTreeItem> folderApis = folderApisMap.getOrDefault(folderPath, Collections.emptyList());
        // 同分类下: title + method + path
        ApiTreeItem apiItem = folderApis.stream().filter(ApiTreeItem::isApiType)
                .filter(item -> StringUtils.equals(item.getName(), title)
                        && StringUtils.equals(item.getApi().getMethod(), method)
                        && StringUtils.equals(item.getApi().getPath(), path))
                .findFirst()
                .orElse(null);

        if (apiItem == null) {
            // 所有api中精准匹配: title, method, path
            apiItem = apiTreeItems.stream().filter(ApiTreeItem::isApiType)
                    .filter(item -> StringUtils.equals(item.getName(), title)
                            && StringUtils.equals(item.getApi().getMethod(), method)
                            && StringUtils.equals(item.getApi().getPath(), path))
                    .findFirst()
                    .orElse(null);
        }

        if (apiItem == null) {
            // 同分类下: method+path, title
            apiItem = folderApis.stream().filter(ApiTreeItem::isApiType)
                    .filter(item -> (StringUtils.equals(item.getApi().getMethod(), method) && StringUtils.equals(item.getApi().getPath(), path))
                            || StringUtils.equals(item.getName(), title))
                    .findFirst()
                    .orElse(null);
        }

        if (apiItem != null) {
            return client.getApiDetail(apiItem.getApi().getId());
        }

        return null;
    }

    private Long getOrCreateFolder(Long projectId, String path) {
        if (folderCache.isEmpty()) {
            List<ApiTreeItem> apiTree = client.getApiTreeList(projectId);
            List<ApiTreeItem> flatFolders = ApifoxUtils.flatApiTree(apiTree).stream()
                    .filter(ApiTreeItem::isFolderType)
                    .collect(Collectors.toList());
            flatFolders.forEach(folder -> folderCache.put(folder.getFolderPath(), folder.getFolder().getId()));
        }
        return doGetOrCreateFolder(projectId, path);
    }

    private Long doGetOrCreateFolder(Long projectId, String path) {
        List<String> names = ApifoxUtils.splitFolderPaths(path);
        if (folderCache.containsKey(path)) {
            return folderCache.get(path);
        }

        return folderCache.computeIfAbsent(path, key -> {
            Long parentFolderId = 0L;
            String folderName = names.get(names.size() - 1);
            if (names.size() > 1) {
                String parentPath = ApifoxUtils.getFolderPath(names.subList(0, names.size() - 1));
                parentFolderId = doGetOrCreateFolder(projectId, parentPath);
            }

            CreateFolderRequest createFolderRequest = CreateFolderRequest.builder()
                    .name(folderName)
                    .parentId(parentFolderId)
                    .build();
            ApiFolder folder = client.createApiFolder(createFolderRequest);
            return folder.getId();
        });
    }
}
