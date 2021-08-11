package com.github.jetplugins.yapix.handle;

import com.github.jetplugins.yapix.constant.BodyTypeConstant;
import com.github.jetplugins.yapix.dto.YapiHeaderDTO;
import com.github.jetplugins.yapix.sdk.yapi.YapiClient;
import com.github.jetplugins.yapix.sdk.yapi.YapiConstants;
import com.github.jetplugins.yapix.sdk.yapi.YapiException;
import com.github.jetplugins.yapix.sdk.yapi.mode.InterfaceVo;
import com.github.jetplugins.yapix.sdk.yapi.mode.YapiCategory;
import com.github.jetplugins.yapix.sdk.yapi.mode.YapiCategoryAddRequest;
import com.github.jetplugins.yapix.sdk.yapi.mode.YapiInterface;
import com.github.jetplugins.yapix.sdk.yapi.mode.YapiInterfaceStatus;
import com.github.jetplugins.yapix.sdk.yapi.mode.YapiListInterfaceResponse;
import com.github.jetplugins.yapix.util.HttpClientUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

/**
 * Yapi上传
 */
public class YapiUploader {

    private final YapiClient client;
    private final Gson gson = new Gson();
    private final Map<String, Integer> menuCatIdCache = new HashMap<>();

    public YapiUploader(YapiClient client) {
        this.client = client;
    }

    /**
     * 上传接口
     */
    public void upload(YapiInterface api) throws YapiException {
        fillInterfaceValues(api);
        Integer categoryId = getCatIdOrCreate(api.getProjectId(), api.getMenu());
        api.setCatid(String.valueOf(categoryId));
        addOrUpdate(api);
    }

    /**
     * 填充接口默认值
     */
    private void fillInterfaceValues(YapiInterface api) {
        if (Strings.isNullOrEmpty(api.getTitle())) {
            api.setTitle(api.getPath());
        }
        // 如果缓存不存在，切自定义菜单为空，则使用默认目录
        if (Strings.isNullOrEmpty(api.getMenu())) {
            api.setMenu(YapiConstants.menu);
        }
        if (api.getReqHeaders() == null) {
            api.setReqHeaders(Lists.newArrayList());
        }

        YapiHeaderDTO contentType = new YapiHeaderDTO();
        if (StringUtils.isEmpty(api.getReqBodyType())) {
            contentType = null;
        } else if (BodyTypeConstant.FORM.equals(api.getReqBodyType())) {
            contentType.setName("Content-Type");
            contentType.setValue("application/x-www-form-urlencoded");
            api.setReqBodyForm(api.getReqBodyForm());
        } else if (BodyTypeConstant.JSON.equals(api.getReqBodyType())) {
            contentType.setName("Content-Type");
            contentType.setValue("application/json");
            api.setReqBodyType("json");
        } else {
            contentType.setName("Content-Type");
            contentType.setValue("application/x-www-form-urlencoded");
        }
        if (Objects.nonNull(contentType)) {
            api.getReqHeaders().add(contentType);
        }
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
        if (!newApi.getRes_body_type().equals(oldParam.getReqBodyType()) || !newApi.getRes_body_type().equals("json")) {
            return;
        }
        JsonObject newObject = gson.fromJson(newApi.getRes_body(), JsonObject.class);
        JsonObject oldObject = gson.fromJson(oldParam.getRes_body(), JsonObject.class);
        recursionMock(newObject, oldObject);
        newApi.setRes_body(gson.toJson(newObject));
    }

    private void recursionMock(JsonObject newObject, JsonObject oldObject) {
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
     * @description: 上传文件
     * @param: [url, filePath]
     * @return: java.lang.String
     * @author: chengsheng@qbb6.com
     * @date: 2019/5/15
     */
    public String uploadFile(String url, String filePath) {
        HttpPost httpPost = null;
        try {
            httpPost = new HttpPost(url);
            FileBody bin = new FileBody(new File(filePath));
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", bin).build();
            httpPost.setEntity(reqEntity);
            return HttpClientUtil.ObjectToString(HttpClientUtil.getHttpclient().execute(httpPost), "utf-8");
        } catch (Exception e) {
        }
        return "";
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
