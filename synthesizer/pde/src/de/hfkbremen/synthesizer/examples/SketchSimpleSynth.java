package de.hfkbremen.synthesizer.examples;

import de.hfkbremen.synthesizer.Scale;
import de.hfkbremen.synthesizer.Synthesizer;
import processing.core.PApplet;

public class SketchSimpleSynth extends PApplet {

    private final Synthesizer mSynth = Synthesizer.getSynth();

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        background(255);
    }

    public void draw() {
    }

    public void mousePressed() {
        int mNote = Scale.note(Scale.MAJOR_CHORD_7, Scale.NOTE_A3, (int) random(0, 10));
        mSynth.noteOn(mNote, 127);
    }

    public void mouseReleased() {
        mSynth.noteOff();
    }

    public static void main(String[] args) {
        PApplet.main(SketchSimpleSynth.class.getName());
    }
}
