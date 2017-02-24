package de.hfkbremen.synthesizer.applications;

import de.hfkbremen.synthesizer.Beat;
import de.hfkbremen.synthesizer.Instrument;
import de.hfkbremen.synthesizer.Scale;
import de.hfkbremen.synthesizer.Synthesizer;
import processing.core.PApplet;

public class SketchStepSequencer extends PApplet {

    private final Synthesizer mSynth = Synthesizer.getSynth("jsyn-filter+lfo");

    private Beat mBeat;

    private final int[] mSteps = {0, 0, 5, 5, 0, 0, 3, 3, 0, 0, 4, 4, 1, 2, 3, 4};

    private int mColor = 0;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        background(255);
        mBeat = new Beat(this);
        mBeat.bpm(120 * 4);
        mSynth.instrument().osc_type(Instrument.SQUARE);
    }

    public void draw() {
        background(mColor);
    }

    public void mousePressed() {
        mBeat.bpm((float) mouseX / width * 200 * 4);
    }

    public void beat(int pBeat) {
        int mStep = mSteps[pBeat % mSteps.length]; // read current value from array
        int mNote = Scale.note(Scale.MINOR_PENTATONIC, Scale.NOTE_C3, mStep); // compute note from step
        mSynth.noteOn(mNote, 127);
        mColor = mStep * 30 + 100; // compute background color from step
    }

    public static void main(String[] args) {
        PApplet.main(SketchStepSequencer.class.getName());
    }
}
