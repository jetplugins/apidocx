package io.yapix.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

/**
 * 智能匹配mock规则
 */
@Data
public class MockRule {

    /** 数据类型 */
    @Getter
    private String type;

    /** 匹配正则表达式 */
    @Getter
    private String match;

    /** mock表达式 */
    @Getter
    private String mock;

    @Getter(AccessLevel.PRIVATE)
    private transient Pattern matchPattern;

    /**
     * 匹配规则
     */
    public boolean match(String type, String fieldName) {
        if (this.type == null || this.match == null) {
            return false;
        }
        if (!this.type.contains(type)) {
            return false;
        }
        Matcher matcher = getMatchPattern().matcher(fieldName);
        return matcher.matches();
    }

}
