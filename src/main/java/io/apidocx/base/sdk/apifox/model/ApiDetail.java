package io.apidocx.base.sdk.apifox.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ApiDetail {
    private Long id;
    private Long projectId;
    private Long folderId;
    private Long ordering;
    private Long responsibleId;

    private String name;
    private String type;
    private String serverId;
    private String description;
    private String operationId;
    private String sourceUrl;
    private String method;
    private String path;
    private List<String> tags;
    private String status;
    private RequestBody requestBody;
    private Parameters parameters;
    private Parameters commonParameters;
    private Object auth;
    private List<Response> responses;
    private List<String> responseExamples;
    private List<String> codeSamples;
    private Object commonResponseStatus;
    private Object advancedSettings;
    private Object customApiFields;
    private Object mockScript;

    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    private Long creatorId;
    private Long editorId;

    @Data
    public static class RequestBody {
        private String type;
        private List<Parameter> parameters;
        private Schema jsonSchema;
    }


    @Data
    public static class Response {
        private Long id;
        private Integer code;
        private String name;
        private String contentType;
        private Long apiDetailId;
        private Schema jsonSchema;
        private Boolean defaultEnable;
        private Integer projectId;
        private Integer ordering;
        private String deletedAt;
        private Date createdAt;
        private Date updatedAt;
    }

    @Data
    public static class Parameters {
        private List<Parameter> path;
        private List<Parameter> query;
        private List<Parameter> cookie;
        private List<Parameter> header;
    }

    @Data
    public static class Schema {
        @SerializedName("x-apifox-orders")
        private List<String> xApifoxOrders;

        private String name;
        private String title = null;
        private BigDecimal multipleOf = null;
        private BigDecimal maximum = null;
        private Boolean exclusiveMaximum = null;
        private BigDecimal minimum = null;
        private Boolean exclusiveMinimum = null;
        private Integer maxLength = null;
        private Integer minLength = null;
        private String pattern = null;
        private Integer maxItems = null;
        private Integer minItems = null;
        private Boolean uniqueItems = null;
        private Integer maxProperties = null;
        private Integer minProperties = null;
        private List<String> required = null;
        private String type = null;
        private Schema not = null;
        private Map<String, Schema> properties = null;
        private Object additionalProperties = null;
        private String description = null;
        private String format = null;
        private Boolean nullable = null;
        private Boolean readOnly = null;
        private Boolean writeOnly = null;
        protected Object example = null;
        private Object externalDocs = null;
        private Boolean deprecated = null;
        private Object xml = null;
        private java.util.Map<String, Object> extensions = null;
        protected List<Object> _enum = null;
        private Object discriminator = null;

        private boolean exampleSetFlag;
        private List<Schema> prefixItems = null;
        private List<Schema> allOf = null;
        private List<Schema> anyOf = null;
        private List<Schema> oneOf = null;
        private Schema items = null;

        private Mock mock = null;

        public Schema addProperty(String key, Schema property) {
            if (this.properties == null) {
                this.properties = new LinkedHashMap<>();
            }
            this.properties.put(key, property);
            return this;
        }
    }

    @Data
    @NoArgsConstructor
    public static class Mock {
        private String mock;

        public Mock(String mock) {
            this.mock = mock;
        }
    }


    @Data
    public static class Parameter {
        private String id;

        /**
         * 名称
         */
        private String name;

        /**
         * 类型
         */
        private String type;

        /**
         * 描述
         */
        private String description;

        /**
         * 是否必须
         */
        private Boolean required;

        /**
         * 请求示例
         */
        private String example;

    }

}
