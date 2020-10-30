package de.hfkbremen.ton.examples;

import de.hfkbremen.ton.Beat;
import de.hfkbremen.ton.Note;
import de.hfkbremen.ton.Synthesizer;
import processing.core.PApplet;

/**
 * this examples shows how to use a beat. once instantiated the beat object calls the `beat(int)` at a defined *beats
 * per
 * minute* (bpm).
 */
public class SketchExampleBasic03Beat extends PApplet {

    private final int[] mNotes = {Note.NOTE_C3, Note.NOTE_C4, Note.NOTE_A2, Note.NOTE_A3};
    private int mBeatCount;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        Beat mBeat = new Beat(this, 120);
    }

    public void draw() {
        background((mBeatCount % 2) * 255);
    }

    public void beat(int pBeatCount) {
        mBeatCount = pBeatCount;
        int mNote = mNotes[mBeatCount % mNotes.length];
        Synthesizer.noteOn(mNote, 127);
    }

    public static void main(String[] args) {
        PApplet.main(SketchExampleBasic03Beat.class.getName());
    }
}
