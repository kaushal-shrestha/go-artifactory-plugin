package com.tw.go.plugins.artifactory.task.executor;

import static com.thoughtworks.webstub.StubServerFacade.newServer;
import static com.thoughtworks.webstub.dsl.builders.ResponseBuilder.response;
import static com.tw.go.plugins.artifactory.testutils.FilesystemUtils.delete;
import static com.tw.go.plugins.artifactory.testutils.FilesystemUtils.path;
import static com.tw.go.plugins.artifactory.testutils.FilesystemUtils.read;
import static com.tw.go.plugins.artifactory.testutils.MapBuilder.map;
import static org.truth0.Truth.ASSERT;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.webstub.StubServerFacade;
import com.thoughtworks.webstub.dsl.HttpDsl;
import com.tw.go.plugins.artifactory.model.GoArtifactFactory;
import com.tw.go.plugins.artifactory.model.GoBuildDetailsFactory;
import com.tw.go.plugins.artifactory.model.Result;
import com.tw.go.plugins.artifactory.task.config.Context;
import com.tw.go.plugins.artifactory.task.config.TaskConfig;
import com.tw.go.plugins.artifactory.task.publish.BuildArtifactPublisher;

public class PublishTaskExecutorIntegrationTest {
    private static StubServerFacade server;
    private static HttpDsl artifactoryStub;
    private PublishTaskExecutor executor;
    private String pluginDirectory;

    @BeforeClass
    public static void beforeAll() throws IOException {
        server = newServer(8888);
        artifactoryStub = server.withContext("/");
        server.start();
    }

    @Before
    public void beforeEach() {
        artifactoryStub.reset();
        artifactoryStub.get("/api/system/version").returns(response(DefaultGoApiResponse.SUCCESS_RESPONSE_CODE).withContent("{ \"version\" : \"3.2.1.1\" }"));

        executor = new PublishTaskExecutor(new GoArtifactFactory(), new GoBuildDetailsFactory(), new BuildArtifactPublisher());
        pluginDirectory = path(System.getProperty("java.io.tmpdir"), "com.tw.go.plugins.go-artifactory-plugin");
    }

    @Test
    public void shouldUploadArtifactAndBuildDetails() throws IOException {

        TaskConfig config = new TaskConfig("test-repo/path/to/artifact.ext",false, "src/test/resources/artifact.txt");
        
        Map<?,?> envVars = map("ARTIFACTORY_URL", "http://localhost:8888")
                .and("ARTIFACTORY_USER", "admin")
                .and("ARTIFACTORY_PASSWORD", "password")
                .and("GO_SERVER_URL", "http://localhost:8153/go")
                .and("GO_PIPELINE_NAME", "pipeline")
                .and("GO_PIPELINE_COUNTER", "1")
                .and("GO_STAGE_COUNTER", "3");
        
        Context context = new Context(envVars , System.getProperty("java.io.tmpdir") );
        

        File testResponse = new File(path("src", "test", "resources", "uploadResponse.json"));
        artifactoryStub.put("/test-repo/path/to/artifact.ext").withContent("content")
                .returns(response(201).withContent(read(testResponse)));

        artifactoryStub.put("/test-repo/path/to/artifact.ext.sha1").withContent("040f06fd774092478d450774f5ba30c5da78acc8").returns(response(201));
        artifactoryStub.put("/test-repo/path/to/artifact.ext.md5").withContent("9a0364b9e99bb480dd25e1f0284c8555").returns(response(201));
        artifactoryStub.put("/api/build").withHeader("Content-Type", "application/vnd.org.jfrog.artifactory+json").returns(response(204));

        Result result = executor.execute(config, context);

        ASSERT.that(result.responseCode()).isEqualTo(DefaultGoApiResponse.SUCCESS_RESPONSE_CODE);
        ASSERT.that(new File(pluginDirectory, "uploadMetadata.json").exists()).isTrue();
    }

    @After
    public void afterEach() throws IOException {
        delete(new File(pluginDirectory));
    }

    @AfterClass
    public static void afterAll() {
        server.stop();
    }

    // Helpful for debugging
    private void enableHttpLogs() {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "DEBUG");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "ERROR");
    }
}