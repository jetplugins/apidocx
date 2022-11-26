package io.apidocx.parse.model;

import lombok.Data;

/**
 * 类级别上的Api信息
 */
@Data
public class ClassLevelApiInfo {

    /**
     * 路径
     */
    private String path;

    /**
     * 分类
     */
    private String category;

    /**
     * 声明的分类
     */
    private String declareCategory;

}
