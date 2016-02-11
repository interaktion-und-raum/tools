import de.hfkbremen.synthesizer.*;

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
    mSynth = new SynthesizerMidi("B1"); // name of an available midi device
}
void draw() {
    if (mIsPlaying) {
        int mColor = (mNote - Scale.NOTE_A2) * 5 + 50;
        background(mColor);
    } else {
        background(255);
    }
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
