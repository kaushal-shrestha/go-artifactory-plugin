package com.tw.go.plugins.artifactory.model;

import static com.google.common.collect.Collections2.transform;

import java.io.File;
import java.util.Collection;

import org.apache.commons.lang.text.StrSubstitutor;

import com.google.common.base.Function;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import com.tw.go.plugins.artifactory.task.config.Context;
import com.tw.go.plugins.artifactory.task.config.TaskConfig;
import com.tw.go.plugins.artifactory.utils.DirectoryScanner;

public class GoArtifactFactory {
	
	

    public Collection<GoArtifact> createArtifacts(final TaskConfig config, Context context) {

        DirectoryScanner scanner = new DirectoryScanner(context.getWorkingDir());
        // TODO: ESSE PATH AQUI É O DO ARTIFACTORY, PRECISO FAZER OS CHECKS DE SEGURANÇA NELE
        Collection<File> files = scanner.scan(config.getArtifactPath());

        return transform(files, goArtifact(config.getArtifactoryUri(), config, context));
    }

    private Function<File, GoArtifact> goArtifact(final String artifactoryUri, final TaskConfig config, final Context context) {
    	
    	
        return new Function<File, GoArtifact>() {
            @Override
        
            public GoArtifact apply(File file) {
                GoArtifact artifact = new GoArtifact(file.getAbsolutePath(), artifactUri(file.getName()));
                
                return artifact;
            }

            private String artifactUri(String artifactName) {
                String uri = config.isFolder() ? config.getArtifactoryUri()+ "/" + artifactName : config.getArtifactoryUri();
                //TODO VER ISSO AQUI QUE N SEI SE O MAP RETORNADO É O MESMO.
                StrSubstitutor sub = new StrSubstitutor(context.getEnvironmentVariables());
                return sub.replace(uri);
            }
            
        };
    }
}
