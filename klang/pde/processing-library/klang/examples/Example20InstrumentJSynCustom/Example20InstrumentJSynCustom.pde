import de.hfkbremen.klang.*;
import controlP5.*;

InstrumentJSynCustom mInstrument;
void settings() {
    size(640, 480);
}
void setup() {
    SynthesizerJSyn mSynth = new SynthesizerJSyn(Synthesizer.INSTRUMENT_EMPTY);
    mInstrument = new InstrumentJSynCustom(mSynth, 0);
}
void draw() {
    mInstrument.set_freq(mouseX);
    mInstrument.set_amp(map(mouseY, 0, height, 0.0f, 1.0f));
}
class InstrumentJSynCustom extends InstrumentJSyn {
    SineOscillator mOsc;
    InstrumentJSynCustom(SynthesizerJSyn pSynth, int pID) {
        super(pSynth, pID);
        mOsc = new SineOscillator();
        mSynth.add(mOsc);
        mOsc.amplitude.set(0);
        mOsc.frequency.set(220);
        mOsc.output.connect(0, mLineOut.input, 0);
        mOsc.output.connect(0, mLineOut.input, 1);
    }
    void set_amp(float pAmp) {
        mAmp = pAmp;
        mOsc.amplitude.set(mAmp);
    }
    void set_freq(float freq) {
        mFreq = freq;
        mOsc.frequency.set(mFreq);
    }
}
