package io.yapix.base.sdk.rap2.request;

import io.yapix.base.sdk.rap2.model.Rap2Property;
import java.util.List;

public class UpdatePropertiesRequest {

    private Long interfaceId;

    private List<Rap2Property> properties;
    private Summary summary;


    public static class Summary {

        private String bodyOption;
        private int posFilter;

        public Summary() {
        }

        public Summary(String bodyOption, int posFilter) {
            this.bodyOption = bodyOption;
            this.posFilter = posFilter;
        }

        public void setBodyOption(String bodyOption) {
            this.bodyOption = bodyOption;
        }

        public String getBodyOption() {
            return bodyOption;
        }

        public void setPosFilter(int posFilter) {
            this.posFilter = posFilter;
        }

        public int getPosFilter() {
            return posFilter;
        }

    }

    public Long getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(Long interfaceId) {
        this.interfaceId = interfaceId;
    }

    public List<Rap2Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Rap2Property> properties) {
        this.properties = properties;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }
}
