package de.hfkbremen.synthesizer;

import rwmidi.MidiOutput;
import rwmidi.RWMidi;

import java.util.ArrayList;
import java.util.Timer;

public class SynthesizerMidi extends Synthesizer {

    private final Timer mTimer;
    private final MidiOutput mMidiOut;
    private int mLastPlayedNote = -1;
    private int mChannel;

    public SynthesizerMidi(String pMidiOutputDeviceName) {
        mTimer = new Timer();
        mMidiOut = RWMidi.getOutputDevice(getProperDeviceName(pMidiOutputDeviceName)).createOutput();
        prepareExitHandler();
    }

    public ArrayList<Instrument> instruments() {
        return null;
    }

    static String getProperDeviceName(String pMidiOutputDeviceName) {
        String[] mDevices = RWMidi.getOutputDeviceNames();
        for (String mDevice : mDevices) {
            if (mDevice.startsWith(pMidiOutputDeviceName)) {
                return mDevice;
            }
        }
        System.err.println("### couldn t find midi device");
        return null;
    }

    public void noteOn(int pNote, int pVelocity, float pDuration) {
        //mTimer.schedule(new MidiTimerNoteOnTask(mMidiOut, mChannel, pNote, pVelocity), 0);
        mMidiOut.sendNoteOn(mChannel, pNote, pVelocity);
        mTimer.schedule(new MidiTimerNoteOffTask(mMidiOut, mChannel, pNote, pVelocity), (int) pDuration * 1000);
        mLastPlayedNote = pNote;
    }

    public void noteOn(int pNote, int pVelocity) {
        mMidiOut.sendNoteOn(mChannel, pNote, pVelocity);
        mLastPlayedNote = pNote;
    }

    public void noteOff(int pNote) {
        mMidiOut.sendNoteOff(mChannel, pNote, 0);
        mLastPlayedNote = -1;
    }

    public void noteOff() {
        mMidiOut.sendNoteOff(mChannel, mLastPlayedNote, 0);
        mLastPlayedNote = -1;
    }

    public void controller(int pCC, int pValue) {
        mMidiOut.sendController(mChannel, pCC, pValue);
    }

    public Instrument instrument(int pInstrumentID) {
        mChannel = pInstrumentID;
        return null;
    }

    public Instrument instrument() {
        return null;
    }

    public boolean isPlaying() {
        return (mLastPlayedNote != -1);
    }

    private void prepareExitHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 127; i++) {
                    mMidiOut.sendNoteOff(mChannel, i, 0);
                    mLastPlayedNote = -1;
                }
            }
        }));
    }
}
