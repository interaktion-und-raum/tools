import de.hfkbremen.netzwerk.*;
import netP5.*;
import oscP5.*;

controlP5.ControlP5 cp5;
NetzwerkClient mClient;
boolean onTop = true;
controlP5.Textarea myTextarea;
void settings() {
    size(displayWidth / 2 - 10, 150);
}
void setup() {
    frameRate(2);
    mClient = new NetzwerkClient(this, "localhost", "OSChatLurker");
    cp5 = new controlP5.ControlP5(this);
    cp5.addToggle("onTop").setPosition(displayWidth / 2 - 40, 10).setSize(20, 20);
    myTextarea = cp5.addTextarea("txt")
                    .setText("MSGs\n")
                    .append("---")
                    .setPosition(10, 10)
                    .setSize(width - 50,
                             height - 20)
                    .setLineHeight(11)
                    .showScrollbar()
                    .setColor(color(128))
                    .setColorBackground(color(255, 100))
                    .setColorForeground(color(255, 100));
}
void draw() {
    background(0);
    surface.setAlwaysOnTop(onTop);
}
void receive(String name, String tag, String message) {
    String mText = timestamp() + " " + name + " : " + message;
    myTextarea.append("\n" + mText).scroll(1);
}
String timestamp() {
    return "[" + nf(hour(), 2) + ":" + nf(minute(), 2) + ":" + nf(second(), 2) + "]";
}
