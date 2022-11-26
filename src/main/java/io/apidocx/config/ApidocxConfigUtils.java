package io.apidocx.config;

import static io.apidocx.config.DefaultConstants.FILE_NAME;

import com.google.common.base.Splitter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import io.apidocx.base.util.JsonUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
 * <p>
 * 备注: 配置文件读取使用.ypix文件
 */
public final class ApidocxConfigUtils {

    private ApidocxConfigUtils() {
    }

    /**
     * 查找配置文件
     */
    public static VirtualFile findConfigFile(Project project, Module module) {
        VirtualFile yapiConfigFile = null;
        if (module != null) {
            VirtualFile[] moduleContentRoots = ModuleRootManager.getInstance(module).getContentRoots();
            if (moduleContentRoots.length > 0) {
                yapiConfigFile = moduleContentRoots[0].findFileByRelativePath(FILE_NAME);
            }
        }
        if (yapiConfigFile == null || !yapiConfigFile.exists()) {
            yapiConfigFile = project.getBaseDir().findFileByRelativePath(FILE_NAME);
        }
        return yapiConfigFile;
    }

    public static ApidocxConfig readConfig(VirtualFile vf) throws IOException {
        String content = new String(vf.contentsToByteArray(), StandardCharsets.UTF_8);
        Properties properties = new Properties();
        properties.load(new StringReader(content));
        return ApidocxConfig.fromProperties(properties);
    }


    /**
     * 读取配置(xml)
     */
    public static ApidocxConfig readFromXml(String xml, String moduleName)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        Element root = doc.getDocumentElement();
        String rootName = root.getNodeName();
        if ("project".equals(rootName)) {
            return doReadXmlYapiProjectConfigByOldVersion(doc);
        } else {
            NodeList nodes = root.getChildNodes();
            ApidocxConfig rootConfig = doReadXmlYapiProjectConfigByNodeList(nodes);

            if (StringUtils.isNotEmpty(moduleName)) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (!"project".equals(node.getNodeName())) {
                        continue;
                    }
                    NamedNodeMap attributes = node.getAttributes();
                    String projectTagName = attributes.getNamedItem("name").getNodeValue();
                    if (moduleName.equalsIgnoreCase(projectTagName)) {
                        ApidocxConfig moduleConfig = doReadXmlYapiProjectConfigByNodeList(node.getChildNodes());
                        mergeToFirst(rootConfig, moduleConfig);
                        break;
                    }
                }
            }
            return rootConfig;
        }
    }

    private static ApidocxConfig doReadXmlYapiProjectConfigByOldVersion(Document doc) {
        ApidocxConfig config = new ApidocxConfig();

        Splitter splitter = Splitter.on(",").trimResults().omitEmptyStrings();
        NodeList nodes = doc.getElementsByTagName("option");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String attributeName = node.getAttributes().getNamedItem("name").getNodeValue();
            String text = node.getTextContent().trim();
            if (StringUtils.isEmpty(text)) {
                continue;
            }

            switch (attributeName) {
                case "projectId":
                    config.setYapiProjectId(text);
                    break;
                case "returnClass":
                case "returnWrapType":
                    config.setReturnWrapType(text);
                    break;
                case "returnUnwrapTypes":
                    config.setReturnUnwrapTypes(splitter.splitToList(text));
                    break;
                case "parameterIgnoreTypes":
                    config.setParameterIgnoreTypes(splitter.splitToList(text));
                    break;
                case "mockRules":
                    Type type = new TypeToken<List<MockRule>>() {
                    }.getType();
                    List<MockRule> mockRules = JsonUtils.fromJson(text, type);
                    config.setMockRules(mockRules);
                    break;
                case "dateTimeFormatMvc":
                    config.setDateTimeFormatMvc(text);
                    break;
                case "dateTimeFormatJson":
                    config.setDateTimeFormatJson(text);
                    break;
            }
        }
        return config;
    }

    @NotNull
    private static ApidocxConfig doReadXmlYapiProjectConfigByNodeList(NodeList nodes) {
        ApidocxConfig config = new ApidocxConfig();
        Gson gson = new Gson();

        Splitter splitter = Splitter.on(",").trimResults().omitEmptyStrings();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String text = node.getTextContent().trim();
            if (StringUtils.isEmpty(text)) {
                continue;
            }

            switch (node.getNodeName()) {
                case "projectId":
                    config.setYapiProjectId(text);
                    break;
                case "returnClass":
                case "returnWrapType":
                    config.setReturnWrapType(text);
                    break;
                case "returnUnwrapTypes":
                    config.setReturnUnwrapTypes(splitter.splitToList(text));
                    break;
                case "parameterIgnoreTypes":
                    config.setParameterIgnoreTypes(splitter.splitToList(text));
                    break;
                case "mockRules":
                    Type type = new TypeToken<List<MockRule>>() {
                    }.getType();
                    List<MockRule> mockRules = JsonUtils.fromJson(text, type);
                    config.setMockRules(mockRules);
                    break;
                case "dateTimeFormatMvc":
                    config.setDateTimeFormatMvc(text);
                    break;
                case "dateTimeFormatJson":
                    config.setDateTimeFormatJson(text);
                    break;
            }
        }
        return config;
    }

    /**
     * 配置合并.
     */
    public static void mergeToFirst(ApidocxConfig a, ApidocxConfig b) {
        if (b != null) {
            if (StringUtils.isNotEmpty(b.getYapiProjectId())) {
                a.setYapiProjectId(b.getYapiProjectId());
            }
            if (StringUtils.isNotEmpty(b.getReturnWrapType())) {
                a.setReturnWrapType(b.getReturnWrapType());
            }
        }

    }
}
