package com.lianglliu.loader;

import java.io.InputStream;
import java.net.URL;

public class ResourceLoader {

    public static URL getResourceURL(String fileName) {
        return ResourceLoader.class.getClassLoader().getResource(fileName);
    }

    public static InputStream getResource(String resource) {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(resource);
    }
}
