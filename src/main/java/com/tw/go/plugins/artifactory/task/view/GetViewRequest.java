package com.tw.go.plugins.artifactory.task.view;

import java.util.HashMap;

import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugins.artifactory.task.PublishTask;
import com.tw.go.plugins.artifactory.utils.Util;

public class GetViewRequest {
    public GoPluginApiResponse execute() {
        int responseCode = DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;
        HashMap<String, String> view = new HashMap<>();
        view.put("displayValue", "Publish to Artifactory v2.0.0");
        try {
            view.put("template", Util.readResource("/view/publish.html"));
        } catch (Exception e) {
            responseCode = DefaultGoApiResponse.INTERNAL_ERROR;
            String errorMessage = "Failed to find template: " + e.getMessage();
            view.put("exception", errorMessage);
            PublishTask.LOGGER.error(errorMessage, e);
        }
        return new DefaultGoPluginApiResponse(responseCode, PublishTask.GSON.toJson(view));

    }

}
