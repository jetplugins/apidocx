package io.apidocx.base.sdk.rap2.dto;

import lombok.Data;

@Data
public class ModuleCreateRequest {

    private Long repositoryId;

    private String name;

    private String description;

}
