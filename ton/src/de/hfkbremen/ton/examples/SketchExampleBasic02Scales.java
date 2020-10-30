package de.hfkbremen.ton.examples;

import de.hfkbremen.ton.Note;
import de.hfkbremen.ton.Scale;
import de.hfkbremen.ton.Synthesizer;
import processing.core.PApplet;

/**
 * this examples shows how to use musical scales. there are a selection of predefined scales but custom ones can also be
 * created.
 */
public class SketchExampleBasic02Scales extends PApplet {

    private static final int NO = -1;
    private int mNote;
    private int mStep;
    private int[] mScale;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mScale = Scale.HALF_TONE;
    }

    public void draw() {
        background(mNote * 2);
    }

    public void keyPressed() {
        if (key == ' ') {
            mStep++;
            mStep %= 12;
            mNote = Scale.note(mScale, Note.NOTE_C3, mStep);
            Synthesizer.noteOn(mNote, 127);
        }
        if (key == '1') {
            mScale = Scale.HALF_TONE;
        }
        if (key == '2') {
            mScale = Scale.MAJOR_CHORD;
        }
        if (key == '3') {
            mScale = Scale.MINOR_CHORD_7;
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchExampleBasic02Scales.class.getName());
    }
}
