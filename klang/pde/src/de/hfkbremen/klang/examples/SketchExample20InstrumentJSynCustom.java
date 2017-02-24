package de.hfkbremen.klang.examples;

import com.jsyn.unitgen.SineOscillator;
import de.hfkbremen.klang.InstrumentJSyn;
import de.hfkbremen.klang.Synthesizer;
import de.hfkbremen.klang.SynthesizerJSyn;
import processing.core.PApplet;

public class SketchExample20InstrumentJSynCustom extends PApplet {

    private InstrumentJSynCustom mInstrument;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        SynthesizerJSyn mSynth = new SynthesizerJSyn(Synthesizer.INSTRUMENT_EMPTY);
        mInstrument = new InstrumentJSynCustom(mSynth, 0);
    }

    public void draw() {
        mInstrument.set_freq(mouseX);
        mInstrument.set_amp(map(mouseY, 0, height, 0.0f, 1.0f));
    }

    private class InstrumentJSynCustom extends InstrumentJSyn {

        private SineOscillator mOsc;

        public InstrumentJSynCustom(SynthesizerJSyn pSynth, int pID) {
            super(pSynth, pID);

            mOsc = new SineOscillator();
            mSynth.add(mOsc);
            mOsc.amplitude.set(0);
            mOsc.frequency.set(220);
            mOsc.output.connect(0, mLineOut.input, 0);
            mOsc.output.connect(0, mLineOut.input, 1);
        }

        public void set_amp(float pAmp) {
            mAmp = pAmp;
            mOsc.amplitude.set(mAmp);
        }

        public void set_freq(float freq) {
            mFreq = freq;
            mOsc.frequency.set(mFreq);
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample20InstrumentJSynCustom.class.getName());
    }
}
