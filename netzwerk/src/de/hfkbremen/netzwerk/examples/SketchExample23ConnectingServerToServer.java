package de.hfkbremen.netzwerk.examples;

import de.hfkbremen.netzwerk.NetzwerkClient;
import processing.core.PApplet;

// SERVER_PATTERN_CONNECT_SERVER

public class SketchExample23ConnectingServerToServer extends PApplet {

    private NetzwerkClient mClient;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mClient = new NetzwerkClient(this, "localhost", "client");
        mClient.connect();
        mClient.connect_server("127.0.0.1");
    }

    public void draw() {
        background(255);
    }

    public void mousePressed() {
        mClient.send("random", random(255));
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample23ConnectingServerToServer.class.getName());
    }
}