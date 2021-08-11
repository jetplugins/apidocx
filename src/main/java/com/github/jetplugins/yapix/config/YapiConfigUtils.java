package com.github.jetplugins.yapix.config;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 配置处理工具类.
 */
public final class YapiConfigUtils {

    private YapiConfigUtils() {
    }

    /**
     * 查找配置文件yapi.xml
     */
    public static VirtualFile findConfigFile(Project project, Module module) {
        VirtualFile yapiConfigFile = null;
        if (module != null) {
            VirtualFile[] moduleContentRoots = ModuleRootManager.getInstance(module).getContentRoots();
            if (moduleContentRoots.length > 0) {
                yapiConfigFile = moduleContentRoots[0].findFileByRelativePath("src/main/resources/yapi.xml");
                if (yapiConfigFile == null) {
                    yapiConfigFile = moduleContentRoots[0].findFileByRelativePath("yapi.xml");
                }
            }
        }
        if (yapiConfigFile == null || !yapiConfigFile.exists()) {
            yapiConfigFile = project.getBaseDir().findFileByRelativePath("src/main/resources/yapi.xml");
            if (yapiConfigFile == null || !yapiConfigFile.exists()) {
                yapiConfigFile = project.getBaseDir().findFileByRelativePath("yapi.xml");
            }
            if (yapiConfigFile == null || !yapiConfigFile.exists()) {
                yapiConfigFile = project.getBaseDir().findFileByRelativePath(".yapi.xml");
            }
        }
        return yapiConfigFile;
    }

    /**
     * 读取配置(properties)
     */
    public static YapiConfig readFromProperties(String props) throws IOException {
        Properties properties = new Properties();
        properties.load(new StringReader(props));

        YapiConfig config = new YapiConfig();
        config.setProjectId(StringUtils.trim(properties.getProperty("projectId")));
        config.setProjectType(StringUtils.trim(properties.getProperty("projectType")));
        config.setReturnClass(StringUtils.trim(properties.getProperty("returnClass")));
        config.setAttachUpload(StringUtils.trim(properties.getProperty("attachUpload")));
        return config;
    }

    /**
     * 读取配置(xml)
     */
    public static YapiConfig readFromXml(String xml)
            throws ParserConfigurationException, IOException, SAXException {
        return readFromXml(xml, null);
    }

    /**
     * 读取配置(xml)
     */
    public static YapiConfig readFromXml(String xml, String moduleName)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        Element root = doc.getDocumentElement();
        String rootName = root.getNodeName();
        if ("project".equals(rootName)) {
            return doReadXmlYapiProjectConfigByOldVersion(doc);
        } else {
            NodeList nodes = root.getChildNodes();
            YapiConfig rootConfig = doReadXmlYapiProjectConfigByNodeList(nodes);

            if (StringUtils.isNotEmpty(moduleName)) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (!"project".equals(node.getNodeName())) {
                        continue;
                    }
                    NamedNodeMap attributes = node.getAttributes();
                    String projectTagName = attributes.getNamedItem("name").getNodeValue();
                    if (moduleName.equalsIgnoreCase(projectTagName)) {
                        YapiConfig moduleConfig = doReadXmlYapiProjectConfigByNodeList(node.getChildNodes());
                        mergeToFirst(rootConfig, moduleConfig);
                        break;
                    }
                }
            }
            return rootConfig;
        }
    }

    private static YapiConfig doReadXmlYapiProjectConfigByOldVersion(Document doc) {
        YapiConfig config = new YapiConfig();
        NodeList nodes = doc.getElementsByTagName("option");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String attributeName = node.getAttributes().getNamedItem("name").getNodeValue();
            switch (attributeName) {
                case "projectId":
                    config.setProjectId(node.getTextContent().trim());
                    break;
                case "projectType":
                    config.setProjectType(node.getTextContent().trim());
                    break;
                case "returnClass":
                    config.setReturnClass(node.getTextContent().trim());
                    break;
                case "attachUpload":
                    config.setAttachUpload(node.getTextContent().trim());
                    break;
            }
        }
        return config;
    }

    @NotNull
    private static YapiConfig doReadXmlYapiProjectConfigByNodeList(NodeList nodes) {
        YapiConfig config = new YapiConfig();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            switch (node.getNodeName()) {
                case "projectId":
                    config.setProjectId(node.getTextContent().trim());
                    break;
                case "projectType":
                    config.setProjectType(node.getTextContent().trim());
                    break;
                case "returnClass":
                    config.setReturnClass(node.getTextContent().trim());
                    break;
                case "attachUpload":
                    config.setAttachUpload(node.getTextContent().trim());
                    break;
            }
        }
        return config;
    }

    /**
     * 配置合并.
     */
    public static void mergeToFirst(YapiConfig a, YapiConfig b) {
        if (b != null) {
            if (StringUtils.isNotEmpty(b.getProjectId())) {
                a.setProjectId(b.getProjectId());
            }
            if (StringUtils.isNotEmpty(b.getProjectType())) {
                a.setProjectType(b.getProjectType());
            }
            if (StringUtils.isNotEmpty(b.getReturnClass())) {
                a.setReturnClass(b.getReturnClass());
            }
            if (StringUtils.isNotEmpty(b.getAttachUpload())) {
                a.setAttachUpload(b.getAttachUpload());
            }
        }

    }
}
