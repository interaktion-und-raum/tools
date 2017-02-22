import de.hfkbremen.netzwerk.*;
import netP5.*;
import oscP5.*;

NetzwerkClient mClient;
float mMouseX;
float mMouseY;
float mSeconds;
void setup() {
    size(15, 15);
    frameRate(1);
    mClient = new NetzwerkClient(this, "localhost", "mouse-time");
}
void draw() {
}
void receive(String name, String tag, float x, float y, float z) {
    /* only consider messages from `mouselogger` with tag `xyc` */
    if (name.equals("mouselogger")) {
        if (tag.equals("xyc")) {
            mMouseX = x;
            mMouseY = y;
        }
    }
    /* only consider messages from `time` with tag `local` */
    if (name.equals("time")) {
        if (tag.equals("local")) {
            /* relay messages only every 10 seconds */
            if (mSeconds % 10 == 0) {
                /* convert time to seconds: hours * 60 * 60 + minutes * 60 + seconds */
                float t = x * 60 * 60 + y * 60 + z;
                mClient.send("xyt", mMouseX, mMouseY, t);
            }
            mSeconds = z;
        }
    }
}
