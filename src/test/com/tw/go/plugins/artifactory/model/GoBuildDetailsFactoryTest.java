package com.tw.go.plugins.artifactory.model;

import static com.tw.go.plugins.artifactory.testutils.MapBuilder.map;
import static com.tw.go.plugins.artifactory.testutils.matchers.DeepEqualsMatcher.deepEquals;
import static com.tw.go.plugins.artifactory.testutils.matchers.DeepEqualsMatcher.ignoring;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.truth0.Truth.ASSERT;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.go.plugin.api.task.Console;
import com.thoughtworks.go.plugin.api.task.EnvironmentVariables;
import com.tw.go.plugins.artifactory.GoBuildDetailsBuilder;
import com.tw.go.plugins.artifactory.model.GoArtifact;
import com.tw.go.plugins.artifactory.model.GoBuildDetails;
import com.tw.go.plugins.artifactory.model.GoBuildDetailsFactory;

public class GoBuildDetailsFactoryTest {
    private Map<String, String> envVars;
    private GoBuildDetailsFactory factory;

    @Before
    public void beforeEach() {
    	this.envVars = map("GO_PIPELINE_NAME", "pipeline")
	            .and("GO_PIPELINE_COUNTER", "pipelineCounter")
	            .and("GO_STAGE_COUNTER", "stageCounter")
	            .and("PIPELINE_VALUESTREAM_URL", "https://localhost:8154/go/pipelines/value_stream_map/pipeline/pipelineCounter" )
	            .and("GO_SERVER_URL", "https://localhost:8154/go/")
	            .and("ARTIFACTORY_PASSWORD", "passwd");

        factory = new GoBuildDetailsFactory();
    }

    @Test
    public void shouldCreateBuildDetails() {
        GoArtifact artifact = new GoArtifact("path", "uri/path");
        GoBuildDetails details = factory.createBuildDetails(envVars, asList(artifact));

        GoBuildDetails expected = new GoBuildDetailsBuilder()
                .buildName("pipeline")
                .buildNumber("pipelineCounter.stageCounter")
                .url("https://localhost:8154/go/pipelines/value_stream_map/pipeline/pipelineCounter")
                .artifact(artifact)
                .envVariables(envVars)
                .build();

        assertThat(details, deepEquals(expected, ignoring("startedAt")));
    }


}