package de.hfkbremen.klang.examples;

import de.hfkbremen.klang.Note;
import de.hfkbremen.klang.Scale;
import de.hfkbremen.klang.SynthUtil;
import de.hfkbremen.klang.Synthesizer;
import de.hfkbremen.klang.SynthesizerMidi;
import processing.core.PApplet;

public class SketchExample11MidiSynth extends PApplet {

    private Synthesizer mSynth;

    private boolean mIsPlaying = false;

    private int mNote = Note.NOTE_A2;

    private int mInstrument = 0;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        background(255);
        SynthUtil.dumpMidiOutputDevices();
        mSynth = Synthesizer.getSynth("midi", "IAC-Driver"); // name of an available midi out device
    }

    public void draw() {
        if (mIsPlaying) {
            int mColor = (mNote - Note.NOTE_A2) * 5 + 50;
            background(mColor);
        } else {
            background(255);
        }
    }

    public void mouseMoved() {
        mSynth.control_change(SynthesizerMidi.CC_MODULATION, (int) map(mouseX, 0, width, 0, 127));
        mSynth.pitch_bend((int) map(mouseY, 0, height, 16383, 0));
    }

    public void keyPressed() {
        if (!mIsPlaying) {
            mNote = Scale.note(Scale.MAJOR_CHORD_7, Note.NOTE_A2, (int) random(0, 10));
            mSynth.instrument(mInstrument);
            mSynth.noteOn(mNote, 127);
        } else {
            mSynth.noteOff();
        }
        mIsPlaying = !mIsPlaying;
    }

    public static void main(String[] args) {
        PApplet.main(SketchExample11MidiSynth.class.getName());
    }

}
