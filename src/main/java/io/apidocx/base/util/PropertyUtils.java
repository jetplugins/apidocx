package io.apidocx.base.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.apidocx.model.DataTypes;
import io.apidocx.model.Property;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 数据模型Property处理工具
 */
@UtilityClass
public class PropertyUtils {

    /**
     * 类型描述转化为json示例
     */
    public static String getJsonExample(Property item) {
        Object example = doGetJsonExample(item);
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        return gson.toJson(example);
    }

    private static Object doGetJsonExample(Property item) {
        if (item == null) {
            return null;
        }
        String type = item.getType();
        switch (type) {
            case DataTypes.BOOLEAN:
                return true;
            case DataTypes.INTEGER:
                return 1;
            case DataTypes.NUMBER:
                return 1.0;
            case DataTypes.STRING:
            case DataTypes.FILE:
                return "";
            case DataTypes.DATETIME:
                return DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        }

        if (type.equals(DataTypes.OBJECT)) {
            Map<String, Object> data = new LinkedHashMap<>();
            Map<String, Property> properties = item.getProperties();
            if (properties != null) {
                for (Entry<String, Property> entry : properties.entrySet()) {
                    data.put(entry.getKey(), doGetJsonExample(entry.getValue()));
                }
            }
            return data;
        }
        if (type.equals(DataTypes.ARRAY)) {
            List<Object> data = new ArrayList<>();
            data.add(doGetJsonExample(item.getItems()));
            return data;
        }
        return new Object();
    }

}
