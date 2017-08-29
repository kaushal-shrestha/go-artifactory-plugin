package com.tw.go.plugins.artifactory.task.config;

import java.util.Map;

import com.tw.go.plugins.artifactory.task.PublishTask;

public class TaskConfig {
	
	private final String artifactoryUri;
	private final boolean uriIsFolder;
	private final String artifactPath;
	
	public TaskConfig(Map<?, ?> config) {
		
		this.artifactoryUri = getValue(config, PublishTask.ARTIFACTORY_URI_PROPERTY);
		this.artifactPath = getValue(config, PublishTask.ARTIFACTORY_PATH_PROPERTY);
		String uriIfFolderAsString = getValue(config, PublishTask.URI_IS_FOLDER_PROPERTY);
		if( Boolean.valueOf(uriIfFolderAsString) ) {
			this.uriIsFolder = true;
		}else{
			this.uriIsFolder = false;
		}
			
	}
	
	  private String getValue(Map<?, ?> config, String property) {
	        return (String) ((Map<?, ?>) config.get(property)).get("value");
	  }

	public String getArtifactoryUri() {
		return artifactoryUri;
	}

	public boolean getUriIsFolder() {
		return uriIsFolder;
	}

	public String getArtifactPath() {
		return artifactPath;
	}
	
	public boolean isFolder() {
		
		return uriIsFolder;
	}
	

}
