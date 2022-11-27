package io.apidocx.base.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BeanUtils {

    /**
     * 合并两个对象
     *
     * @param target 目标对象
     * @param source 源对象
     */
    @SneakyThrows
    public static <T> void merge(T target, T source) {
        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
                field.setAccessible(true);
                Object value = field.get(source);
                if (value != null) {
                    field.set(target, value);
                }
            }
        }
    }

}
