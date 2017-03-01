package de.hfkbremen.netzwerk.applications;

import de.hfkbremen.netzwerk.NetzwerkServer;
import netP5.NetAddress;
import oscP5.OscMessage;
import processing.core.PApplet;

public class AppBroadcastingServer extends PApplet {

    private final float mFontSize = 10;
    NetzwerkServer mServer;

    public void settings() {
        size(480, 640);
    }

    public void setup() {
        frameRate(15);
        textFont(createFont("Courier", mFontSize));
        fill(0);
        noStroke();

        mServer = new NetzwerkServer();
    }

    public synchronized void draw() {
        background(255);
        fill(0);

        final float mX = 10;
        float mY = mFontSize * 3;

        text("... SERVER IP ADDRESS ... ", mX, mY);
        mY += mFontSize * 2;
        fill(0, 127, 255);
        text("    " + mServer.ip(), mX, mY);
        fill(0);
        mY += mFontSize * 2;

        text("... CONNECTED CLIENTS ... ", mX, mY);

        mY += mFontSize * 2;
        for (int i = 0; i < mServer.clients().size(); i++) {
            NetAddress mNetAddress = mServer.clients().get(i);
            text("    " + mNetAddress.address() + ":" + mNetAddress.port(), mX, mY);
            mY += mFontSize;
        }

        mY += mFontSize * 1;
        text("... LAST MESSAGES ....... ", mX, mY);
        mY += mFontSize * 2;

        OscMessage[] mMessages = mServer.messages();
        for (OscMessage m : mMessages) {
            text("    " + "| " + m.toString() + " | " + NetzwerkServer.getAsString(m.arguments()), mX, mY);
            mY += mFontSize;
        }
    }

    public void keyPressed() {
        if (key == ' ') {
            mServer.purge_clients();
            println("### purging clients");
        }
    }

    public static void main(String[] args) {
        PApplet.main(AppBroadcastingServer.class.getName());
    }
}
