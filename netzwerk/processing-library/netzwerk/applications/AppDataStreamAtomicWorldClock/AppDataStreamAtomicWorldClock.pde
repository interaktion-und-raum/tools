import de.hfkbremen.netzwerk.*;
import netP5.*;
import oscP5.*;

NetzwerkClient mClient;
void setup() {
    size(15, 15);
    frameRate(1);
    mClient = new NetzwerkClient(this, "localhost", "time");
}
void draw() {
    mClient.send("local", hour(), minute(), second());
}
