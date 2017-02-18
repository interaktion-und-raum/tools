package de.hfkbremen.netzwerk.examples;

import de.hfkbremen.netzwerk.NetzwerkClient;
import netP5.NetAddress;
import oscP5.OscMessage;
import processing.core.PApplet;

public class SketchExample22SendingAndReceivingRawOSCMessages extends PApplet {

    private NetzwerkClient mClient;

    private float mBackgroundColor;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mClient = new NetzwerkClient(this, "", "");
        /* no need to connect to a server, when messages are send directly */
    }

    public void draw() {
        background(255);
        noStroke();
        rect(10, 10, width - 20, height - 20);
    }

    public void mousePressed() {
        OscMessage m = new OscMessage("/raw");
        m.add((int) random(255));
        m.add((int) random(255));
        m.add((int) random(255));
        m.add((int) random(255));
        /* specify IP and port of receiving machine. note that messages sent to `server` will also get relayed to connected clients. */
        NetAddress mNetAddress = new NetAddress("localhost", mClient.port());
        mClient.send_raw(m, mNetAddress);
        System.out.println("### sending : /raw iiii");
    }

    public void receive(OscMessage m) {
        if (m.addrPattern().equals("/raw") && m.typetag().equals("iiii")) {
            fill(m.get(0).intValue(), m.get(1).intValue(), m.get(2).intValue(), m.get(3).intValue());
            System.out.println("### received: /raw iiii");
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample22SendingAndReceivingRawOSCMessages.class.getName());
    }
}
