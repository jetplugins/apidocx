package io.yapix.parse.model;

/**
 * 数据类型解析结果
 */
public class DataTypeParseInfo {

    /** 数据类型 */
    private String type;

    /** 时间格式 */
    private String dateFormat;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
