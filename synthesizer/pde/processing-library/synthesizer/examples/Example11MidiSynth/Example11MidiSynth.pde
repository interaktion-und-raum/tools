import de.hfkbremen.synthesizer.*;
import controlP5.*;

Synthesizer mSynth;
boolean mIsPlaying = false;
int mNote = Scale.NOTE_A2;
int mInstrument = 0;
void settings() {
    size(640, 480);
}
void setup() {
    background(255);
    SynthUtil.dumpMidiOutputDevices();
    mSynth = new SynthesizerMidi("IAC-Driver"); // name of an available midi out device
}
void draw() {
    if (mIsPlaying) {
        int mColor = (mNote - Scale.NOTE_A2) * 5 + 50;
        background(mColor);
    } else {
        background(255);
    }
}
void mouseMoved() {
    mSynth.controller(SynthesizerMidi.CC_MODULATION, (int) map(mouseX, 0, width, 0, 127));
    mSynth.pitch_bend((int) map(mouseY, 0, height, 16383, 0));
}
void keyPressed() {
    if (!mIsPlaying) {
        mNote = Scale.note(Scale.MAJOR_CHORD_7, Scale.NOTE_A2, (int) random(0, 10));
        mSynth.instrument(mInstrument);
        mSynth.noteOn(mNote, 127);
    } else {
        mSynth.noteOff();
        mInstrument = (int) random(0, 15);
    }
    mIsPlaying = !mIsPlaying;
}