package io.yapix.config;

import static io.yapix.config.DefaultConstants.FILE_NAME;

import com.google.common.base.Splitter;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import io.yapix.base.util.JsonUtils;
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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 配置处理工具类.
 * <p>
 * 备注: 配置文件读取使用.ypix文件
 */
public final class YapixConfigUtils {

    private YapixConfigUtils() {
    }

    /**
     * 查找配置文件
     */
    public static VirtualFile findConfigFile(Project project, Module module) {
        // make sure documents all saved before refresh v-files in sync/recursive.
        // it's effective when modify/delete/create v-file
        FileDocumentManager.getInstance().saveAllDocuments();
        VfsUtil.markDirtyAndRefresh(false, true, true, project.getBaseDir());
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

    public static YapixConfig readYapixConfig(VirtualFile vf) throws IOException {
        String content = new String(vf.contentsToByteArray(), StandardCharsets.UTF_8);
        Properties properties = new Properties();
        properties.load(new StringReader(content));
        return YapixConfig.fromProperties(properties);
    }


    /**
     * 读取配置(xml)
     */
    public static YapixConfig readFromXml(String xml)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        return doReadXmlYapiProjectConfigByOldVersion(doc);
    }

    private static YapixConfig doReadXmlYapiProjectConfigByOldVersion(Document doc) {
        YapixConfig config = new YapixConfig();

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

}
