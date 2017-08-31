package com.tw.go.plugins.artifactory.model;

import static com.tw.go.plugins.artifactory.testutils.FilesystemUtils.path;
import static org.apache.commons.lang.StringUtils.join;
import static org.truth0.Truth.ASSERT;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.tw.go.plugins.artifactory.task.config.Context;
import com.tw.go.plugins.artifactory.task.config.TaskConfig;
import com.tw.go.plugins.artifactory.task.config.TaskConfigBuilder;

public class GoArtifactFactoryIntegrationTest {
    private static GoArtifactFactory factory;

    private Map<String, String> properties = ImmutableMap.<String, String>builder().put("name", "value").build();
    
    		private static Context context;
    
    @BeforeClass
    public static void beforeAll() throws Exception {
        Map<String, String> envVars = new HashMap<>();
        envVars.put("GO_REVISION", "123");
        envVars.put("workingDirectory", "123");
        context = new Context(envVars);
        factory = new GoArtifactFactory();
    }
    
    @Test 
    public void shouldCreateGoArtifacts() {
    	
    	Map<String, String> enviromentVariable = new HashMap<String, String>();
    	enviromentVariable.put("ArtifactoryUri", "repo/path/to/output.ext");
    	enviromentVariable.put("UriIsFolder", "false");
    	enviromentVariable.put("ArtifactoryPath", path("src", "test", "resources", "artifact.txt"));    	
        TaskConfig config = new TaskConfig(enviromentVariable);

        Collection<GoArtifact> artifacts = factory.createArtifacts(config, context);

        GoArtifact expectedArtifact = goArtifact("src/test/resources/artifact.txt", "repo/path/to/output.ext", properties);

        ASSERT.that(artifacts).has().exactly(expectedArtifact);
    }


    @Test
    public void shouldCreateArtifactsWithUniqueRemotePathsIfUriIsAFolder() {
    	Map<String, String> enviromentVariable = new HashMap<>();
    	enviromentVariable.put("ArtifactoryUri", "repo/path");
    	enviromentVariable.put("UriIsFolder", "true");
    	enviromentVariable.put("ArtifactoryPath", asPath("src", "test", "resources", "**{artifact.txt,test.html}"));
    	TaskConfig config = new TaskConfig(enviromentVariable);
    

        Collection<GoArtifact> artifacts = factory.createArtifacts(config, context);

        GoArtifact artifactTxt = goArtifact("src/test/resources/artifact.txt", "repo/path/artifact.txt", properties);
        GoArtifact testHtml = goArtifact("src/test/resources/view/test.html", "repo/path/test.html", properties);

        ASSERT.that(artifacts).has().exactly(artifactTxt, testHtml);
    }

    @Test
    public void shouldCreateArtifactsWithSameRemotePathIfUriIsNotAFolder() {
    	Map<String, String> enviromentVariable = new HashMap<>();
    	enviromentVariable.put("ArtifactoryUri", "repo/path");
    	enviromentVariable.put("UriIsFolder", "false");
    	enviromentVariable.put("ArtifactoryPath", asPath("src", "test", "resources", "**{artifact.txt,test.html}"));
    	TaskConfig config = new TaskConfig(enviromentVariable);
    	
        Collection<GoArtifact> artifacts = factory.createArtifacts(config, context);

        GoArtifact artifactTxt = goArtifact("src/test/resources/artifact.txt", "repo/path", properties);
        GoArtifact testHtml = goArtifact("src/test/resources/view/test.html", "repo/path", properties);

        ASSERT.that(artifacts).has().exactly(artifactTxt, testHtml);
    }
    
    //@Test
    public void shouldSubstituteEnvironmentVariablesIntoUri() {

    	Map<String, String> enviromentVariable = new HashMap<>();
    	enviromentVariable.put("ArtifactoryUri", "repo/path/${GO_REVISION}");
    	enviromentVariable.put("UriIsFolder", "false");
    	enviromentVariable.put("ArtifactoryPath", asPath("src", "test", "resources", "**{artifact.txt,test.html}"));
    	TaskConfig config = new TaskConfig(enviromentVariable);
        
        Collection<GoArtifact> artifacts = factory.createArtifacts(config, context);

        GoArtifact artifactTxt = goArtifact("src/test/resources/artifact.txt", "repo/path/123", properties);
        GoArtifact testHtml = goArtifact("src/test/resources/view/test.html", "repo/path/123", properties);

        ASSERT.that(artifacts).has().exactly(artifactTxt, testHtml);
    }

    private GoArtifact goArtifact(String relativePath, String uri, Map<String, String> properties) {
        String[] segments = relativePath.split("/");

        GoArtifact artifact = new GoArtifact(path(System.getProperty("user.dir"), segments), uri);
        artifact.properties(properties);
        return artifact;
    }

    private String asPath(String... segments) {
        return join(segments, File.separatorChar);
    }
}