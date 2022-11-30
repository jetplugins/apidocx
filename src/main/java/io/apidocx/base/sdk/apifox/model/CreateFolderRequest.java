package io.apidocx.base.sdk.apifox.model;

import lombok.Builder;
import lombok.Data;

/**
 * 创建文件夹请求参数
 */
@Data
@Builder
public class CreateFolderRequest {

    /**
     * 文件夹名称
     */
    private String name;

    /**
     * 父目录id
     */
    private Long parentId;

    /**
     * 类型
     */
    @Builder.Default
    private String type = "http";

}
