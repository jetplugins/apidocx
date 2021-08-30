package io.yapix.process.curl;

import static java.util.Objects.nonNull;

import io.yapix.base.util.PropertyUtils;
import io.yapix.model.Api;
import io.yapix.model.ParameterIn;
import io.yapix.model.Property;
import io.yapix.model.RequestBodyType;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * 接口信息生成curl命令
 */
public class CurlGenerator {

    /**
     * 生成curl字符串
     */
    public String generate(Api api) {
        StringBuilder sb = new StringBuilder("curl --location --request ");
        sb.append(api.getMethod().name()).append(" '").append(getUrl(api)).append("' \\\n");
        // 请求头
        for (Property p : api.getParametersByIn(ParameterIn.header)) {
            sb.append("--header '").append(p.getName()).append(": ' \\\n");
        }
        RequestBodyType bodyType = api.getRequestBodyType();
        if (bodyType != null && StringUtils.isNotEmpty(bodyType.getContentType())) {
            sb.append("--header '").append("Content-Type").append(": ").append(bodyType.getContentType())
                    .append("' \\\n");
        }
        // 表单数据
        for (Property p : api.getRequestBodyForm()) {
            sb.append("--data-urlencode '").append(p.getName())
                    .append("=").append(nonNull(p.getDefaultValue()) ? p.getDefaultValue() : "")
                    .append("' \\\n");
        }
        // 请求体
        if (bodyType == RequestBodyType.json && api.getRequestBody() != null) {
            sb.append("--data-raw '").append(PropertyUtils.getJsonExample(api.getRequestBody())).append("' \\\n");
        }
        if (sb.charAt(sb.length() - 2) == '\\') {
            sb.deleteCharAt(sb.length() - 2);
        }
        return sb.toString();
    }

    /**
     * 获取地址，包括参数拼接
     */
    private String getUrl(Api api) {
        List<Property> queries = api.getParametersByIn(ParameterIn.query);
        StringBuilder sb = new StringBuilder();
        sb.append("{{host}}");
        sb.append(api.getPath());
        if (queries.size() > 0) {
            sb.append("?");
            for (Property q : queries) {
                sb.append(q.getName())
                        .append("=").append(nonNull(q.getDefaultValue()) ? q.getDefaultValue() : "")
                        .append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
