import de.hfkbremen.netzwerk.*;
import netP5.*;
import oscP5.*;

PVector mScreenSize;
PVector mPosition;
java.awt.Color mColor;
PVector mPreviousPostion;
java.awt.Robot mRobot;
NetzwerkClient mClient;
void setup() {
    size(100, 75);
    frameRate(25);
    background(255);
    try {
        mRobot = new java.awt.Robot();
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    mScreenSize = new PVector(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,
                              java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
    mPreviousPostion = new PVector();
    mPosition = new PVector();
    mColor = new java.awt.Color(0, 0, 0);
    mClient = new NetzwerkClient(this, "localhost", "mouselogger");
}
void keyPressed() {
    if (key == ',') {
        mClient.disconnect();
    }
    if (key == '.') {
        mClient.connect();
    }
}
void sendMouseMoved() {
    /* position */
    mPosition.set(java.awt.MouseInfo.getPointerInfo().getLocation().x / mScreenSize.x,
                  java.awt.MouseInfo.getPointerInfo().getLocation().y / mScreenSize.y);
    /* color */
    mColor = mRobot.getPixelColor(java.awt.MouseInfo.getPointerInfo().getLocation().x,
                                  java.awt.MouseInfo.getPointerInfo().getLocation().y);
    /* send values ( only if they have changed ) */
    if (mPosition.x != mPreviousPostion.x && mPosition.y != mPreviousPostion.y) {
        mClient.send("xyc",
                     mPosition.x,
                     mPosition.y,
                     color(mColor.getRed(), mColor.getGreen(), mColor.getBlue()));
    }
    mPreviousPostion.set(mPosition.x, mPosition.y);
}
void draw() {
    stroke(mColor.getRed(), mColor.getGreen(), mColor.getBlue());
    /* visualize */
    line(mPosition.x * width,
         mPosition.y * height,
         mPreviousPostion.x * width,
         mPreviousPostion.y * height);
    sendMouseMoved();
}
