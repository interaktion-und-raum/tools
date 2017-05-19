package de.hfkbremen.mesh;

import processing.core.PApplet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Sandbox extends PApplet {

    public void settings() {
    }

    public void setup() {
        println(System.getProperty("java.library.path"));
        println(Resource.getPath(""));
    }

    public void draw() {
    }

    public static class Resource {

        public static String getPath(String filename) {
            URL url;
            url = Resource.class.getResource(filename);
            if (url == null) {
                System.err.println("### ERROR @Resource.getPath / " + filename + " doesn t exist");
                return "";
            } else {
                String myFile = url.getFile();
                return myFile.replaceAll("%20", " ");
            }
        }

        public static String getResourceLocation() {
            return Resource.class.getResource("Resource.class").getFile();
        }

        public static InputStream getStream(String filename) {
            URL url = Resource.class.getResource(filename);
            try {
                return url.openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void main(String[] args) {
        PApplet.main(Sandbox.class.getName());
    }

}

