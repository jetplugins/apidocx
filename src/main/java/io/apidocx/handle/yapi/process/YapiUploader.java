package io.apidocx.handle.yapi.process;

import com.google.common.base.Strings;
import io.apidocx.base.sdk.yapi.YapiClient;
import io.apidocx.base.sdk.yapi.YapiException;
import io.apidocx.base.sdk.yapi.model.ApiCategory;
import io.apidocx.base.sdk.yapi.model.ApiInterface;
import io.apidocx.base.sdk.yapi.model.ApiInterfaceVo;
import io.apidocx.base.sdk.yapi.model.CategoryCreateRequest;
import io.apidocx.base.sdk.yapi.model.ListInterfaceResponse;
import io.apidocx.model.Api;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Yapi上传
 */
public class YapiUploader {

    private final YapiClient client;
    private final Map<String, Integer> menuCatIdCache = new ConcurrentHashMap<>();

    public YapiUploader(YapiClient client) {
        this.client = client;
    }

    public ApiInterface upload(Integer projectId, Api api) {
        ApiInterface data = YapiDataConvector.convert(projectId, api);
        Integer categoryId = getCatIdOrCreate(data.getProjectId(), data.getMenu());
        data.setCatid(categoryId);
        addOrUpdate(data);
        return data;
    }


    /**
     * 获取或者创建分类
     */
    public Integer getCatIdOrCreate(Integer projectId, String menu) {
        return menuCatIdCache.computeIfAbsent(menu, key -> {
            Integer catId = menuCatIdCache.get(menu);
            if (catId != null) {
                return catId;
            }
            try {
                List<ApiCategory> list = client.getCategories(projectId);
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
                    for (ApiCategory apiCategory : list) {
                        if (apiCategory.getName().equals(menus[i])) {
                            needAdd = false;
                            now_id = apiCategory.getId();
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
            return catId;
        });
    }

    /**
     * 创建或更新接口
     */
    private void addOrUpdate(ApiInterface api) {
        ApiInterface originApi = findInterface(api);
        if (originApi != null) {
            api.setId(originApi.getId());
            if (!YapiInterfaceModifyJudge.isModify(originApi, api)) {
                return;
            }
        }
        client.saveInterface(api);
    }

    private ApiInterface findInterface(ApiInterface apiInterface) {
        // 比较: title + path + method
        ListInterfaceResponse interfacesList = client.listInterfaceByCat(apiInterface.getCatid(), 1, 1000);
        ApiInterfaceVo originInterface = interfacesList.getList().stream()
                .filter(o -> Objects.equals(o.getTitle(), apiInterface.getTitle())
                        && Objects.equals(o.getPath(), apiInterface.getPath())
                        && Objects.equals(o.getMethod(), apiInterface.getMethod()))
                .findFirst().orElse(null);
        // 比较: path + method
        if (originInterface == null) {
            originInterface = interfacesList.getList().stream()
                    .filter(o -> Objects.equals(o.getPath(), apiInterface.getPath())
                            && Objects.equals(o.getMethod(), apiInterface.getMethod()))
                    .findFirst().orElse(null);
        }
        // 比较: title
        if (originInterface == null) {
            originInterface = interfacesList.getList().stream()
                    .filter(o -> Objects.equals(o.getTitle(), apiInterface.getTitle()))
                    .findFirst().orElse(null);
        }
        if (originInterface != null) {
            return client.getInterface(originInterface.getId());
        }
        return null;
    }


    /**
     * 创建分类
     */
    private Integer addCategory(Integer projectId, Integer parent_id, String menu) {
        CategoryCreateRequest req = new CategoryCreateRequest(menu, projectId, parent_id);
        ApiCategory category = client.addCategory(req);
        return category.getId();
    }

}
