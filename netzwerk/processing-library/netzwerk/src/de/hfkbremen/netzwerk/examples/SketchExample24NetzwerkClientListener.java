package de.hfkbremen.netzwerk.examples;

import de.hfkbremen.netzwerk.NetzwerkClient;
import de.hfkbremen.netzwerk.NetzwerkClientListener;
import oscP5.OscMessage;
import processing.core.PApplet;

public class SketchExample24NetzwerkClientListener extends PApplet {

    private NetzwerkClient mClient;
    private int mBackgroundColor = 0;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        NetzwerkClientListener mListener = new MyNetzwerkClientListener();
        mClient = new NetzwerkClient(mListener, "localhost", "client");
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

    public class MyNetzwerkClientListener implements NetzwerkClientListener {

        @Override
        public void receive(String name, String tag, float x) {
            println("### received: " + name + " - " + tag + " - " + x);
            mBackgroundColor = (int) x;
        }

        @Override
        public void receive(String name, String tag, float x, float y) {
            println("### received: " + name + " - " + tag + " - " + x + ", " + y);
            mBackgroundColor = (int) x;
        }

        @Override
        public void receive(String name, String tag, float x, float y, float z) {
            println("### received: " + name + " - " + tag + " - " + x + ", " + y + ", " + z);
            mBackgroundColor = (int) x;
        }

        @Override
        public void receive(String name, String tag, String message) {
        }

        @Override
        public void receive_raw(OscMessage m) {
        }

        @Override
        public void receive_ping() {
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample24NetzwerkClientListener.class.getName());
    }
}
