package io.apidocx.base.sdk.rap2.model;

import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * 模块
 */
@Data
public class Rap2Module {

    /** 主键 */
    private Long id;
    /** 名称 */
    private String name;
    /** 描述 */
    private String description;
    /** 排序 */
    private Long priority;
    /** 创建人id */
    private Long creatorId;
    /** 仓库id */
    private Long repositoryId;
    /** 创建时间 */
    private Date createdAt;
    /** 更新时间 */
    private Date updatedAt;

    private List<Rap2InterfaceBase> interfaces;

}
