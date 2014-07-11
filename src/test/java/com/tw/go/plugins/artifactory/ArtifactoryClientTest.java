package com.tw.go.plugins.artifactory;

import com.tw.go.plugins.artifactory.model.GoBuildDetails;
import com.tw.go.plugins.artifactory.model.GoArtifact;
import org.jfrog.build.api.Artifact;
import org.jfrog.build.api.Build;
import org.jfrog.build.api.Module;
import org.jfrog.build.api.builder.ArtifactBuilder;
import org.jfrog.build.api.builder.ModuleBuilder;
import org.jfrog.build.client.ArtifactoryBuildInfoClient;
import org.jfrog.build.client.DeployDetails;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.truth0.Truth.ASSERT;

public class ArtifactoryClientTest {

    private ArtifactoryBuildInfoClient buildInfoClient;
    private ArtifactoryClient client;


    @Before
    public void beforeEach() {
        buildInfoClient = mock(ArtifactoryBuildInfoClient.class);
        client = new ArtifactoryClient(buildInfoClient);
    }

    @Test
    public void shouldUploadAnArtifact() throws IOException {
        String sourcePath = System.getProperty("user.dir") + "/src/test/resources/artifact.txt";

        GoArtifact artifact = new GoArtifact(sourcePath, "repo/path/to/artifact.txt");

        client.uploadArtifact(artifact);

        ArgumentCaptor<DeployDetails> captor = ArgumentCaptor.forClass(DeployDetails.class);
        verify(buildInfoClient).deployArtifact(captor.capture());

        DeployDetails deployDetails = captor.getValue();

        ASSERT.that(deployDetails.getTargetRepository()).is("repo");
        ASSERT.that(deployDetails.getArtifactPath()).is("path/to/artifact.txt");
        ASSERT.that(deployDetails.getFile().getAbsolutePath()).is(sourcePath);
    }

    @Test
    public void shouldUploadBuildDetails() throws IOException {
        Properties buildProperties = new Properties();
        buildProperties.put("a", "b");

        GoArtifact artifact = new GoArtifact("/a/b", "c/d");

        GoBuildDetails details = new GoBuildDetailsBuilder()
                .buildName("buildName")
                .buildNumber("1.2")
                .startedAt(new DateTime(2004, 12, 13, 21, 39, 45, 618, DateTimeZone.forID("Asia/Kolkata")))
                .artifact(artifact)
                .properties(buildProperties)
                .build();

        client.uploadBuildDetails(details);

        ArgumentCaptor<Build> captor = ArgumentCaptor.forClass(Build.class);
        verify(buildInfoClient).sendBuildInfo(captor.capture());

        Build build = captor.getValue();
        ASSERT.that(build.getName()).is("buildName");
        ASSERT.that(build.getNumber()).is("1.2");
        ASSERT.that(build.getStarted()).is("2004-12-13T21:39:45.618+0530");
        ASSERT.that(build.getModules().size()).is(1);
        ASSERT.that(build.getProperties()).is(buildProperties);


        List<Artifact> artifacts = build.getModule("buildName").getArtifacts();
        ASSERT.that(artifacts).isNotEmpty();
        ASSERT.that(artifacts.get(0).getName()).is("/a/b");
    }

    @Test
    public void shouldCleanlyShutdown() throws IOException {
        client.close();
        verify(buildInfoClient).shutdown();
    }
}