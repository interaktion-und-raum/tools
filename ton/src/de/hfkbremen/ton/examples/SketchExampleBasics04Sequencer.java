package de.hfkbremen.ton.examples;

import de.hfkbremen.ton.Beat;
import de.hfkbremen.ton.Note;
import de.hfkbremen.ton.Scale;
import de.hfkbremen.ton.Ton;
import processing.core.PApplet;

/**
 * this examples shows how to implement the concept of a sequencer to repeatedly play a pattern..
 */
public class SketchExampleBasics04Sequencer extends PApplet {

    private static final int NO = -1;
    private int mNote;
    private final int[] mSteps = {
            0, NO, 12, NO,
            0, NO, 12, NO,
            0, NO, 12, NO,
            0, NO, 12, NO,
            3, 3, 15, 15,
            3, 3, 15, 15,
            5, 5, 17, 17,
            5, 5, 17, 17
    };

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        Beat.start(this, 120 * 4);
    }

    public void draw() {
        background(255);
        noStroke();
        fill(0);
        float mScale = (mNote - 48) / 36.0f + 0.1f;
        ellipse(width * 0.5f, height * 0.5f, width * mScale, width * mScale);
    }

    public void beat(int pBeat) {
        int mStep = mSteps[pBeat % mSteps.length];
        if (mStep != NO) {
            mNote = Scale.note(Scale.HALF_TONE, Note.NOTE_C4, mStep);
            System.out.println(mNote);
            Ton.noteOn(mNote, 100);
        } else {
            Ton.noteOff();
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchExampleBasics04Sequencer.class.getName());
    }
}

