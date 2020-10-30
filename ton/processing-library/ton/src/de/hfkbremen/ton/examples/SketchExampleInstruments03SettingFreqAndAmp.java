package de.hfkbremen.ton.examples;

import de.hfkbremen.ton.Instrument;
import de.hfkbremen.ton.InstrumentJSynOscillator;
import de.hfkbremen.ton.Synthesizer;
import de.hfkbremen.ton.SynthesizerJSyn;
import processing.core.PApplet;

public class SketchExampleInstruments03SettingFreqAndAmp extends PApplet {

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
        mInstrument.frequency(mFreq);
        mInstrument.amplitude(mAmp);

        background(mAmp * 255);
    }

    public static void main(String[] args) {
        PApplet.main(SketchExampleInstruments03SettingFreqAndAmp.class.getName());
    }
}
