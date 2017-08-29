package com.tw.go.plugins.artifactory.task.config;

import java.util.HashMap;

import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugins.artifactory.task.PublishTask;

public class GetConfigRequest {
		
    public GoPluginApiResponse execute() {
    	 HashMap<String, Object> config = new HashMap<>();

         HashMap<String, Object> artifactoryUri = new HashMap<>();
         artifactoryUri.put("display-order", "0");
         artifactoryUri.put("display-name", "Artifactory Uri");
         artifactoryUri.put("required", true);
         config.put(PublishTask.ARTIFACTORY_URI_PROPERTY, artifactoryUri);

         HashMap<String, Object> uriIsFolder = new HashMap<>();
         uriIsFolder.put("default-value", PublishTask.URI_IS_FOLDER_PROPERTY);
         uriIsFolder.put("display-order", "1");
         uriIsFolder.put("display-name", "Uri is folder");
         uriIsFolder.put("required", false);
         config.put(PublishTask.URI_IS_FOLDER_PROPERTY, uriIsFolder);

         HashMap<String, Object> artifactoryPath = new HashMap<>();
         artifactoryPath.put("default-value", PublishTask.ARTIFACTORY_PATH_PROPERTY);
         artifactoryPath.put("display-order", "2");
         artifactoryPath.put("display-name", "Artifactory Path");
         artifactoryPath.put("required", true);
         config.put(PublishTask.ARTIFACTORY_PATH_PROPERTY, artifactoryPath);


         return DefaultGoPluginApiResponse.success(PublishTask.GSON.toJson(config));
    }
	
	

}
