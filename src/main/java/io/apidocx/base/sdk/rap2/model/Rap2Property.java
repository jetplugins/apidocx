package io.apidocx.base.sdk.rap2.model;

import java.util.Date;
import java.util.List;
import lombok.Data;


@Data
public class Rap2Property {

    private String id;

    private String scope;
    private String type;
    private Long pos;
    private String name;
    private String rule;
    private String value;
    private String description;
    private String parentId;
    private Long priority;
    private Long interfaceId;
    private Long creatorId;
    private Long moduleId;
    private Long repositoryId;
    private Boolean required;
    private Integer depth;
    private Boolean memory;
    private List<Rap2Property> children;
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;

}
