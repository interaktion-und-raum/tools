package de.hfkbremen.netzwerk.examples.deprecated;

import de.hfkbremen.netzwerk.NetzwerkClient;
import processing.core.PApplet;

public class SketchBroadcastingClient extends PApplet {

    private NetzwerkClient mClient;

    private float mBackgroundColor;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        frameRate(25);
        /*
         * create a client that connects to the server `edc.local` and specify
         * `client` as the sender.
         *
         * network party knowledge: if the server is run on the same machine as
         * the sketch you can also specify the server as `localhost` or with the
         * ip address `127.0.0.1`.
         */
        mClient = new NetzwerkClient(this, "d3Book.local", "client");
    }

    public void draw() {
        background(mBackgroundColor);
    }

    public void mousePressed() {
        /*
         * send a message with the tag `random` and a random value from 0 to
         * 255. there a two other `send` methods available with two (x, y) and
         * three (x, y, z) paramters
         */
        mClient.send("random", random(255));
    }

    public void keyPressed() {
        /* connect to or disconnect from server if the keys `.` or `,` are pressed */
        if (key == ',') {
            mClient.disconnect();
        }
        if (key == '.') {
            mClient.connect();
        }
    }

    /*
     * if the following three `receive` methods are implemented they will be
     * called in case a message from the server is received with eiter one, two
     * or three parameters.
     */
    public void receive(String name, String tag, float x) {
        println("### received: " + name + " - " + tag + " - " + x);

        /*
         * we are only interested in messages from `client` with the tag
         * `random` ( i.e. ourself )
         */
        if (name.equals("client") && tag.equals("random")) {
            mBackgroundColor = x;
        }
    }

    public void receive(String name, String tag, float x, float y) {
        println("### received: " + name + " - " + tag + " - " + x + ", " + y);
    }

    public void receive(String name, String tag, float x, float y, float z) {
        println("### received: " + name + " - " + tag + " - " + x + ", " + y + ", " + z);
    }

    public static void main(String[] args) {
        PApplet.main(SketchBroadcastingClient.class.getName());
    }
}
