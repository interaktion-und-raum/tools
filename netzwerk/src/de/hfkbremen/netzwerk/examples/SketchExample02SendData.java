package de.hfkbremen.netzwerk.examples;

import de.hfkbremen.netzwerk.NetzwerkClient;
import processing.core.PApplet;

public class SketchExample02SendData extends PApplet {

    private NetzwerkClient mClient;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mClient = new NetzwerkClient(this, "localhost", "client");
        mClient.connect();
    }

    public void draw() {
        background(255);
    }

    public void mousePressed() {
        /*
         * send a message with the tag `random` and a random value from 0 to
         * 255. there a two other `send` methods available with two (x, y) and
         * three (x, y, z) paramters
         */
        mClient.send("random", random(255));
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample02SendData.class.getName());
    }
}


