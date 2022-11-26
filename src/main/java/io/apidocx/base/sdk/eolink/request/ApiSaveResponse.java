package io.apidocx.base.sdk.eolink.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApiSaveResponse extends Response {

    private Long apiID;

    private Long groupID;

}
