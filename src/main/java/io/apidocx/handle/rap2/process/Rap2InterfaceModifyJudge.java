package io.apidocx.handle.rap2.process;

import static java.util.Objects.nonNull;

import com.google.common.collect.Lists;
import io.apidocx.base.sdk.rap2.model.Rap2Interface;
import io.apidocx.base.sdk.rap2.model.Rap2Property;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class Rap2InterfaceModifyJudge {

    /**
     * 接口是否更新
     */
    public static boolean isModify(Rap2Interface originApi, Rap2Interface api) {
        // 比较基本信息
        boolean isBaseEqual = Objects.equals(api.getModuleId(), originApi.getModuleId())
                && Objects.equals(api.getName(), originApi.getName())
                && Objects.equals(api.getUrl(), originApi.getUrl())
                && Objects.equals(api.getMethod(), originApi.getMethod())
                && Objects.equals(api.getStatus(), originApi.getStatus())
                && Objects.equals(api.getBodyOption(), originApi.getBodyOption())
                && Objects.equals(api.getDescription(), originApi.getDescription());
        if (!isBaseEqual) {
            return true;
        }

        // 比较请求/响应参数
        List<Rap2Property> properties = Lists.newArrayList(
                nonNull(api.getProperties()) ? api.getProperties() : Collections.emptyList());
        List<Rap2Property> originApiProperties = Lists.newArrayList(
                nonNull(originApi.getProperties()) ? originApi.getProperties() : Collections.emptyList());
        if (properties.size() != originApiProperties.size()) {
            return true;
        }

        for (int i = 0; i < properties.size(); i++) {
            Rap2Property p1 = properties.get(i);
            Rap2Property p2 = originApiProperties.get(i);
            if (!isPropertyEquals(p1, p2)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPropertyEquals(Rap2Property p1, Rap2Property p2) {
        return Objects.equals(p1.getScope(), p2.getScope())
                && Objects.equals(p1.getPos(), p2.getPos())
                && Objects.equals(p1.getName(), p2.getName())
                && StringUtils.equalsIgnoreCase(p1.getType(), p2.getType())
                && Objects.equals(p1.getRule(), p2.getRule())
                && Objects.equals(p1.getValue(), p2.getValue())
                && Objects.equals(p1.getDescription(), p2.getDescription())
                && Objects.equals(p1.getRequired(), p2.getRequired());
    }
}
