package de.hfkbremen.netzwerk.examples;

import de.hfkbremen.netzwerk.NetzwerkClient;
import processing.core.PApplet;

public class SketchExample04ParseData extends PApplet {

    private NetzwerkClient mClient;

    private float mBackgroundColor;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mClient = new NetzwerkClient(this, "localhost", "client");
        mClient.connect();
    }

    public void draw() {
        /* draw the last message received */
        background(mBackgroundColor);
    }

    public void mousePressed() {
        mClient.send("random", random(255));
    }

    public void receive(String name, String tag, float x) {
        println("### received: " + name + " - " + tag + " - " + x);

        /*
         * we are only interested in messages from `client` with the tag `random`. all other messages are ignored. note
         * that messages sent by a client are also received by the same client.
         */
        if (name.equals("client") && tag.equals("random")) {
            mBackgroundColor = x;
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample04ParseData.class.getName());
    }
}
