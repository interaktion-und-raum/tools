import de.hfkbremen.netzwerk.*;
import netP5.*;
import oscP5.*;

NetzwerkClient mClient;
long mTimer = 0;
long mDuration = 0;
boolean mPingReturned = true;
int mLineCounter = 0;
void settings() {
    size(640, 480);
}
void setup() {
    frameRate(15);
    textFont(createFont("Courier", 10));
    background(255);
    mClient = new NetzwerkClient(this, "itp2017.local", "client");
    mClient.connect();
}
void draw() {
    /* draw latency */
    float mDurationLine = height - map(mDuration,0, 20000, 0, height);
    stroke(255);
    line(mLineCounter, 0, mLineCounter, mDurationLine);
    stroke(0);
    line(mLineCounter, mDurationLine, mLineCounter, height);
    mLineCounter++;
    mLineCounter %= width;
    /* write latency to screen */
    noStroke();
    fill(255);
    rect(0, 0, 91, 82);
    fill(0);
    text("------------" + "\n" + "PING LATENCY" + "\n" + mDuration + "\n" + "" + "MICROSECONDS" + "\n" + "------------" + "\n",
         10,
         15);
    /* send new ping request */
    if (mPingReturned) {
        mTimer = System.nanoTime();
        mClient.ping();
        mPingReturned = false;
    }
}
void ping() {
    mDuration = (System.nanoTime() - mTimer) / 1000;
    mPingReturned = true;
}
