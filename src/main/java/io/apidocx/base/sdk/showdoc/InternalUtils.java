package io.apidocx.base.sdk.showdoc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * 内部工具包
 */
@UtilityClass
class InternalUtils {

    private static final Gson gson = new Gson();

    public static String toJson(Object data) {
        return gson.toJson(data);
    }

    /**
     * 获取地址path路径
     */
    public static String getUrlPath(String url) {
        try {
            URL theUrl = new URL(url);
            return theUrl.getPath();
        } catch (MalformedURLException e) {
            // ignored
        }
        return null;
    }


    /**
     * 解析SetCookie为Cookie
     */
    public static String parseCookie(Collection<String> setCookies) {
        return setCookies.stream()
                .map(c -> {
                    List<HttpCookie> httpCookies = HttpCookie.parse(c);
                    if (httpCookies.isEmpty()) {
                        return null;
                    }
                    HttpCookie httpCookie = httpCookies.get(0);
                    return httpCookie.getName() + "=" + httpCookie.getValue();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining("; "));
    }

    /**
     * Bean转换为Map, 非普通类型字段转化为Json字符串
     */
    public static Map<String, String> beanToMap(Object bean) {
        List<Field> fields = getAllField(bean.getClass());
        Map<String, String> map = Maps.newHashMapWithExpectedSize(fields.size());
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                continue;
            }
            field.setAccessible(true);

            String name = "";
            SerializedName annotation = field.getAnnotation(SerializedName.class);
            if (annotation != null) {
                name = annotation.value().trim();
            }
            if (StringUtils.isEmpty(name)) {
                name = field.getName();
            }

            String value = null;
            try {
                Object fieldValue = field.get(bean);
                if (fieldValue != null) {
                    if (isSimpleType(field.getType())) {
                        value = fieldValue.toString();
                    } else {
                        value = InternalUtils.toJson(fieldValue);
                    }
                } else if (!isSimpleType(field.getType())) {
                    value = isCollectionOrArrayType(field.getType()) ? "[]" : "{}";
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (value != null) {
                map.put(name, value);
            }
        }
        return map;
    }

    private static List<Field> getAllField(Class<?> clazz) {
        List<Field> fields = Lists.newArrayList();
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            fields.addAll(getAllField(superclass));
        }
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return fields;
    }

    /**
     * 是否是普通类型（非对象和数组）
     */
    private static boolean isSimpleType(Class<?> type) {
        if (type.isPrimitive() || type.isEnum()) {
            return true;
        }

        Set<String> set = new HashSet<>();
        set.add("java.lang.Byte");
        set.add("java.lang.Short");
        set.add("java.lang.Integer");
        set.add("java.lang.Long");
        set.add("java.lang.Float");
        set.add("java.lang.Double");
        set.add("java.lang.Boolean");
        set.add("java.lang.Character");
        set.add("java.lang.String");
        set.add("java.util.Date");
        set.add("java.time.LocalDateTime");
        set.add("java.time.LocalDate");
        return set.contains(type.getCanonicalName());
    }

    private static boolean isCollectionOrArrayType(Class<?> type) {
        return Collection.class.isAssignableFrom(type) || type.isArray();
    }

}
