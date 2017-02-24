package de.hfkbremen.netzwerk.examples;

import de.hfkbremen.netzwerk.NetzwerkClient;
import processing.core.PApplet;

public class SketchExample21SendingDirectMessages extends PApplet {

    private NetzwerkClient mClient;

    private float mBackgroundColor;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mClient = new NetzwerkClient(this, "", "client");
        /* no need to connect to a server, when messages are sent directly */
    }

    public void draw() {
        /* draw the last message received */
        background(mBackgroundColor);
    }

    public void mousePressed() {
        mClient.send_direct("localhost", "random", random(255));
    }

    public void receive(String name, String tag, float x) {
        println("### received: " + name + " - " + tag + " - " + x);

        if (name.equals("client") && tag.equals("random")) {
            mBackgroundColor = x;
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample21SendingDirectMessages.class.getName());
    }
}