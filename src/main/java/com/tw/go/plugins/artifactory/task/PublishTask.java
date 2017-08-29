package com.tw.go.plugins.artifactory.task;

import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.tw.go.plugins.artifactory.Logger;
import com.tw.go.plugins.artifactory.task.config.GetConfigRequest;
import com.tw.go.plugins.artifactory.task.executor.ExecuteRequest;
import com.tw.go.plugins.artifactory.task.validation.ValidateRequest;
import com.tw.go.plugins.artifactory.task.view.GetViewRequest;

@Extension
public class PublishTask implements GoPlugin {
	
	public final static String ARTIFACTORY_URI_PROPERTY = "ArtifactoryUri";
	public final static String URI_IS_FOLDER_PROPERTY = "UriIsFolder";
	public final static String ARTIFACTORY_PATH_PROPERTY = "ArtifactoryPath";    
    public static final Gson GSON = new GsonBuilder().serializeNulls().create();
    public static Logger LOGGER = Logger.getLogger(PublishTask.class);


	@Override
	public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
		// TODO Auto-generated method stub		
	}

	@Override
	public GoPluginApiResponse handle(GoPluginApiRequest requestMessage) throws UnhandledRequestTypeException {
        if ("configuration".equals(requestMessage.requestName())) {
            return new GetConfigRequest().execute();
        } else if ("validate".equals(requestMessage.requestName())) {
            return new ValidateRequest().execute(requestMessage);
        } else if ("execute".equals(requestMessage.requestName())) {
            return new ExecuteRequest().execute(requestMessage);
        } else if ("view".equals(requestMessage.requestName())) {
            return new GetViewRequest().execute();
        }
        throw new UnhandledRequestTypeException(requestMessage.requestName());	
	}

	@Override
	public GoPluginIdentifier pluginIdentifier() {
		return new GoPluginIdentifier("task", Arrays.asList("1.0"));
	}
}
