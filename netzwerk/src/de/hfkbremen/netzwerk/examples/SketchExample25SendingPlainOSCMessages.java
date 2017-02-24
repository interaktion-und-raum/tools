package de.hfkbremen.netzwerk.examples;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

public class SketchExample25SendingPlainOSCMessages extends PApplet {

    OscP5 oscP5;
    NetAddress mRecipientAddress;
    private float mBackgroundColor;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        oscP5 = new OscP5(this, 12000);
        mRecipientAddress = new NetAddress("127.0.0.1", 12000);
    }

    public void draw() {
        background(mBackgroundColor);
    }

    public void mousePressed() {
        OscMessage m = new OscMessage("/random");
        m.add(random(0, 255)); /* add an int to the osc message */
        oscP5.send(m, mRecipientAddress);
    }

    public void oscEvent(OscMessage m) {
        println("### received an osc message.");
        m.print();
        if (m.checkAddrPattern("/random") && m.checkTypetag("f")) {
            mBackgroundColor = m.get(0).floatValue();
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample25SendingPlainOSCMessages.class.getName());
    }
}
