package com.tw.go.plugins.artifactory.task.validation;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugins.artifactory.task.PublishTask;

public class ValidateRequest {
	
    public GoPluginApiResponse execute(GoPluginApiRequest request) {
        HashMap<String, Object> validationResult = new HashMap<>();
        int responseCode = DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE;
        Map<?, ?> configMap = (Map<?, ?>) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);
        HashMap<String, String> errorMap = new HashMap<String, String>();
       
        //TODO Better validation below:
        if (!configMap.containsKey(PublishTask.ARTIFACTORY_URI_PROPERTY) 
        	|| ((Map<?, ?>) configMap.get(PublishTask.ARTIFACTORY_URI_PROPERTY)).get("value") == null 
        	|| ((String) ((Map<?, ?>) configMap.get(PublishTask.ARTIFACTORY_URI_PROPERTY)).get("value")).trim().isEmpty()) {
            errorMap.put(PublishTask.ARTIFACTORY_URI_PROPERTY, "Artifactory URI cannot be empty");
        }
        
        if (!configMap.containsKey(PublishTask.ARTIFACTORY_PATH_PROPERTY) || ((Map<?, ?>) configMap.get(PublishTask.ARTIFACTORY_PATH_PROPERTY)).get("value") == null || 
        		((String) ((Map<?, ?>) configMap.get(PublishTask.ARTIFACTORY_PATH_PROPERTY)).get("value")).trim().isEmpty()) {
            errorMap.put(PublishTask.ARTIFACTORY_PATH_PROPERTY, "Artifact PATH cannot be empty");
        }
        
        validationResult.put("errors", errorMap);
        return new DefaultGoPluginApiResponse(responseCode, PublishTask.GSON.toJson(validationResult));
    }

}
