package io.apidocx.parse.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * properties文件加载，内部会缓存.
 */
public class PropertiesLoader  {

    private static final Map<String, Properties> cache = new ConcurrentHashMap<>();

    private PropertiesLoader() {
    }

    /**
     * 获取properties内部缓存
     */
    public static Properties getProperties(String file) {
        return cache.computeIfAbsent(file, key -> readProperties(file));
    }

    /**
     * 读取properties不会缓存
     */
    public static Properties readProperties(String file) {
        Properties properties = new Properties();
        try {
            InputStream is = PropertiesLoader.class.getClassLoader().getResourceAsStream(file);
            BufferedReader bf = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            properties.load(bf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

}
