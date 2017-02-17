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
    /* connect to or disconnect from server if the keys `.` or `,` are pressed */
    if (key == ',') {
        mClient.disconnect();
    }
    if (key == '.') {
        mClient.connect();
    }
}
void mousePressed() {
    /*
     * send a message with the tag `random` and a random value from 0 to
     * 255. there a two other `send` methods available with two (x, y) and
     * three (x, y, z) paramters
     */
    mClient.send("random", random(255));
}
