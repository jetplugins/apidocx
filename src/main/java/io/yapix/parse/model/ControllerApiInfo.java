package io.yapix.parse.model;

/**
 * 控制类上接口信息
 */
public class ControllerApiInfo {

    /**
     * 路径
     */
    private String path;

    /**
     * 分类
     */
    private String category;

    /**
     * 声明的分类
     */
    private String declareCategory;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDeclareCategory() {
        return declareCategory;
    }

    public void setDeclareCategory(String declareCategory) {
        this.declareCategory = declareCategory;
    }
}
