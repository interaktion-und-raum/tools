import de.hfkbremen.netzwerk.*;
import netP5.*;
import oscP5.*;

NetzwerkClient mClient;
void settings() {
    size(640, 480);
}
void setup() {
    mClient = new NetzwerkClient(this, "localhost", "client");
}
void draw() {
    background(255);
}
void keyPressed() {
    if (key == ',') {
        mClient.disconnect();
    }
    if (key == '.') {
        mClient.connect();
    }
}
void mousePressed() {
    mClient.send("random", random(255));
}
/*
 * if the following three `receive` methods are implemented they will be
 * called in case a message from the server is received with eiter one, two
 * or three parameters.
 */
void receive(String name, String tag, float x) {
    println("### received: " + name + " - " + tag + " - " + x);
}
void receive(String name, String tag, float x, float y) {
    println("### received: " + name + " - " + tag + " - " + x + ", " + y);
}
void receive(String name, String tag, float x, float y, float z) {
    println("### received: " + name + " - " + tag + " - " + x + ", " + y + ", " + z);
}
