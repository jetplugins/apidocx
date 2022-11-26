package io.apidocx.parse.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaDocumentedElement;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.javadoc.PsiInlineDocTag;
import io.apidocx.parse.constant.DocumentTags;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * PsiDocComment相关工具类
 */
public class PsiDocCommentUtils {

    private PsiDocCommentUtils() {
    }


    /**
     * 获取标记自定义字段名(包括字段描述)
     *
     */
    public static Map<String, String> getTagParamTextMap(PsiJavaDocumentedElement element) {
        Map<String, String> map = new HashMap<>();
        PsiDocTag[] tags = findTagsByName(element, DocumentTags.Param);
        for (PsiDocTag tag : tags) {
            PsiElement[] elements = tag.getDataElements();
            if (elements.length >= 2) {
                String name = elements[0].getText().trim();
                String description = elements[1].getText().trim();
                map.put(name, description);
            }
        }
        return map;
    }

    /**
     * 获取标记自定义字段名(不包括字段描述)
     * @param tag 实例变量类型为接口类/实体类  可通过@see 来获取扁平后的字段
     *
     * /**
     *   * @see SubInterfaceImpl {field1, field2}
     *   * @see SubxInterfaceImpl {field3, field4, field5}
     *   *\/
     * interface BaseInterface {}
     *
     * BaseInterface {field1, field2, field3, ...}
     * 至于如何区分field1,field2,field3隶属于哪个实现类, 是用户写desc需要考量的, 而非插件程序逻辑.
     *
     */
    public static Set<String> getTagTextSet(PsiJavaDocumentedElement element, String tag){
        return Arrays.stream(findTagsByName(element, tag))
                .map(PsiDocTag::getDataElements)
                .filter(it -> it.length >= 1)
                .map(it -> it[0].getText().trim())
                .collect(Collectors.toSet());
    }

    /**
     * 获取标记文本值
     */
    public static String getTagText(PsiJavaDocumentedElement element, String tagName) {
        PsiDocTag tag = PsiDocCommentUtils.findTagByName(element, tagName);
        if (tag != null) {
            String[] splits = tag.getText().split("\\s", 2);
            if (splits.length > 1) {
                return splits[1];
            }
        }
        return null;
    }

    /**
     * 获取文档标记内容
     */
    public static String getDocCommentTagText(PsiJavaDocumentedElement element, String tagName) {
        String text = null;
        PsiDocComment comment = element.getDocComment();
        if (comment != null) {
            PsiDocTag tag = comment.findTagByName(tagName);
            if (tag != null && tag.getValueElement() != null) {
                StringBuilder sb = new StringBuilder();
                for (PsiElement e : tag.getDataElements()) {
                    sb.append(e.getText().trim());
                }
                text = sb.toString();
            }
        }
        return text;
    }

    /**
     * 获取文档标题行
     */
    public static String getDocCommentTitle(PsiJavaDocumentedElement element) {
        PsiDocComment comment = element.getDocComment();
        if (comment != null) {
            return Arrays.stream(comment.getDescriptionElements())
                    .filter(o -> o instanceof PsiDocToken)
                    .map(PsiElement::getText)
                    .findFirst()
                    .map(String::trim)
                    .orElse(null);
        }
        return null;
    }

    /**
     * 获取文档注释上的标记
     */
    public static PsiDocTag findTagByName(PsiJavaDocumentedElement element, String tagName) {
        PsiDocComment comment = element.getDocComment();
        if (comment != null) {
            return comment.findTagByName(tagName);
        }
        return null;
    }


    /**
     * 获取文档注释上的标记
     */
    public static PsiDocTag[] findTagsByName(PsiJavaDocumentedElement element, String tagName) {
        PsiDocComment comment = element.getDocComment();
        if (comment != null) {
            return comment.findTagsByName(tagName);
        }
        return new PsiDocTag[0];
    }

    /**
     * 获取注释中link标记的内容
     */
    public static String getInlineLinkContent(PsiJavaDocumentedElement element) {
        PsiDocComment comment = element.getDocComment();
        if (comment == null) {
            return null;
        }
        PsiElement linkElement = Arrays.stream(comment.getDescriptionElements())
                .filter(ele -> (ele instanceof PsiInlineDocTag) && ele.getText().startsWith("{@link"))
                .findFirst().orElse(null);
        if (linkElement == null) {
            return null;
        }
        String text = linkElement.getText();
        return text.substring("{@link".length(), text.length() - 1).trim();
    }
}
