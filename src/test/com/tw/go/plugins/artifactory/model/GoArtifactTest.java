package com.tw.go.plugins.artifactory.model;

import static org.truth0.Truth.ASSERT;

import org.junit.Test;

import com.tw.go.plugins.artifactory.model.GoArtifact;

public class GoArtifactTest {
    private GoArtifact artifact = new GoArtifact("/full/path/to/artifact", "repo/path/to/artifact.ext");

    @Test
    public void shouldSplitUriIntoRepoAndArtifactPath() {
        ASSERT.that(artifact.repository()).is("repo");
        ASSERT.that(artifact.remotePath()).is("path/to/artifact.ext");
    }

    @Test
    public void shouldReturnTheLocalPath() {
        ASSERT.that(artifact.localPath()).is("/full/path/to/artifact");
    }

    @Test
    public void shouldReturnTheRemoteArtifactName() {
        ASSERT.that(artifact.remoteName()).is("artifact.ext");
    }
}
