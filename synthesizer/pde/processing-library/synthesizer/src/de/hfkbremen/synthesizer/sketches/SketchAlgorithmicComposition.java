package de.hfkbremen.synthesizer.sketches;

import de.hfkbremen.synthesizer.Instrument;
import de.hfkbremen.synthesizer.Scale;
import de.hfkbremen.synthesizer.Synthesizer;
import de.hfkbremen.synthesizer.SynthesizerJSyn;
import java.util.Timer;
import java.util.TimerTask;
import processing.core.PApplet;

public class SketchAlgorithmicComposition extends PApplet {

    private final Synthesizer mSynth = new SynthesizerJSyn();
    private final Timer mTimer = new Timer();
    private int mBeat = 0;

    public void setup() {
        size(1280, 720);
        background(255);

        /* set ADSR parameters for current instrument */
        Instrument mInstrument = mSynth.instrument();
        mInstrument.attack(0.01f);
        mInstrument.decay(0.1f);
        mInstrument.sustain(0.0f);
        mInstrument.release(0.01f);
        mInstrument.osc_type(Instrument.TRIANGLE);

        final int BPM = 120 * 4; // 16th
        final int mPeriod = (int) (60.0f / BPM * 1000.0f);
        mTimer.scheduleAtFixedRate(new StepTimerTask(), 1000, mPeriod);
    }

    public void draw() {
        background(255);
    }

    public void play() {
        if (mBeat % 2 == 0) {
            mSynth.noteOn(Scale.NOTE_A2 + (mBeat % 4) * 3, 100);
        }
        if (mBeat % 8 == 0) {
            mSynth.noteOn(Scale.NOTE_A3, 100);
        }
        if (mBeat % 32 == 0) {
            mSynth.noteOn(Scale.NOTE_A4, 100);
        }
        if (mBeat % 11 == 0) {
            mSynth.noteOn(Scale.NOTE_C4, 100);
        }
        if (mBeat % 13 == 0) {
            mSynth.noteOn(Scale.NOTE_C5, 100);
        }
    }

    class StepTimerTask extends TimerTask {

        int mLastNote = -1;

        @Override
        public void run() {
            mBeat++;
            play();
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchAlgorithmicComposition.class.getName());
    }
}
