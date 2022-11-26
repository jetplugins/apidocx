package io.apidocx.base.sdk.rap2.dto;

import io.apidocx.base.sdk.rap2.model.Rap2Property;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class InterfacePropertiesUpdateRequest {

    private Long interfaceId;

    private List<Rap2Property> properties;
    private Summary summary;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Summary {

        private String bodyOption;
        private int posFilter;

    }

}
