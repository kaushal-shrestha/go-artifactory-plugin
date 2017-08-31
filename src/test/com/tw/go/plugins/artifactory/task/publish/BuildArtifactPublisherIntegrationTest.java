package com.tw.go.plugins.artifactory.task.publish;

import static com.tw.go.plugins.artifactory.testutils.FilesystemUtils.delete;
import static com.tw.go.plugins.artifactory.testutils.FilesystemUtils.path;
import static com.tw.go.plugins.artifactory.testutils.FilesystemUtils.read;
import static com.tw.go.plugins.artifactory.testutils.FilesystemUtils.write;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.junit.After;
import org.junit.Test;

import com.tw.go.plugins.artifactory.task.config.Context;

public class BuildArtifactPublisherIntegrationTest {
    private static String pluginId = "com.tw.go.plugins.go-artifactory-plugin";

    private String tempFilePath = path(System.getProperty("java.io.tmpdir"), pluginId, "name");

    private Context context = new Context(new HashMap(), System.getProperty("java.io.tmpdir"));
    
    private BuildArtifactPublisher publisher = new BuildArtifactPublisher();

    @Test
    public void shouldCreateBuildArtifactInGivenDirectory() throws IOException {
        Publishable publishable = new TestPublishable("name", "content");
        
        publisher.publish(context, publishable);

        assertThat(read(new File(tempFilePath)), is("content"));
    }

    @Test
    public void shouldOverwriteBuildArtifactIfOneAlreadyExists() throws IOException {
        write(tempFilePath, "content");

        Publishable publishable = new TestPublishable("name", "differentContent");
        publisher.publish(context, publishable);

        assertThat(read(new File(tempFilePath)), is("differentContent"));
    }

    @After
    public void cleanUp() throws IOException {
        delete(new File(System.getProperty("java.io.tmpdir"), pluginId));
    }
}