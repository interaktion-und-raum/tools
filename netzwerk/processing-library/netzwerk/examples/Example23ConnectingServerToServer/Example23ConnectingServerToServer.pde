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
void mousePressed() {
    mClient.send("random", random(255));
}
void keyPressed() {
    /* connect to or disconnect from server by pressing `.` or `,` */
    if (key == ',') {
        mClient.disconnect();
    }
    if (key == '.') {
        mClient.connect();
    }
    if (key == ' ') {
        mClient.connect_server("192.168.1.6", 32001);
    }
}
