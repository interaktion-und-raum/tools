package de.hfkbremen.netzwerk.examples;

import de.hfkbremen.netzwerk.NetzwerkClient;
import processing.core.PApplet;

public class SketchExample23ConnectingServerToServer extends PApplet {

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
        mClient.send("random", random(255));
    }

    public void keyPressed() {
        /* connect to or disconnect from server by pressing `.` or `,` */
        if (key == ',') {
            mClient.disconnect();
        }
        if (key == '.') {
            mClient.connect();
        }
        if (key == ' ') {
            mClient.connect_server("itp2017.local", 32000);
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample23ConnectingServerToServer.class.getName());
    }
}