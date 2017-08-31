package com.tw.go.plugins.artifactory.model;

import static java.lang.String.format;

import java.util.Collection;
import java.util.Map;

import org.joda.time.DateTime;

import com.thoughtworks.go.plugin.api.task.Console;

public class GoBuildDetailsFactory {
    private static final String OBFUSCATED = "****";

    public GoBuildDetails createBuildDetails(Map<?,?> environmentVariables, Collection<GoArtifact> buildArtifacts) {
    	String goPipelineName = (String) environmentVariables.get("GO_PIPELINE_NAME");    	 
    	
    	String pipelineValuestreamURL = (String) environmentVariables.get("PIPELINE_VALUESTREAM_URL");
        GoBuildDetails buildDetails = new GoBuildDetails(goPipelineName , buildNumber(environmentVariables), DateTime.now());
        
        buildDetails.url(pipelineValuestreamURL);
        buildDetails.artifacts(buildArtifacts);
        buildDetails.environmentVariables(environmentVariables);

        return buildDetails;
    }

    private String buildNumber(Map<?,?> environmentVariables) {
    	String goPipelineCounter = (String) environmentVariables.get("GO_PIPELINE_COUNTER");
    	String goStageCounter = (String) environmentVariables.get("GO_STAGE_COUNTER");
    	
        return format("%s.%s", goPipelineCounter, goStageCounter);
    }


}
