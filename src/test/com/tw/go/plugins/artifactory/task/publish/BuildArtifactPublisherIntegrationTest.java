package com.tw.go.plugins.artifactory.task.publish;

import static com.tw.go.plugins.artifactory.testutils.FilesystemUtils.delete;
import static com.tw.go.plugins.artifactory.testutils.FilesystemUtils.path;
import static com.tw.go.plugins.artifactory.testutils.FilesystemUtils.read;
import static com.tw.go.plugins.artifactory.testutils.FilesystemUtils.write;
import static java.lang.System.getProperty;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.tw.go.plugins.artifactory.task.executor.TaskExecutionContextBuilder;
import com.tw.go.plugins.artifactory.task.publish.BuildArtifactPublisher;
import com.tw.go.plugins.artifactory.task.publish.Publishable;

public class BuildArtifactPublisherIntegrationTest {
    private static String pluginId = "com.tw.go.plugins.go-artifactory-plugin";

    private String tempFilePath = path(getProperty("java.io.tmpdir"), pluginId, "name");

    private TaskExecutionContext context = new TaskExecutionContextBuilder()
            .withWorkingDir(getProperty("java.io.tmpdir"))
            .build();

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
        delete(new File(getProperty("java.io.tmpdir"), pluginId));
    }
}