package io.yapix.parse.model;

import io.yapix.model.HttpMethod;
import java.util.List;
import lombok.Data;

/**
 * 请求路径和方法信息
 */
@Data
public class PathParseInfo {

    private HttpMethod method;

    private List<String> paths;

    public String getPath() {
        return paths != null && paths.size() > 0 ? paths.get(0) : null;
    }

}
