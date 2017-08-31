package com.tw.go.plugins.artifactory.task.config;

import java.util.Map;

public class Context {
    private final Map<?, ?> environmentVariables;
    private final String workingDir;

    public Context(Map<?, ?> context) {
        this.environmentVariables = (Map<?, ?>) context.get("environmentVariables");
        this.workingDir = (String) context.get("workingDirectory");
    }
    public Context(Map<?, ?> environmentVariables, String workingDir ) {
        this.environmentVariables =  environmentVariables;
        this.workingDir = workingDir;
    }

    public Map<?, ?> getEnvironmentVariables() {
        return environmentVariables;
    }

    public String getWorkingDir() {
        return workingDir;
    }
}