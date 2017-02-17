package de.hfkbremen.netzwerk.examples;

import de.hfkbremen.netzwerk.NetzwerkClient;
import processing.core.PApplet;

public class SketchExample03ReceiveData extends PApplet {

    private NetzwerkClient mClient;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mClient = new NetzwerkClient(this, "localhost", "client");
    }

    public void draw() {
        background(255);
    }

    public void keyPressed() {
        if (key == ',') {
            mClient.disconnect();
        }
        if (key == '.') {
            mClient.connect();
        }
    }

    public void mousePressed() {
        mClient.send("random", random(255));
    }

    /*
     * if the following three `receive` methods are implemented they will be
     * called in case a message from the server is received with eiter one, two
     * or three parameters.
     */
    public void receive(String name, String tag, float x) {
        println("### received: " + name + " - " + tag + " - " + x);
    }

    public void receive(String name, String tag, float x, float y) {
        println("### received: " + name + " - " + tag + " - " + x + ", " + y);
    }

    public void receive(String name, String tag, float x, float y, float z) {
        println("### received: " + name + " - " + tag + " - " + x + ", " + y + ", " + z);
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample03ReceiveData.class.getName());
    }
}
