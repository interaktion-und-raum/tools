package de.hfkbremen.klang.examples;

import de.hfkbremen.klang.Beat;
import de.hfkbremen.klang.Note;
import de.hfkbremen.klang.Scale;
import de.hfkbremen.klang.Synthesizer;
import de.hfkbremen.klang.SynthesizerJSyn;
import processing.core.PApplet;

/**
 * his examples shows how to implement the concept of a sequencer to repeatedly play a pattern..
 */
public class SketchExample14Sequencer extends PApplet {

    private static final int NO = -1;
    private Synthesizer mSynth;
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
        mSynth = new SynthesizerJSyn();

        Beat mBeat = new Beat(this);
        mBeat.bpm(120 * 4);
    }

    public void draw() {
        background(mNote * 2);
    }

    public void beat(int pBeat) {
        int mStep = mSteps[pBeat % mSteps.length];
        if (mStep != NO) {
            mNote = Scale.note(Scale.HALF_TONE, Note.NOTE_C4, mStep);
            mSynth.noteOn(mNote, 127);
        } else {
            mSynth.noteOff();
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample14Sequencer.class.getName());
    }
}

