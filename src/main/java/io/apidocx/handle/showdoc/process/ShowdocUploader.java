package io.apidocx.handle.showdoc.process;

import io.apidocx.base.sdk.showdoc.ShowdocClient;
import io.apidocx.base.sdk.showdoc.model.ShowdocProjectToken;
import io.apidocx.base.sdk.showdoc.model.ShowdocUpdateRequest;
import io.apidocx.base.sdk.showdoc.model.ShowdocUpdateResponse;
import io.apidocx.handle.markdown.MarkdownGenerator;
import io.apidocx.model.Api;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;

/**
 * Showdoc上传
 */
public class ShowdocUploader {

    private final ShowdocClient client;
    private final Map<String, ShowdocProjectToken> tokenCache = new ConcurrentHashMap<>();

    public ShowdocUploader(ShowdocClient client) {
        this.client = client;
    }

    public ShowdocUpdateResponse upload(String projectId, Api api) {
        if (StringUtils.isEmpty(api.getSummary())) {
            api.setSummary(api.getPath());
        }

        String markdown = new MarkdownGenerator().generate(api);
        ShowdocProjectToken token = getToken(projectId);

        ShowdocUpdateRequest page = new ShowdocUpdateRequest();
        page.setApiKey(token.getApiKey());
        page.setApiToken(token.getApiToken());
        page.setCatName(api.getCategory());
        page.setPageTitle(api.getSummary());
        page.setPageContent(markdown);

        ShowdocUpdateResponse response = client.updatePageByOpenApi(page);
        if (response == null) {
            response = new ShowdocUpdateResponse();
            response.setItemId(projectId);
        }
        return response;
    }

    private ShowdocProjectToken getToken(String projectId) {
        return tokenCache.computeIfAbsent(projectId, key -> client.getProjectToken(projectId));
    }

}
