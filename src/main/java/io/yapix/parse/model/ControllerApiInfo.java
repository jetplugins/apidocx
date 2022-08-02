package io.yapix.parse.model;

import lombok.Data;

/**
 * 控制类上接口信息
 */
@Data
public class ControllerApiInfo {

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
