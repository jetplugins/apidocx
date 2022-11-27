package io.apidocx.handle.curl;

import static java.util.Objects.nonNull;

import io.apidocx.base.util.PropertyUtils;
import io.apidocx.model.Api;
import io.apidocx.model.ParameterIn;
import io.apidocx.model.Property;
import io.apidocx.model.RequestBodyType;
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
        final String lineEnd = "' \\\n";
        StringBuilder sb = new StringBuilder("curl --location --request ");
        sb.append(api.getMethod().name()).append(" '").append(escape(getUrl(api))).append(lineEnd);
        // 请求头
        for (Property p : api.getParametersByIn(ParameterIn.header)) {
            sb.append("--header '").append(escape(p.getName())).append(": ").append(lineEnd);
        }
        RequestBodyType bodyType = api.getRequestBodyType();
        if (bodyType != null && StringUtils.isNotEmpty(bodyType.getContentType())) {
            sb.append("--header '")
                    .append("Content-Type").append(": ").append(escape(bodyType.getContentType()))
                    .append(lineEnd);
        }
        // 表单数据
        for (Property p : api.getRequestBodyForm()) {
            String value = nonNull(p.getDefaultValue()) ? p.getDefaultValue() : "";
            sb.append("--data-urlencode '").append(escape(p.getName()))
                    .append("=").append(escape(value)).append(lineEnd);
        }
        // 请求体
        if (bodyType == RequestBodyType.json && api.getRequestBody() != null) {
            String example = PropertyUtils.getJsonExample(api.getRequestBody());
            sb.append("--data-raw '").append(escape(example)).append(lineEnd);
        }
        sb.delete(sb.length() - 3, sb.length());
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

    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("'", "\\'");
    }
}
