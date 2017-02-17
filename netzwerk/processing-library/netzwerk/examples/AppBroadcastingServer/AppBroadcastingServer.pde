import de.hfkbremen.netzwerk.*;
import netP5.*;
import oscP5.*;

final float mFontSize = 9;
NetzwerkServer mNetworkServer;
void settings() {
    size(480, 640);
}
void setup() {
    frameRate(15);
    textFont(createFont("Courier", mFontSize));
    fill(0);
    noStroke();
    mNetworkServer = new NetzwerkServer();
}
synchronized void draw() {
    background(255);
    fill(0);
    final float mX = 10;
    float mY = mFontSize * 3;
    text("... SERVER IP ADDRESS ... ", mX, mY);
    mY += mFontSize * 2;
    fill(0, 127, 255);
    text("    " + mNetworkServer.ip(), mX, mY);
    fill(0);
    mY += mFontSize * 2;
    text("... CONNECTED CLIENTS ... ", mX, mY);
    mY += mFontSize * 2;
    for (int i = 0; i < mNetworkServer.clients().size(); i++) {
        NetAddress mNetAddress = mNetworkServer.clients().get(i);
        text("    " + mNetAddress.address() + ":" + mNetAddress.port(), mX, mY);
        mY += mFontSize;
    }
    mY += mFontSize * 1;
    text("... LAST MESSAGES ....... ", mX, mY);
    mY += mFontSize * 2;
    OscMessage[] mMessages = mNetworkServer.messages();
    for (OscMessage m : mMessages) {
        text("    " + "| " + m.toString() + " | " + getAsString(m.arguments()), mX, mY);
        mY += mFontSize;
    }
}
void keyPressed() {
    if (key == ' ') {
        mNetworkServer.purge_clients();
        println("### purging clients");
    }
}
static String getAsString(Object[] theObject) {
    StringBuilder s = new StringBuilder();
    for (Object o : theObject) {
        if (o instanceof Float) {
            String str = nfc((Float) o, 2);
            s.append(str).append(" | ");
        } else if (o instanceof Integer) {
            String str = o.toString();
            s.append(str).append(" | ");
        }
    }
    return s.toString();
}
