package de.hfkbremen.ton.examples;

import de.hfkbremen.ton.Synthesizer;
import processing.core.PApplet;

/**
 * this examples shows how to use the default synthesizer to play notes.
 */
public class SketchExampleBasic01Notes extends PApplet {

    public void settings() {
        size(640, 480);
    }

    public void setup() {}

    public void draw() {
        background(Synthesizer.isPlaying() ? 255 : 0);
    }

    public void mousePressed() {
        int mNote = 45 + (int) random(0, 12);
        Synthesizer.noteOn(mNote, 127);
    }

    public void mouseReleased() {
        Synthesizer.noteOff();
    }

    public static void main(String[] args) {
        PApplet.main(SketchExampleBasic01Notes.class.getName());
    }
}