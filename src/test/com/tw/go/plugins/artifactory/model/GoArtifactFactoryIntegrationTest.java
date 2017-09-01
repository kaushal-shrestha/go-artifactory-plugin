package com.tw.go.plugins.artifactory.model;

import static com.tw.go.plugins.artifactory.testutils.FilesystemUtils.path;
import static com.tw.go.plugins.artifactory.testutils.MapBuilder.map;
import static org.apache.commons.lang.StringUtils.join;
import static org.truth0.Truth.ASSERT;

import java.io.File;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tw.go.plugins.artifactory.task.config.Context;
import com.tw.go.plugins.artifactory.task.config.TaskConfig;

public class GoArtifactFactoryIntegrationTest {
    private static GoArtifactFactory factory;

    
    private static Context context = new Context(map("GO_REVISION", "123") ,System.getProperty("user.dir") );
    
    @BeforeClass
    public static void beforeAll() throws Exception {
   
        factory = new GoArtifactFactory();
    }
    
    @Test 
    public void shouldCreateGoArtifacts() {
    	
        TaskConfig config = new TaskConfig("repo/path/to/output.ext",false, path("src", "test", "resources", "artifact.txt"));

        Collection<GoArtifact> artifacts = factory.createArtifacts(config, context);

        GoArtifact expectedArtifact = goArtifact("src/test/resources/artifact.txt", "repo/path/to/output.ext");
        
        ASSERT.that(artifacts).has().exactly(expectedArtifact);
    }


    @Test
    public void shouldCreateArtifactsWithUniqueRemotePathsIfUriIsAFolder() {

    	TaskConfig config = new TaskConfig("repo/path", true, asPath("src", "test", "resources", "**{artifact.txt,test.html}"));
   
        Collection<GoArtifact> artifacts = factory.createArtifacts(config, context);

        GoArtifact artifactTxt = goArtifact("src/test/resources/artifact.txt", "repo/path/artifact.txt");
        GoArtifact testHtml = goArtifact("src/test/resources/view/test.html", "repo/path/test.html");

        ASSERT.that(artifacts).has().exactly(artifactTxt, testHtml);
    }

    @Test
    public void shouldCreateArtifactsWithSameRemotePathIfUriIsNotAFolder() {

    	TaskConfig config = new TaskConfig("repo/path", false, asPath("src", "test", "resources", "**{artifact.txt,test.html}"));
    	
        Collection<GoArtifact> artifacts = factory.createArtifacts(config, context);

        GoArtifact artifactTxt = goArtifact("src/test/resources/artifact.txt", "repo/path");
        GoArtifact testHtml = goArtifact("src/test/resources/view/test.html", "repo/path");

        ASSERT.that(artifacts).has().exactly(artifactTxt, testHtml);
    }
    
    @Test
    public void shouldSubstituteEnvironmentVariablesIntoUri() {

            
        TaskConfig config = new TaskConfig("repo/path/${GO_REVISION}", false, asPath("src", "test", "resources", "**{artifact.txt,test.html}"));
        
        
        System.out.println(context.getEnvironmentVariables());
        Collection<GoArtifact> artifacts = factory.createArtifacts(config, context);

        GoArtifact artifactTxt = goArtifact("src/test/resources/artifact.txt", "repo/path/123");
        GoArtifact testHtml = goArtifact("src/test/resources/view/test.html", "repo/path/123");

        
        ASSERT.that(artifacts).has().exactly(artifactTxt, testHtml);
    }

    private GoArtifact goArtifact(String relativePath, String uri) {
        String[] segments = relativePath.split("/");

        GoArtifact artifact = new GoArtifact(path(System.getProperty("user.dir"), segments), uri);
        return artifact;
    }

    private String asPath(String... segments) {
        return join(segments, File.separatorChar);
    }
}