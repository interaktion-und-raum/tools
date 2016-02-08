package de.hfkbremen.synthesizer.examples;

import controlP5.ControlP5;
import de.hfkbremen.synthesizer.Beat;
import de.hfkbremen.synthesizer.Instrument;
import de.hfkbremen.synthesizer.Scale;
import de.hfkbremen.synthesizer.Synthesizer;
import processing.core.PApplet;

public class SketchInstrumentWithADSRAndControlP5 extends PApplet {

    private Synthesizer mSynth;

    private ControlP5 cp5;

    private Beat mBeat;

    private static final int NO = -1;

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
        mSynth = Synthesizer.getSynth("jsyn-adv"); // *jsyn-adv* features an LFO + a filter

        mSynth.instrument().osc_type(Instrument.SQUARE);
        mSynth.instrument().attack(0.01f);
        mSynth.instrument().decay(0.2f);
        mSynth.instrument().sustain(0.0f);
        mSynth.instrument().release(0.0f);
        mSynth.instrument().lfo_amp(12.0f);
        mSynth.instrument().lfo_freq(64.0f);
        mSynth.instrument().filter_q(3.0f);
        mSynth.instrument().filter_freq(2048.0f);

        cp5 = Synthesizer.createInstrumentsGUI(this, mSynth, 0);

        mBeat = new Beat(this);
        mBeat.bpm(120 * 4);
    }

    public void draw() {
        background(127);
    }

    public void beat(int pBeat) {
        int mStep = mSteps[pBeat % mSteps.length];
        if (mStep != NO) {
            int mNote = Scale.note(Scale.HALF_TONE, Scale.NOTE_C4, mStep);
            mSynth.noteOn(mNote, 127);
        } else {
            mSynth.noteOff();
        }
//        mSynth.instrument().get_filter_q(abs(sin(radians(pBeat * 4)) * 2));
        mSynth.instrument().filter_freq(abs(sin(radians(pBeat))) * 3000 + 200);
        Synthesizer.updateGUI(cp5, mSynth.instrument(), Synthesizer.GUI_FILTER_FREQ);
    }

    public static void main(String[] args) {
        PApplet.main(SketchInstrumentWithADSRAndControlP5.class.getName());
    }
}
