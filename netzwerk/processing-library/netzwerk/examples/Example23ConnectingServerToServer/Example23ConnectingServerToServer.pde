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
    mClient.connect_server("127.0.0.1");
}
void draw() {
    background(255);
}
void mousePressed() {
    mClient.send("random", random(255));
}
