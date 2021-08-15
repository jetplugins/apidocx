package io.yapix.process.yapi;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.yapix.base.sdk.yapi.YapiClient;
import io.yapix.base.sdk.yapi.YapiException;
import io.yapix.base.sdk.yapi.mode.InterfaceVo;
import io.yapix.base.sdk.yapi.mode.YapiCategory;
import io.yapix.base.sdk.yapi.mode.YapiCategoryAddRequest;
import io.yapix.base.sdk.yapi.mode.YapiInterface;
import io.yapix.base.sdk.yapi.mode.YapiInterfaceStatus;
import io.yapix.base.sdk.yapi.mode.YapiListInterfaceResponse;
import io.yapix.model.Api;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Yapi上传
 */
public class YapiUploader {

    private final YapiClient client;
    private final Gson gson = new Gson();
    private final Map<String, Integer> menuCatIdCache = new ConcurrentHashMap<>();

    public YapiUploader(YapiClient client) {
        this.client = client;
    }

    public YapiInterface upload(Integer projectId, Api api) {
        YapiInterface data = YapiDataConvector.convert(projectId, api);

        Integer categoryId = getCatIdOrCreate(data.getProjectId(), data.getMenu());
        data.setCatid(String.valueOf(categoryId));
        addOrUpdate(data);
        return data;
    }


    /**
     * 获取或者创建分类
     */
    public Integer getCatIdOrCreate(Integer projectId, String menu) {
        Integer catId = menuCatIdCache.get(menu);
        if (catId != null) {
            return catId;
        }
        try {
            List<YapiCategory> list = client.getCategories(projectId);
            String[] menus = menu.split("/");
            // 循环多级菜单，判断是否存在，如果不存在就创建
            //  解决多级菜单创建问题
            Integer parent_id = -1;
            Integer now_id = null;
            for (int i = 0; i < menus.length; i++) {
                if (Strings.isNullOrEmpty(menus[i])) {
                    continue;
                }
                boolean needAdd = true;
                now_id = null;
                for (YapiCategory yapiCategory : list) {
                    if (yapiCategory.getName().equals(menus[i])) {
                        needAdd = false;
                        now_id = yapiCategory.getId();
                        break;
                    }
                }
                if (needAdd) {
                    now_id = this.addCategory(projectId, parent_id, menus[i]);
                }
                if (i == (menus.length - 1)) {
                    catId = now_id;
                } else {
                    parent_id = now_id;
                }
            }
        } catch (YapiException e) {
            //出现这种情况可能是yapi 版本不支持
        }
        if (catId == null) {
            catId = addCategory(projectId, -1, menu);
        }
        if (catId != null) {
            menuCatIdCache.put(menu, catId);
        }
        return catId;
    }

    /**
     * 创建或更新接口
     * <p>
     * 判断接口是否存在，不存在直接添加，存在检查接口状态完成不去更新，未完成则更新
     */
    private void addOrUpdate(YapiInterface api) {
        YapiInterface originApi = findOldParamByTitle(api);
        if (originApi != null) {
            mergeInterface(api, originApi);
        }
        boolean isDone = isInterfaceDone(api);
        if (!isDone) {
            client.saveInterface(api);
        }
    }

    private boolean isInterfaceDone(YapiInterface api) {
        YapiListInterfaceResponse listResponse = client.listInterfaceByCat(api.getCatid(), 1, 1000);
        List<InterfaceVo> apis = listResponse.getList();
        if (apis == null || apis.size() == 0) {
            return false;
        }
        for (InterfaceVo interfaceVo : apis) {
            if (api.getPath().equals(interfaceVo.getPath()) && api.getMethod()
                    .equals(interfaceVo.getMethod())) {
                api.setId(String.valueOf(interfaceVo.getId()));
                return YapiInterfaceStatus.done.name().equals(interfaceVo.getStatus());
            }
        }
        return false;
    }

    private void mergeInterface(YapiInterface newApi, YapiInterface oldParam) {
        if (!newApi.getResBodyType().equals(oldParam.getReqBodyType()) || !newApi.getResBodyType().equals("json")) {
            return;
        }
        JsonObject newObject = gson.fromJson(newApi.getResBody(), JsonObject.class);
        JsonObject oldObject = gson.fromJson(oldParam.getResBody(), JsonObject.class);
        recursionMock(newObject, oldObject);
        newApi.setResBody(gson.toJson(newObject));
    }

    private void recursionMock(JsonObject newObject, JsonObject oldObject) {
        if (newObject == null || oldObject == null) {
            return;
        }
        if (oldObject.get("type").getAsString().equals("object")) {
            JsonObject newProperties = (JsonObject) newObject.get("properties");
            JsonObject oldProperties = (JsonObject) oldObject.get("properties");
            if (oldProperties != null && newProperties != null) {
                for (String key : oldProperties.keySet()) {
                    if (newProperties.has(key)) {
                        recursionMock((JsonObject) newProperties.get(key), (JsonObject) oldProperties.get(key));
                    }
                }
            }
        } else if (oldObject.get("type").getAsString().equals("array")) {
            recursionMock((JsonObject) newObject.get("items"), (JsonObject) oldObject.get("items"));
        } else if (oldObject.has("mock") && oldObject.get("type").getAsString()
                .equals(newObject.get("type").getAsString())) {
            try {
                newObject.remove("mock");
            } catch (Exception e) {
                e.printStackTrace();
            }
            newObject.add("mock", oldObject.get("mock"));
        }
    }

    public YapiInterface findOldParamByTitle(YapiInterface yapiInterface) {
        YapiListInterfaceResponse interfacesList = client.listInterfaceByCat(yapiInterface.getCatid(), 1, 1000);
        for (InterfaceVo interfaceVo : interfacesList.getList()) {
            if (interfaceVo.getTitle().equals(yapiInterface.getTitle())) {
                return client.getInterface(interfaceVo.getId());
            }
        }
        return null;
    }


    /**
     * 创建分类
     */
    private Integer addCategory(Integer projectId, Integer parent_id, String menu) {
        YapiCategoryAddRequest req = new YapiCategoryAddRequest(menu, projectId, parent_id);
        YapiCategory category = client.addCategory(req);
        return category.getId();
    }


}
