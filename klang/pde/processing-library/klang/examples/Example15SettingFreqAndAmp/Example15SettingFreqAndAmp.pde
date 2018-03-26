import de.hfkbremen.klang.*; 
import controlP5.*; 
import ddf.minim.*; 
import com.jsyn.unitgen.*; 


final SynthesizerJSyn mSynth = new SynthesizerJSyn(Synthesizer.INSTRUMENT_EMPTY);
InstrumentJSynOscillator mInstrument;
void settings() {
    size(640, 480);
}
void setup() {
    background(255);
    mInstrument = new InstrumentJSynOscillator(mSynth, 0);
    mInstrument.osc_type(Instrument.SAWTOOTH);
}
void draw() {
    float mFreq = 110 + 330 * mouseX / (float) width;
    float mAmp = mouseY / (float) height;
    mInstrument.set_freq(mFreq);
    mInstrument.set_amp(mAmp);
    background(mAmp * 255);
}
