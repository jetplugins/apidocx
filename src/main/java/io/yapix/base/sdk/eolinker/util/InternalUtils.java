package io.yapix.base.sdk.eolinker.util;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import io.yapix.base.sdk.eolinker.AbstractClient.CustomUrlEncodedFormEntity;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class InternalUtils {

    private InternalUtils() {
    }

    public static CustomUrlEncodedFormEntity beanToFormEntity(Object data) {
        List<NameValuePair> fields = getFields(data);
        return new CustomUrlEncodedFormEntity(fields, StandardCharsets.UTF_8);
    }

    private static List<NameValuePair> getFields(Object data) {
        List<Field> fields = getAllField(data.getClass());
        List<NameValuePair> parameters = Lists.newArrayListWithExpectedSize(fields.size());
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
                String name = field.getName();
                String value = "";
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(data);
                    if (fieldValue != null) {
                        if (isNeedJsonString(fieldValue)) {
                            value = new Gson().toJson(fieldValue);
                        } else {
                            value = fieldValue.toString();
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                parameters.add(new BasicNameValuePair(name, value));
            }
        }
        return parameters;
    }

    private static List<Field> getAllField(Class clazz) {
        List<Field> fields = Lists.newArrayList();
        Class superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            fields.addAll(getAllField(superclass));
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field f : declaredFields) {
            fields.add(f);
        }
        return fields;
    }

    private static boolean isNeedJsonString(Object value) {
        Set<String> set = new HashSet<>();
        set.add("byte");
        set.add("short");
        set.add("int");
        set.add("long");
        set.add("float");
        set.add("double");
        set.add("boolean");
        set.add("char");
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
        return !set.contains(value.getClass().getCanonicalName());
    }

    public static String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return new BigInteger(1, bytes).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No way");
        }
    }

}
