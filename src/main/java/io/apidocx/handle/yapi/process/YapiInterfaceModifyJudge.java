package io.apidocx.handle.yapi.process;

import com.google.common.collect.Sets;
import io.apidocx.base.sdk.yapi.model.ApiInterface;
import io.apidocx.base.sdk.yapi.model.ApiParameter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class YapiInterfaceModifyJudge {

    private YapiInterfaceModifyJudge() {
    }

    /**
     * 判断接口是否变更
     */
    public static boolean isModify(ApiInterface api1, ApiInterface api2) {
        // 简单参数比较
        boolean simpleEqual = Objects.equals(api1.getCatid(), api2.getCatid())
                && Objects.equals(api1.getTitle(), api2.getTitle())
                && Objects.equals(api1.getPath(), api2.getPath())
                && Objects.equals(api1.getMethod(), api2.getMethod())
                && compareSet(api1.getTag(), api2.getTag())
                && (api1.isReqBodyIsJsonSchema() == api2.isReqBodyIsJsonSchema() || Objects.equals(
                api1.getReqBodyType(), api2.getReqBodyType()))
                && Objects.equals(api1.getReqBodyOther(), api2.getReqBodyOther())
                && Objects.equals(api1.getStatus(), api2.getStatus())
                && Objects.equals(api1.getResBodyType(), api2.getResBodyType())
                && Objects.equals(api1.getResBody(), api2.getResBody())
                && api1.isResBodyIsJsonSchema() == api2.isResBodyIsJsonSchema()
                && Objects.equals(api1.getDesc(), api2.getDesc());
        if (!simpleEqual) {
            return true;
        }

        // 数组参数比较
        boolean parametersEqual = compareParameters(api1.getReqParams(), api2.getReqParams())
                && compareParameters(api1.getReqQuery(), api2.getReqQuery())
                && compareParameters(api1.getReqBodyForm(), api2.getReqBodyForm());
        return !parametersEqual;
    }

    private static <T> boolean compareSet(Collection<T> source, Collection<T> target) {
        Set<T> set1 = source == null ? Collections.emptySet()
                : source instanceof Set ? (Set<T>) source : Sets.newHashSet(source);
        Set<T> set2 = target == null ? Collections.emptySet()
                : target instanceof Set ? (Set<T>) target : Sets.newHashSet(target);
        return set1.size() == set2.size() && set1.containsAll(set2);
    }

    public static boolean compareParameters(List<ApiParameter> parameters1, List<ApiParameter> parameters2) {
        int size1 = parameters1 != null ? parameters1.size() : 0;
        int size2 = parameters2 != null ? parameters2.size() : 0;
        if (size1 != size2) {
            return false;
        }
        if (size1 == 0) {
            return true;
        }
        // 参数值比较
        for (int i = 0; i < parameters1.size(); i++) {
            ApiParameter p1 = parameters1.get(i);
            ApiParameter p2 = parameters2.get(i);
            if (!p1.equals(p2)) {
                return false;
            }
        }
        return true;
    }
}
