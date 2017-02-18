import de.hfkbremen.netzwerk.*;
import netP5.*;
import oscP5.*;

NetzwerkClient mClient;
void settings() {
    size(640, 480);
}
void setup() {
    mClient = new NetzwerkClient(this, "localhost", "client");
    mClient.connect();
}
void draw() {
    background(255);
}
void keyPressed() {
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
void receive(String name, String tag, float x) {
    println("### received: " + name + " - " + tag + " - " + x);
}
void receive(String name, String tag, float x, float y) {
    println("### received: " + name + " - " + tag + " - " + x + ", " + y);
}
void receive(String name, String tag, float x, float y, float z) {
    println("### received: " + name + " - " + tag + " - " + x + ", " + y + ", " + z);
}
