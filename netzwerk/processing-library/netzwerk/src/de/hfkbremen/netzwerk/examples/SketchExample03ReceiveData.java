package de.hfkbremen.netzwerk.examples;

import de.hfkbremen.netzwerk.NetzwerkClient;
import processing.core.PApplet;

public class SketchExample03ReceiveData extends PApplet {

    private NetzwerkClient mClient;
    private int mBackgroundColor = 0;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mClient = new NetzwerkClient(this, "localhost", "client");
        mClient.connect();
    }

    public void draw() {
        background(mBackgroundColor);
    }

    public void keyPressed() {
        if (key == '1') {
            mClient.send("rnd1f", random(255));
            println("### sending: rnd1f");
        } else if (key == '2') {
            mClient.send("rnd2f", random(255), random(255));
            println("### sending: rnd2f");
        } else if (key == '3') {
            mClient.send("rnd3f", random(255), random(255), random(255));
            println("### sending: rnd3f");
        }
    }

    /*
     * if the following three `receive` methods are implemented they will be
     * called in case a message from the server is received with eiter one, two
     * or three parameters.
     */
    public void receive(String name, String tag, float x) {
        println("### received: " + name + " - " + tag + " - " + x);
        mBackgroundColor = (int) x;
    }

    public void receive(String name, String tag, float x, float y) {
        println("### received: " + name + " - " + tag + " - " + x + ", " + y);
        mBackgroundColor = (int) x;
    }

    public void receive(String name, String tag, float x, float y, float z) {
        println("### received: " + name + " - " + tag + " - " + x + ", " + y + ", " + z);
        mBackgroundColor = (int) x;
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample03ReceiveData.class.getName());
    }
}
