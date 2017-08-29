package com.tw.go.plugins.artifactory.task.publish;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.tw.go.plugins.artifactory.Logger;
import com.tw.go.plugins.artifactory.PluginProperties;
import com.tw.go.plugins.artifactory.task.config.Context;

public class BuildArtifactPublisher {
    private Logger logger = Logger.getLogger(getClass());

    private PluginProperties plugin = new PluginProperties();

    public void publish(Context context, Publishable publishable) {
        OutputStream stream = null;

        try {
            File publishableFile = new File(pluginDir(context), publishable.name());
            stream = new FileOutputStream(publishableFile);

            IOUtils.write(publishable.content(), stream, "UTF-8");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Error creating build artifact", e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private File pluginDir(Context context) throws IOException {
        File parent = Paths.get(context.getWorkingDir(), plugin.name()).toFile();
        FileUtils.forceMkdir(parent);
        return parent;
    }
}
