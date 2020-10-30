package de.hfkbremen.ton;

import java.util.ArrayList;

public class Synthesizer {

    private static SynthesizerManager mSynthesizer = null;

    public static void init(String... pName) {
        mSynthesizer = SynthesizerManager.createSynth(pName);
    }

    public static void noteOn(int note, int velocity) {
        if (mSynthesizer == null) { init(); }
        mSynthesizer.noteOn(note, velocity);
    }

    public static void noteOff(int note) {
        if (mSynthesizer == null) { init(); }
        mSynthesizer.noteOff(note);
    }

    public static void noteOff() {
        if (mSynthesizer == null) { init(); }
        mSynthesizer.noteOff();
    }

    public static void control_change(int pCC, int pValue) {
        if (mSynthesizer == null) { init(); }
        mSynthesizer.control_change(pCC, pValue);
    }

    public static void pitch_bend(int pValue) {
        if (mSynthesizer == null) { init(); }
        mSynthesizer.pitch_bend(pValue);
    }

    public static boolean isPlaying() {
        if (mSynthesizer == null) { init(); }
        return mSynthesizer.isPlaying();
    }

    public static Instrument instrument(int pInstrumentID) {
        if (mSynthesizer == null) { init(); }
        return mSynthesizer.instrument(pInstrumentID);
    }

    public static Instrument instrument() {
        if (mSynthesizer == null) { init(); }
        return mSynthesizer.instrument();
    }

    public static ArrayList<? extends Instrument> instruments() {
        if (mSynthesizer == null) { init(); }
        return mSynthesizer.instruments();
    }
}
