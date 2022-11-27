package io.apidocx.handle.markdown;

import com.google.common.collect.Lists;
import io.apidocx.model.Api;
import io.apidocx.model.ParameterIn;
import io.apidocx.model.Property;
import io.apidocx.model.RequestBodyType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

/**
 * 生成markdown内容
 */
public class MarkdownGenerator {

    public String generate(List<Api> apis) {
        Map<String, List<Api>> categoryApis = apis.stream().collect(Collectors.groupingBy(Api::getCategory));
        StringBuilder markdown = new StringBuilder();
        categoryApis.forEach((category, apiList) -> {
            markdown.append(generateCategory(category, apiList));
        });
        return markdown.toString();
    }

    /**
     * 生成单个接口文档,不包含标题部分
     */
    public String generate(Api api) {
        return generateApi(api, -1);
    }

    /**
     * 生成某个分类的接口文档
     */
    private String generateCategory(String category, List<Api> apis) {
        StringBuilder markdown = new StringBuilder();
        markdown.append(format("# %s (%d)", category, apis.size())).append("\n\n");
        for (int i = 0; i < apis.size(); i++) {
            markdown.append(generateApi(apis.get(i), i + 1));
        }
        return markdown.toString();
    }

    private String generateApi(Api api, int serialNumber) {
        StringBuilder markdown = new StringBuilder();
        String summary = StringUtils.isNotEmpty(api.getSummary()) ? api.getSummary() : api.getPath();
        if (serialNumber >= 0) {
            markdown.append(format("## %d.%s", serialNumber, summary)).append("\n\n");
        }
        markdown.append(format("**路径**: %s %s", api.getMethod().name(), api.getPath())).append("\n\n");
        if (serialNumber < 0 && !Objects.equals(api.getSummary(), api.getPath())) {
            markdown.append(format("**描述**: %s", api.getSummary())).append("\n\n");
        }
        markdown.append("**请求参数**").append("\n\n");
        markdown.append(getPropertiesSnippets("Headers", api.getParametersByIn(ParameterIn.header)));
        markdown.append(getPropertiesSnippets("Path", api.getParametersByIn(ParameterIn.path)));
        markdown.append(getPropertiesSnippets("Query", api.getParametersByIn(ParameterIn.query)));
        markdown.append(
                getPropertiesSnippets(api.getRequestBodyType() == RequestBodyType.form_data ? "Form Data" : "Form",
                        api.getRequestBodyForm()));
        markdown.append(getBodySnippets("Body", api.getRequestBody(), true)).append("\n");
        markdown.append("**响应参数**").append("\n\n");
        markdown.append(getBodySnippets("Body", api.getResponses(), true)).append("\n");
        return markdown.toString();
    }

    private String getPropertiesSnippets(String title, List<Property> properties) {
        if (properties == null || properties.isEmpty()) {
            return "";
        }
        StringBuilder markdown = new StringBuilder();
        markdown.append(format("*%s:*", title)).append("\n\n");
        markdown.append("| 名称 | 必选 | 类型 | 默认值 | 描述 |").append("\n");
        markdown.append("| --- | --- | --- | --- | --- |").append("\n");
        properties.forEach(h -> {
            String description = getPropertyDescription(h);
            String hr = formatTable("| %s | %s | %s | %s | %s |",
                    h.getName(), requiredText(h.getRequired()), h.getTypeWithArray(),
                    h.getDefaultValue(), description);
            markdown.append(hr).append("\n");
        });
        markdown.append("\n");
        return markdown.toString();
    }


    /**
     * 获取请求体或响应体的markdown拼接
     */
    private String getBodySnippets(String title, Property property, boolean isLast) {
        if (property == null) {
            return "";
        }
        StringBuilder markdown = new StringBuilder();
        markdown.append(format("*%s:*", title)).append("\n\n");
        markdown.append("| 名称 | 必选 | 类型 | 默认值 | 描述 |").append("\n");
        markdown.append("| --- | --- | --- | --- | --- |").append("\n");
        if (property.isObjectType()) {
            List<Property> propertyList = Optional.ofNullable(property.getProperties())
                    .map(Map::values)
                    .map(Collection::stream)
                    .orElseGet(Stream::empty)
                    .collect(Collectors.toList());
            for (int i = 0; i < propertyList.size(); i++) {
                Property p = propertyList.get(i);
                markdown.append(propertyRowSnippets(p, 1, i == propertyList.size() - 1));
            }
        } else {
            markdown.append(propertyRowSnippets(property, 1, isLast));
        }
        return markdown.toString();
    }

    private String propertyRowSnippets(Property property, int depth, boolean isLast) {
        String nameDepth = property.getName();
        String tree = isLast ? "└ " : "├ ";
        if (depth > 1 && StringUtils.isNotEmpty(property.getName())) {
            nameDepth = StringUtils.repeat("&nbsp;&nbsp;", depth - 1) + tree + property.getName();
        }
        String row = formatTable("| %s | %s | %s | %s | %s |\n",
                nameDepth, requiredText(property.getRequired()), property.getTypeWithArray(),
                property.getDefaultValue(), getPropertyDescription(property));
        StringBuilder markdown = new StringBuilder(row);

        // 对象或对象数组
        Map<String, Property> properties = null;
        if (property.isObjectType() && property.getProperties() != null) {
            properties = property.getProperties();
        } else if (property.isArrayType() && property.getItems() != null && property.getItems().isObjectType()) {
            // 数组对象解开一层包装
            properties = property.getItems().getProperties();
        }
        if (properties != null) {
            List<Property> propertyList = Lists.newArrayList(properties.values());
            for (int i = 0; i < propertyList.size(); i++) {
                Property p = propertyList.get(i);
                markdown.append(propertyRowSnippets(p, depth + 1, i == propertyList.size() - 1));
            }
        }

        // 多维普通数组
        boolean multipleArray = property.isArrayType() && property.getItems() != null
                && property.getItems().isArrayType();
        if (multipleArray) {
            markdown.append(propertyRowSnippets(property.getItems(), depth + 1, isLast));
        }
        return markdown.toString();
    }

    //----------------------- 辅助方法 ---------------------------//

    private String getPropertyDescription(Property property) {
        return property.getDescriptionMore();
    }

    private String format(String format, Object... values) {
        Object[] objects = Arrays.stream(values).map(v -> v == null ? "" : v).toArray();
        return String.format(format, objects);
    }

    private String formatTable(String format, Object... values) {
        Object[] objects = Arrays.stream(values).map(v -> v == null ? "" : escapeTable(v.toString())).toArray();
        return String.format(format, objects);
    }

    private String requiredText(Boolean required) {
        return Boolean.TRUE.equals(required) ? "是" : "否";
    }

    private String escapeTable(String value) {
        return value.replaceAll("\\|", "\\\\|");
    }

}
