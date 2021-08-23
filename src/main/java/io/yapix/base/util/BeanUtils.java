package io.yapix.base.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class BeanUtils {

    private BeanUtils() {
    }

    public static <T> void merge(T o1, T o2) {
        Class<?> clazz = o1.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
                Object value = getFiledValue(field, o2);
                if (value != null) {
                    setFiledValue(o1, field, value);
                }
            }
        }
    }

    private static void setFiledValue(Object target, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("It's impossible", e);
        }
    }

    private static Object getFiledValue(Field field, Object target) {
        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("It's impossible", e);
        }
    }
}
