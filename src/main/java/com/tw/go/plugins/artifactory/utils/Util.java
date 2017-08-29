package com.tw.go.plugins.artifactory.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public class Util {

    public static String readResource(String resourceFile) {
        try (InputStreamReader reader = new InputStreamReader(Util.class.getResourceAsStream(resourceFile), StandardCharsets.UTF_8)) {
            return IOUtils.toString(reader);
        } catch (IOException e) {
            throw new RuntimeException("Could not find resource " + resourceFile, e);
        }
    }


}