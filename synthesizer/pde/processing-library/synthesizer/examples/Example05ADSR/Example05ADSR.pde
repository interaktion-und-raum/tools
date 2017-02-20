import de.hfkbremen.synthesizer.*;
import controlP5.*;

Synthesizer mSynth;
Instrument mInstrument;
boolean mIsPlaying = false;
int mNote;
void settings() {
    size(640, 480);
}
void setup() {
    background(255);
    mSynth = new SynthesizerJSyn();
    /* select instrument #2 */
    mSynth.instrument(2);
    /* set ADSR parameters for current instrument */
    println(Instrument.ADSR_DIAGRAM);
    mInstrument = mSynth.instrument();
    mInstrument.attack(3.0f);
    mInstrument.decay(0.0f);
    mInstrument.sustain(1.0f);
    mInstrument.release(3.0f);
}
void draw() {
    if (mIsPlaying) {
        int mColor = (mNote - Scale.NOTE_A2) * 5 + 50;
        background(mColor);
    } else {
        background(255);
    }
    /* set get_attack for current instrument */
    final float mAttack = (float) mouseX / width;
    mInstrument.attack(mAttack);
}
void keyPressed() {
    if (key == ' ') {
        if (mIsPlaying) {
            mSynth.noteOff();
        } else {
            mNote = Scale.note(Scale.MAJOR_CHORD_7, Scale.NOTE_A2, (int) random(0, 10));
            mSynth.noteOn(mNote, 127);
        }
        mIsPlaying = !mIsPlaying;
    }
    if (key == '1') {
        mInstrument.osc_type(Instrument.SINE);
    }
    if (key == '2') {
        mInstrument.osc_type(Instrument.TRIANGLE);
    }
    if (key == '3') {
        mInstrument.osc_type(Instrument.SAWTOOTH);
    }
    if (key == '4') {
        mInstrument.osc_type(Instrument.SQUARE);
    }
    if (key == '5') {
        mInstrument.osc_type(Instrument.NOISE);
    }
}
