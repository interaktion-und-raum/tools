package de.hfkbremen.ton.examples;

import de.hfkbremen.ton.Beat;
import de.hfkbremen.ton.Instrument;
import de.hfkbremen.ton.Note;
import de.hfkbremen.ton.Scale;
import de.hfkbremen.ton.Synthesizer;
import de.hfkbremen.ton.SynthesizerJSyn;
import processing.core.PApplet;

/**
 * this examples shows how to use controlP5 to control instrument paramters via visual interface.
 */
public class SketchExampleInstruments05InstrumentWithGUI extends PApplet {

    private static final int NO = -1;
    private Synthesizer mSynth;
    private controlP5.ControlP5 cp5;

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
        mSynth = new SynthesizerJSyn(Synthesizer.INSTRUMENT_WITH_OSCILLATOR_ADSR_FILTER_LFO);
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

        Beat mBeat = new Beat(this);
        mBeat.bpm(120 * 4);
    }

    public void draw() {
        background(127);
    }

    public void beat(int pBeat) {
        int mStep = mSteps[pBeat % mSteps.length];
        if (mStep != NO) {
            int mNote = Scale.note(Scale.HALF_TONE, Note.NOTE_C4, mStep);
            mSynth.noteOn(mNote, 127);
        } else {
            mSynth.noteOff();
        }
        mSynth.instrument().filter_freq(abs(sin(radians(pBeat))) * 3000 + 200);
        Synthesizer.updateGUI(cp5, mSynth.instrument(), Synthesizer.GUI_FILTER_FREQ);
    }

    public static void main(String[] args) {
        PApplet.main(SketchExampleInstruments05InstrumentWithGUI.class.getName());
    }
}
