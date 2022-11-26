package io.apidocx.parse.model;

import io.apidocx.model.HttpMethod;
import java.util.List;
import lombok.Data;

/**
 * 请求路径和方法信息
 */
@Data
public class PathInfo {

    private HttpMethod method;

    private List<String> paths;

    public String getPath() {
        return paths != null && paths.size() > 0 ? paths.get(0) : null;
    }

}
