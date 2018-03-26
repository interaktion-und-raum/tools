package de.hfkbremen.klang.examples;

import de.hfkbremen.klang.Instrument;
import de.hfkbremen.klang.InstrumentJSynOscillator;
import de.hfkbremen.klang.Synthesizer;
import de.hfkbremen.klang.SynthesizerJSyn;
import processing.core.PApplet;

public class SketchExample15SettingFreqAndAmp extends PApplet {

    private final SynthesizerJSyn mSynth = new SynthesizerJSyn(Synthesizer.INSTRUMENT_EMPTY);
    private InstrumentJSynOscillator mInstrument;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        background(255);
        mInstrument = new InstrumentJSynOscillator(mSynth, 0);
        mInstrument.osc_type(Instrument.SAWTOOTH);
    }

    public void draw() {
        float mFreq = 110 + 330 * mouseX / (float) width;
        float mAmp = mouseY / (float) height;
        mInstrument.set_freq(mFreq);
        mInstrument.set_amp(mAmp);

        background(mAmp * 255);
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample15SettingFreqAndAmp.class.getName());
    }
}
