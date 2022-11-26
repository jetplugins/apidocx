package io.apidocx.base.sdk.rap2.model;

import java.util.List;
import lombok.Data;

/**
 * 仓库信息
 */
@Data
public class Rap2Repository {

    private Long id;
    private String name;
    private String description;
    private List<Rap2Module> modules;

}
