package de.hfkbremen.netzwerk.examples.deprecated;

import de.hfkbremen.netzwerk.NetzwerkClient;
import processing.core.PApplet;
import processing.core.PFont;

public class SketchKeyPressOverOSC extends PApplet {

    private NetzwerkClient mClient;

    private PFont font;

    private char lastKey = ' ';

    public void settings() {
        size(300, 300);
    }

    public void setup() {
        mClient = new NetzwerkClient(this, "edc.local", "keyPressor");
        font = createFont("Times", 200);
        textAlign(CENTER, CENTER);
        textFont(font);
    }

    public void draw() {
        background(23, 68, 250);
        text(lastKey, width / 2, height / 2 - 15);
    }

    public void keyPressed() {
        /* send keyPresses as ASCII values: www.asciitable.com */
        println("asciivalue: " + (int) (key));
        mClient.send("keyPress", key);
    }

    public void receive(String name, String tag, float x) {
        //println("### received: " + name + " - " + tag + " - " + x);
        if (tag.equals("keyPress")) {
            lastKey = (char) (x);
            if (x == 'a') {
                println(name + " sent keyPress 'a'");
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchKeyPressOverOSC.class.getName());
    }
}
