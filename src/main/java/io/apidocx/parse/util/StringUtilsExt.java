package io.apidocx.parse.util;

import com.google.common.base.Strings;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述工具,存放标题，菜单，描述等工具类
 *
 * @author chengsheng@qbb6.com
 * @date 2019/4/30 4:13 PM
 */
public class StringUtilsExt {


    static Pattern humpPattern = Pattern.compile("[A-Z]");

    static final String DASH = "-";

    /**
     * 驼峰转化
     */
    public static String camelToLine(String camelCase, String split) {
        if (Strings.isNullOrEmpty(split)) {
            split = DASH;
        }
        Matcher matcher = humpPattern.matcher(camelCase);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, split + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        if (result.startsWith(split)) {
            result = result.substring(split.length());
        }
        return result;
    }

}
