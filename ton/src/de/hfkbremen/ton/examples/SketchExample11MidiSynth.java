package de.hfkbremen.ton.examples;

import de.hfkbremen.ton.Note;
import de.hfkbremen.ton.Scale;
import de.hfkbremen.ton.SynthUtil;
import de.hfkbremen.ton.Synthesizer;
import de.hfkbremen.ton.SynthesizerMidi;
import processing.core.PApplet;

/**
 * this examples shows how to control a midi instrument. make sure to set up the midi configuration properly in the
 * system control panel.
 */
public class SketchExample11MidiSynth extends PApplet {

    private Synthesizer mSynth;

    private boolean mIsPlaying = false;

    private int mNote = Note.NOTE_A2;

    private final int mInstrument = 0;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        background(255);
        SynthUtil.dumpMidiOutputDevices();
        mSynth = Synthesizer.getSynth("midi", "Bus 1"); // name of an available midi out device
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