import de.hfkbremen.netzwerk.*;
import netP5.*;
import oscP5.*;

NetzwerkClient mClient;
float mBackgroundColor;
void settings() {
    size(640, 480);
}
void setup() {
    mClient = new NetzwerkClient(this, "", "client");
    /* no need to connect to a server, when messages are sent directly */
}
void draw() {
    /* draw the last message received */
    background(mBackgroundColor);
}
void mousePressed() {
    mClient.send_direct("localhost", "random", random(255));
}
void receive(String name, String tag, float x) {
    println("### received: " + name + " - " + tag + " - " + x);
    if (name.equals("client") && tag.equals("random")) {
        mBackgroundColor = x;
    }
}
