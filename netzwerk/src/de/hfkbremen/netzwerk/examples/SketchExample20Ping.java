package de.hfkbremen.netzwerk.examples;

import de.hfkbremen.netzwerk.NetzwerkClient;
import processing.core.PApplet;

public class SketchExample20Ping extends PApplet {

    private NetzwerkClient mClient;

    private long mTimer = 0;

    private long mDuration = 0;

    private boolean mPingReturned = true;

    private int mLineCounter = 0;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        frameRate(15);

        textFont(createFont("Courier", 10));
        background(255);

        mClient = new NetzwerkClient(this, "itp2017.local", "client");
        mClient.connect();
    }

    public void draw() {
        /* draw latency */
        float mDurationLine = height - map(mDuration,0, 20000, 0, height);
        stroke(255);
        line(mLineCounter, 0, mLineCounter, mDurationLine);
        stroke(0);
        line(mLineCounter, mDurationLine, mLineCounter, height);
        mLineCounter++;
        mLineCounter %= width;

        /* write latency to screen */
        noStroke();
        fill(255);
        rect(0, 0, 91, 82);
        fill(0);
        text("------------" + "\n" + "PING LATENCY" + "\n" + mDuration + "\n" + "" + "MICROSECONDS" + "\n" + "------------" + "\n",
             10,
             15);

        /* send new ping request */
        if (mPingReturned) {
            mTimer = System.nanoTime();
            mClient.ping();
            mPingReturned = false;
        }
    }

    public void ping() {
        mDuration = (System.nanoTime() - mTimer) / 1000;
        mPingReturned = true;
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample20Ping.class.getName());
    }
}