package com.tw.go.plugins.artifactory.task.executor;

import java.util.Map;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugins.artifactory.model.GoArtifactFactory;
import com.tw.go.plugins.artifactory.model.GoBuildDetailsFactory;
import com.tw.go.plugins.artifactory.model.Result;
import com.tw.go.plugins.artifactory.task.PublishTask;
import com.tw.go.plugins.artifactory.task.config.Context;
import com.tw.go.plugins.artifactory.task.config.TaskConfig;
import com.tw.go.plugins.artifactory.task.publish.BuildArtifactPublisher;

public class ExecuteRequest {
    public GoPluginApiResponse execute(GoPluginApiRequest request) {
    	

        BuildArtifactPublisher buildArtifactPublisher = new BuildArtifactPublisher();
        GoArtifactFactory artifactFactory = new GoArtifactFactory();
        GoBuildDetailsFactory buildDetailsFactory = new GoBuildDetailsFactory();
        
        PublishTaskExecutor executor = new PublishTaskExecutor(artifactFactory, buildDetailsFactory, buildArtifactPublisher);
        
        Map executionRequest = (Map) new GsonBuilder().create().fromJson(request.requestBody(), Object.class);
        Map config = (Map) executionRequest.get("config");
        Map context = (Map) executionRequest.get("context");

        Result result = executor.execute(new TaskConfig(config), new Context(context));
        return new DefaultGoPluginApiResponse(result.responseCode(), PublishTask.GSON.toJson(result.toMap()));
    }
	
}
