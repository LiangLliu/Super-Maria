package com.lianglliu.loader;

import java.net.URL;
import java.nio.file.Paths;

public class ResourceLoader {


    public static URL getResourceURL(String fileName) {
        return ResourceLoader.class.getClassLoader().getResource(fileName);

    }

    public static String getResourceFilePath(String fileName) {
        var resource = ResourceLoader.getResourceURL(fileName);
        if (resource == null) {
            return null;
        }
        return Paths.get(resource.getPath()).toString();
    }
}
