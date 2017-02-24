import de.hfkbremen.netzwerk.*;
import netP5.*;
import oscP5.*;

OscP5 oscP5;
NetAddress mRecipientAddress;
float mBackgroundColor;
void settings() {
    size(640, 480);
}
void setup() {
    oscP5 = new OscP5(this, 12000);
    mRecipientAddress = new NetAddress("127.0.0.1", 12000);
}
void draw() {
    background(mBackgroundColor);
}
void mousePressed() {
    OscMessage m = new OscMessage("/random");
    m.add(random(0, 255)); /* add an int to the osc message */
    oscP5.send(m, mRecipientAddress);
}
void oscEvent(OscMessage m) {
    println("### received an osc message.");
    m.print();
    if (m.checkAddrPattern("/random") && m.checkTypetag("f")) {
        mBackgroundColor = m.get(0).floatValue();
    }
}
