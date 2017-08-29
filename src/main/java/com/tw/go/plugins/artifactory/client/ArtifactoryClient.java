package com.tw.go.plugins.artifactory.client;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.jfrog.build.api.Build;
import org.jfrog.build.client.ArtifactoryBuildInfoClient;
import org.jfrog.build.client.ArtifactoryUploadResponse;
import org.jfrog.build.client.DeployDetails;

import com.google.common.base.Function;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugins.artifactory.Logger;
import com.tw.go.plugins.artifactory.model.GoArtifact;

import com.tw.go.plugins.artifactory.model.GoBuildDetails;
import com.tw.go.plugins.artifactory.model.UploadMetadata;
import static org.apache.commons.lang.StringUtils.chomp;
import static com.google.common.collect.Maps.toMap;

public class ArtifactoryClient implements Closeable {
    private Logger logger = Logger.getLogger(getClass());

    // TODO: Needs a better name
    private BuildMap buildMap = new BuildMap();

    private ArtifactoryBuildInfoClient buildInfoClient;

    public ArtifactoryClient(String artifactoryUrl, String user, String password) {
        this.buildInfoClient = new ArtifactoryBuildInfoClient(artifactoryUrl, user, password, logger);
    }

    // TODO: is this really required?
    ArtifactoryClient(ArtifactoryBuildInfoClient buildInfoClient) {
        this.buildInfoClient = buildInfoClient;
    }

    public UploadMetadata uploadArtifacts(Collection<GoArtifact> artifacts) {
        ArrayList<ArtifactoryUploadResponse> responses = new ArrayList<ArtifactoryUploadResponse>();

        for ( GoArtifact artifact : artifacts) {
        	ArtifactoryUploadResponse response = upload(artifact);
        	responses.add(response);
        }
        
        return new UploadMetadata(newArrayList(responses));
    }

    public void uploadBuildDetails(GoBuildDetails details) {
        Build build = buildMap.apply(details);
        try {
            buildInfoClient.sendBuildInfo(build);
        } catch (IOException e) {
            String error = format("Unable to upload build info : %s", e.getMessage());
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }

    @Override
    public void close() {
        buildInfoClient.shutdown();
    }

    private ArtifactoryUploadResponse upload(GoArtifact artifact){

    	
    	File artifactFile = new File(artifact.localPath());
    	
        try {
            DeployDetails deployDetails = new DeployDetails.Builder()
                    .targetRepository(artifact.repository())
                    .artifactPath(artifact.remotePath())
                    .file(artifactFile)
                    .sha1(artifact.sha1())
                    .md5(artifact.md5())
                    .addProperties(removeTrailingSlashes(artifact.properties()))
                    .build();
            return buildInfoClient.deployArtifact(deployDetails);
        } catch (IOException e) {
            String error = format("Unable to upload artifact %s : %s", artifact, e.getMessage());
            logger.error(error, e);
            throw new RuntimeException(error, e);
        }
    }
    

    private Map<String, String> removeTrailingSlashes(final Map<String, String> properties) {
        return toMap(properties.keySet(), new Function<String, String>() {
            @Override
            public String apply(String key) {
                return chomp(properties.get(key), "/");
            }
        });
    }

}
