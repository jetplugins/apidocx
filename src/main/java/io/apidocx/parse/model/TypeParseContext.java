package io.apidocx.parse.model;

import java.util.List;
import lombok.Data;

/**
 * 类型解析上下文参数
 */
@Data
public class TypeParseContext {

    /**
     * 分组校验
     */
    private List<String> jsr303ValidateGroups;

}
