import de.hfkbremen.klang.*;
import controlP5.*;

final Synthesizer mSynth = Synthesizer.getSynth("jsyn-filter+lfo");
Beat mBeat;
final int[] mSteps = {0, 0, 5, 5, 0, 0, 3, 3, 0, 0, 4, 4, 1, 2, 3, 4};
int mColor = 0;
void settings() {
    size(640, 480);
}
void setup() {
    background(255);
    mBeat = new Beat(this);
    mBeat.bpm(120 * 4);
    mSynth.instrument().osc_type(Instrument.SQUARE);
}
void draw() {
    background(mColor);
}
void mousePressed() {
    mBeat.bpm((float) mouseX / width * 200 * 4);
}
void beat(int pBeat) {
    int mStep = mSteps[pBeat % mSteps.length]; // read current value from array
    int mNote = Scale.note(Scale.MINOR_PENTATONIC, Scale.NOTE_C3, mStep); // compute note from step
    mSynth.noteOn(mNote, 127);
    mColor = mStep * 30 + 100; // compute background color from step
}
