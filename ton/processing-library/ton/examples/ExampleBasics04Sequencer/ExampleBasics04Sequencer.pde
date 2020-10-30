import de.hfkbremen.ton.*; 
import controlP5.*; 
import ddf.minim.*; 
import com.jsyn.unitgen.*; 


static final int NO = -1;
int mNote;
final int[] mSteps = {
        0, NO, 12, NO,
        0, NO, 12, NO,
        0, NO, 12, NO,
        0, NO, 12, NO,
        3, 3, 15, 15,
        3, 3, 15, 15,
        5, 5, 17, 17,
        5, 5, 17, 17
};
void settings() {
    size(640, 480);
}
void setup() {
    Beat.start(this, 120 * 4);
}
void draw() {
    background(255);
    noStroke();
    fill(0);
    float mScale = (mNote - 48) / 36.0f + 0.1f;
    ellipse(width * 0.5f, height * 0.5f, width * mScale, width * mScale);
}
void beat(int pBeat) {
    int mStep = mSteps[pBeat % mSteps.length];
    if (mStep != NO) {
        mNote = Scale.note(Scale.HALF_TONE, Note.NOTE_C4, mStep);
        System.out.println(mNote);
        Ton.noteOn(mNote, 100);
    } else {
        Ton.noteOff();
    }
}
