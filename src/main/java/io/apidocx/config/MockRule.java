package io.apidocx.config;

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
    private String type;

    /** 匹配正则表达式 */
    private String match;

    /** mock表达式 */
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
        Matcher matcher = doGetMatchPattern().matcher(fieldName);
        return matcher.matches();
    }

    private Pattern doGetMatchPattern() {
        if (matchPattern != null) {
            return matchPattern;
        }
        synchronized (this) {
            if (matchPattern != null) {
                return matchPattern;
            }
            this.matchPattern = Pattern.compile(match, Pattern.CASE_INSENSITIVE);
            return this.matchPattern;
        }
    }

}
