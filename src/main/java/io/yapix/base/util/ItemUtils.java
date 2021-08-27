package io.yapix.base.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.yapix.model.DataTypes;
import io.yapix.model.Item;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.time.DateFormatUtils;

public class ItemUtils {

    private ItemUtils() {
    }

    public static String getJsonExample(Item item) {
        Object example = doGetJsonExample(item);
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        return gson.toJson(example);
    }

    private static Object doGetJsonExample(Item item) {
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
            Map<String, Object> data = new HashMap<>();
            Map<String, Item> properties = item.getProperties();
            if (properties != null) {
                for (Entry<String, Item> entry : properties.entrySet()) {
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
