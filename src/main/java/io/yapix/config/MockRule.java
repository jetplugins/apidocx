package io.yapix.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 智能匹配mock规则
 */
public class MockRule {

    /** 数据类型 */
    private String type;

    /** 匹配正则表达式 */
    private String match;

    /** mock表达式 */
    private String mock;

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

    private Pattern getMatchPattern() {
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

    //-----------generated---------------------//

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getMock() {
        return mock;
    }

    public void setMock(String mock) {
        this.mock = mock;
    }
}
