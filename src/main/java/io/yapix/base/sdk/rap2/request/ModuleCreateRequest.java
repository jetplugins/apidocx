package io.yapix.base.sdk.rap2.request;

import lombok.Data;

@Data
public class ModuleCreateRequest {

    private Long repositoryId;

    private String name;

    private String description;

}
