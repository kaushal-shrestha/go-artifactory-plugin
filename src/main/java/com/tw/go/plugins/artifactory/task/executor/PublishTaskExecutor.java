package com.tw.go.plugins.artifactory.task.executor;

import static java.lang.String.format;

import java.util.Collection;
import java.util.Map;

import com.tw.go.plugins.artifactory.Logger;
import com.tw.go.plugins.artifactory.client.ArtifactoryClient;
import com.tw.go.plugins.artifactory.model.GoArtifact;
import com.tw.go.plugins.artifactory.model.GoArtifactFactory;
import com.tw.go.plugins.artifactory.model.GoBuildDetails;
import com.tw.go.plugins.artifactory.model.GoBuildDetailsFactory;
import com.tw.go.plugins.artifactory.model.Result;
import com.tw.go.plugins.artifactory.model.UploadMetadata;
import com.tw.go.plugins.artifactory.task.config.Context;
import com.tw.go.plugins.artifactory.task.config.TaskConfig;
import com.tw.go.plugins.artifactory.task.publish.BuildArtifactPublisher;

public class PublishTaskExecutor{
    private Logger logger = Logger.getLogger(getClass());
    private GoBuildDetailsFactory buildDetailsFactory;
    private BuildArtifactPublisher buildArtifactPublisher;
    private GoArtifactFactory artifactFactory;

    public PublishTaskExecutor(GoArtifactFactory factory, GoBuildDetailsFactory buildDetailsFactory, BuildArtifactPublisher buildArtifactPublisher) {
        this.artifactFactory = factory;
        this.buildDetailsFactory = buildDetailsFactory;
        this.buildArtifactPublisher = buildArtifactPublisher;
    }

    
    public Result execute(TaskConfig config, Context context) {
        Collection<GoArtifact> artifacts = artifactFactory.createArtifacts(config, context);
        Map<?,?> environmentVariables = context.getEnvironmentVariables();
        GoBuildDetails details = buildDetailsFactory.createBuildDetails(environmentVariables, artifacts);
        
        try (ArtifactoryClient client = createClient(environmentVariables)) {
            UploadMetadata uploadMetadata = client.uploadArtifacts(artifacts);
            client.uploadBuildDetails(details);
            buildArtifactPublisher.publish(context, uploadMetadata);
            
            String message = format("Successfully published artifacts:\n%s", asString(artifacts));
            
            return new Result(true, message);
        }
        catch (RuntimeException e) {
            String message = format("Failed to publish one or more artifact [%s]", artifacts);
            logger.error(message, e);
            Result result = new Result(false, message);
            return result;
        }
    }

    private ArtifactoryClient createClient(Map<?,?>  environmentVariables) {
        String url = (String) environmentVariables.get("ARTIFACTORY_URL");
        String user =(String) environmentVariables.get("ARTIFACTORY_USER");
        String password = (String) environmentVariables.get("ARTIFACTORY_PASSWORD");

        return new ArtifactoryClient(url, user, password);
    }

    private String asString(Collection<GoArtifact> artifacts) {
        StringBuilder result = new StringBuilder();
        for (GoArtifact artifact : artifacts) {
            result.append(format("%s => %s", artifact.localPath(), artifact.remotePath()));
            result.append("\n");
        }
        return result.toString();
    }

}
