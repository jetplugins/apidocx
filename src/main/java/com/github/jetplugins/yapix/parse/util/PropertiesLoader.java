package com.github.jetplugins.yapix.parse.util;

import com.github.jetplugins.yapix.parse.parser.MockParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * properties文件加载，内部会缓存.
 */
public class PropertiesLoader {

    private static final Map<String, Properties> cache = new ConcurrentHashMap<>();

    private PropertiesLoader() {
    }

    public static Properties getProperties(String file) {
        return cache.computeIfAbsent(file, key -> {
            InputStream is = MockParser.class.getClassLoader().getResourceAsStream(file);
            Properties properties = new Properties();
            try {
                properties.load(is);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return properties;
        });
    }
}
