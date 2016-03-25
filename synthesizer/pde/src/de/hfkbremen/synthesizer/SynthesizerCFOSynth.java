package de.hfkbremen.synthesizer;

import static de.hfkbremen.synthesizer.SynthUtil.*;
import java.util.ArrayList;
import rwmidi.MidiOutput;
import rwmidi.RWMidi;

public class SynthesizerCFOSynth extends Synthesizer {

    private final MidiOutput mMidiOut;

    private int mCurrentlyPlayingNote = -1;

    private int mChannel = 0;

    public static final int PRESET_SAVE = 0;

    public static final int PRESET_RECALL = 1;

    public static final int IS_12_BIT = 3;

    public static final int CUTOFF = 4;

    public static final int ZERO_HZ_FM = 5;

    public static final int FM_OCTAVES = 6;

    public static final int PORTAMENTO = 8;

    public static final int FILTER_TYPE = 9;

    public static final int LFO1 = 10;

    public static final int SEMITONE1 = 11;

    public static final int DETUNE1 = 12;

    public static final int GAIN1 = 13;

    public static final int WAVEFORM1 = 14;

    public static final int FM1 = 15;

    public static final int FM1_OCTAVES = 16;

    public static final int FM1_SOURCE = 17;

    public static final int FM1_SHAPE = 18;

    public static final int FREQUENCY1 = 19;

    public static final int LFO2 = 20;

    public static final int SEMITONE2 = 21;

    public static final int DETUNE2 = 22;

    public static final int GAIN2 = 23;

    public static final int WAVEFORM2 = 24;

    public static final int FM2 = 25;

    public static final int FM2_OCTAVES = 26;

    public static final int FM2_SOURCE = 27;

    public static final int FM2_SHAPE = 28;

    public static final int FREQUENCY2 = 29;

    public static final int LFO3 = 30;

    public static final int SEMITONE3 = 31;

    public static final int DETUNE3 = 32;

    public static final int GAIN3 = 33;

    public static final int WAVEFORM3 = 34;

    public static final int FM3 = 35;

    public static final int FM3_OCTAVES = 36;

    public static final int FM3_SOURCE = 37;

    public static final int FM3_SHAPE = 38;

    public static final int FREQUENCY3 = 39;

    public static final int CUTOFF_MOD_AMOUNT = 70;

    public static final int CUTOFF_SOURCE = 72;

    public static final int ENV1_VELOCITY = 112;

    public static final int ENV1_ENABLE = 113;

    public static final int ENV1_ATTACK = 114;

    public static final int ENV1_DECAY = 115;

    public static final int ENV1_SUSTAIN = 116;

    public static final int ENV1_RELEASE = 117;

    public static final int ENV2_VELOCITY = 122;

    public static final int ENV2_ENABLE = 123;

    public static final int ENV2_ATTACK = 124;

    public static final int ENV2_DECAY = 125;

    public static final int ENV2_SUSTAIN = 126;

    public static final int ENV2_RELEASE = 127;

    public SynthesizerCFOSynth(int pOutputID) {
        mMidiOut = RWMidi.getOutputDevices()[pOutputID].createOutput();
    }

    public ArrayList<Instrument> instruments() {
        return null;
    }

    MidiOutput midi() {
        return mMidiOut;
    }

    public void channel(int pChannel) {
        mChannel = pChannel;
    }

    public SynthesizerCFOSynth(String pOutputname) {
        mMidiOut = RWMidi.getOutputDevice(SynthesizerMidi.getProperDeviceName(pOutputname)).createOutput();
    }

    public void sendController(int pController, int pValue) {
        int ret = mMidiOut.sendController(mChannel, clamp127(pController), clamp127(pValue));
        if (ret == 0) {
            System.err.println("### ERROR in CFOSynth.sendController");
        }
    }

    public void sendNoteOn(int pNote, int pVelocity) {
        mCurrentlyPlayingNote = pNote;
        int ret = mMidiOut.sendNoteOn(mChannel, clamp127(pNote), clamp127(pVelocity));
        if (ret == 0) {
            System.err.println("### ERROR in CFOSynth.sendNoteOn");
        }
    }

    public void sendNoteOff() {
        if (mCurrentlyPlayingNote > -1) {
            sendNoteOff(mCurrentlyPlayingNote, 0);
        }
    }

    public void sendNoteOff(int pNote, int pVelocity) {
        mCurrentlyPlayingNote = -1;
        int ret = mMidiOut.sendNoteOff(mChannel, clamp127(pNote), clamp127(pVelocity));
        if (ret == 0) {
            System.err.println("### ERROR in CFOSynth.sendNoteOff");
        }
    }

    public int currently_playing_note() {
        return mCurrentlyPlayingNote;
    }

    public void noteOn(int pNote, int pVelocity, float pDuration) {
        sendNoteOn(pNote, pVelocity);
    }

    public void noteOn(int pNote, int pVelocity) {
        sendNoteOn(pNote, pVelocity);
    }

    public Instrument instrument(int pInstrumentID) {
        return null;
    }

    public Instrument instrument() {
        return null;
    }

    public void noteOff(int pNote) {
        sendNoteOff(pNote, 0);
    }

    public void noteOff() {
        sendNoteOff(mCurrentlyPlayingNote, 0);
    }

    public boolean isPlaying() {
        return mCurrentlyPlayingNote != -1;
    }
}
