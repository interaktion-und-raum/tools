import de.hfkbremen.netzwerk.*;
import netP5.*;
import oscP5.*;

NetzwerkClient mClient;
float mBackgroundColor;
void settings() {
    size(640, 480);
}
void setup() {
    mClient = new NetzwerkClient(this, "localhost", "client");
}
void draw() {
    /* draw the last message received */
    background(mBackgroundColor);
}
void mousePressed() {
    mClient.send("random", random(255));
}
void keyPressed() {
    if (key == ',') {
        mClient.disconnect();
    }
    if (key == '.') {
        mClient.connect();
    }
}
void receive(String name, String tag, float x) {
    println("### received: " + name + " - " + tag + " - " + x);
    /*
     * we are only interested in messages from `client` with the tag `random`. all other messages are ignored. note
     * that messages sent by a client are also received by the same client.
     */
    if (name.equals("client") && tag.equals("random")) {
        mBackgroundColor = x;
    }
}
void receive(String name, String tag, float x, float y) {
    println("### received: " + name + " - " + tag + " - " + x + ", " + y);
}
void receive(String name, String tag, float x, float y, float z) {
    println("### received: " + name + " - " + tag + " - " + x + ", " + y + ", " + z);
}
