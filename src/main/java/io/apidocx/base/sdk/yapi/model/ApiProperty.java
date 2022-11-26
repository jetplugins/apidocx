package io.apidocx.base.sdk.yapi.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;
import java.util.Set;
import lombok.Data;

/**
 * 参数
 */
@Data
public class ApiProperty {

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
    private Set<String> required;

    @SerializedName("default")
    private String defaultValue;

    /**
     * 当type为object
     */
    private Map<String, ApiProperty> properties;

    /** 当type为array */
    private ApiProperty items;

    /**
     * 当type为array, item元素是否唯一
     */
    private Boolean uniqueItems;

    /**
     * 当type为array, 最小元素个数
     */
    private Integer minItems;

    /**
     * 当type为array, 最大元素个数
     */
    private Integer maxItems;

    /**
     * 字符串长度
     */
    private Integer minLength;

    /**
     * 字符串长度
     */
    private Integer maxLength;

    /**
     * 响应mock
     */
    private Mock mock;

    @Data
    public static class Mock {

        private String mock;

        public Mock(String mock) {
            this.mock = mock;
        }

    }
}


