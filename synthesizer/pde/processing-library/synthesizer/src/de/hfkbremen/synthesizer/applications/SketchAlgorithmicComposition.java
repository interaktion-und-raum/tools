package de.hfkbremen.synthesizer.applications;

import de.hfkbremen.synthesizer.Beat;
import de.hfkbremen.synthesizer.Instrument;
import de.hfkbremen.synthesizer.Scale;
import de.hfkbremen.synthesizer.Synthesizer;
import de.hfkbremen.synthesizer.SynthesizerJSyn;
import processing.core.PApplet;

public class SketchAlgorithmicComposition extends PApplet {

    private final Synthesizer mSynth = new SynthesizerJSyn();
    private int mBeatCounter = 0;
    private Beat mBeat;

    public void settings() {
        size(1280, 720);
    }

    public void setup() {
        background(255);

        /* set ADSR parameters for current instrument */
        Instrument mInstrument = mSynth.instrument();
        mInstrument.attack(0.01f);
        mInstrument.decay(0.1f);
        mInstrument.sustain(0.0f);
        mInstrument.release(0.01f);
        mInstrument.osc_type(Instrument.TRIANGLE);

        mBeat = new Beat(this, 120 * 4);
        mSynth.instrument().osc_type(Instrument.SAWTOOTH);
    }

    public void draw() {
        background(255);
    }

    public void play() {
        if (mBeatCounter % 2 == 0) {
            mSynth.noteOn(Scale.NOTE_A2 + (mBeatCounter % 4) * 3, 100);
        }
        if (mBeatCounter % 8 == 0) {
            mSynth.noteOn(Scale.NOTE_A3, 100);
        }
        if (mBeatCounter % 32 == 0) {
            mSynth.noteOn(Scale.NOTE_A4, 100);
        }
        if (mBeatCounter % 11 == 0) {
            mSynth.noteOn(Scale.NOTE_C4, 100);
        }
        if (mBeatCounter % 13 == 0) {
            mSynth.noteOn(Scale.NOTE_C5, 100);
        }
    }

    public void beat(int pBeat) {
        mBeatCounter++;
        play();
    }

    public static void main(String[] args) {
        PApplet.main(SketchAlgorithmicComposition.class.getName());
    }
}