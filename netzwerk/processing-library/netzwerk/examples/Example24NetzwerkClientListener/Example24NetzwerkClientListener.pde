import de.hfkbremen.netzwerk.*;
import netP5.*;
import oscP5.*;

NetzwerkClient mClient;
int mBackgroundColor = 0;
void settings() {
    size(640, 480);
}
void setup() {
    NetzwerkClientListener mListener = new MyNetzwerkClientListener();
    mClient = new NetzwerkClient(mListener, "localhost", "client");
    mClient.connect();
}
void draw() {
    background(mBackgroundColor);
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
class MyNetzwerkClientListener implements NetzwerkClientListener {
    @Override
    void receive(String name, String tag, float x) {
        println("### received: " + name + " - " + tag + " - " + x);
        mBackgroundColor = (int) x;
    }
    @Override
    void receive(String name, String tag, float x, float y) {
        println("### received: " + name + " - " + tag + " - " + x + ", " + y);
        mBackgroundColor = (int) x;
    }
    @Override
    void receive(String name, String tag, float x, float y, float z) {
        println("### received: " + name + " - " + tag + " - " + x + ", " + y + ", " + z);
        mBackgroundColor = (int) x;
    }
    @Override
    void receive(String name, String tag, String message) {
    }
    @Override
    void receive_raw(OscMessage m) {
    }
    @Override
    void receive_ping() {
    }
}
