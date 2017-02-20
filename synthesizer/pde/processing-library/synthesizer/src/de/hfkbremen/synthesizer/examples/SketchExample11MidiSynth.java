package de.hfkbremen.synthesizer.examples;

import de.hfkbremen.synthesizer.Scale;
import de.hfkbremen.synthesizer.SynthUtil;
import de.hfkbremen.synthesizer.Synthesizer;
import de.hfkbremen.synthesizer.SynthesizerMidi;
import processing.core.PApplet;

public class SketchExample11MidiSynth extends PApplet {

    private Synthesizer mSynth;

    private boolean mIsPlaying = false;

    private int mNote = Scale.NOTE_A2;

    private int mInstrument = 0;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        background(255);
        SynthUtil.dumpMidiOutputDevices();
        mSynth = new SynthesizerMidi("IAC-Driver"); // name of an available midi out device
    }

    public void draw() {
        if (mIsPlaying) {
            int mColor = (mNote - Scale.NOTE_A2) * 5 + 50;
            background(mColor);
        } else {
            background(255);
        }
    }

    public void mouseMoved() {
        mSynth.controller(SynthesizerMidi.CC_MODULATION, (int) map(mouseX, 0, width, 0, 127));
        mSynth.pitch_bend((int) map(mouseY, 0, height, 16383, 0));
    }

    public void keyPressed() {
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

    public static void main(String[] args) {
        PApplet.main(SketchExample11MidiSynth.class.getName());
    }

}
