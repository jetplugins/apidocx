package io.yapix.process.yapi.process;

import io.yapix.base.sdk.yapi.model.YapiInterface;
import io.yapix.base.sdk.yapi.model.YapiParameter;
import java.util.List;
import java.util.Objects;

public class YapiInterfaceModifyJudge {

    private YapiInterfaceModifyJudge() {
    }

    /**
     * 判断接口是否变更
     */
    public static boolean isModify(YapiInterface api1, YapiInterface api2) {
        // 简单参数比较
        boolean simpleEqual = Objects.equals(api1.getCatid(), api2.getCatid())
                && Objects.equals(api1.getTitle(), api2.getTitle())
                && Objects.equals(api1.getPath(), api2.getPath())
                && Objects.equals(api1.getMethod(), api2.getMethod())
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

    public static boolean compareParameters(List<YapiParameter> parameters1, List<YapiParameter> parameters2) {
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
            YapiParameter p1 = parameters1.get(i);
            YapiParameter p2 = parameters2.get(i);
            if (!p1.equals(p2)) {
                return false;
            }
        }
        return true;
    }
}
