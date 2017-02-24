package de.hfkbremen.synthesizer;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SynthesizerOSC extends Synthesizer {

    private final OscP5 mOscP5;
    private final NetAddress mRemoteLocation;
    private final Timer mTimer;
    private int mChannel;
    private boolean mIsPlaying = false;

    public SynthesizerOSC(String pHostIP) {
        mOscP5 = new OscP5(this, 12000);
        mRemoteLocation = new NetAddress(pHostIP, 7400);
        mTimer = new Timer();
    }

    public SynthesizerOSC() {
        this("127.0.0.1");
    }

    public void noteOn(int pNote, int pVelocity, float pDuration) {
        noteOn(pNote, pVelocity);
        TimerTask mTask = new NoteOffTask();
        mTimer.schedule(mTask, (long) (pDuration * 1000));
    }

    public void noteOn(int pNote, int pVelocity) {
        mIsPlaying = true;
        OscMessage m = new OscMessage("/note" + mChannel + "/on");
        m.add(pNote);
        mOscP5.send(m, mRemoteLocation);
    }

    public void noteOff(int pNote) {
        mIsPlaying = false;
        OscMessage m = new OscMessage("/note" + mChannel + "/off");
        m.add(pNote);
        mOscP5.send(m, mRemoteLocation);
    }

    public void noteOff() {
        noteOff(-1);
    }

    public void control_change(int pCC, int pValue) {
    }

    public void pitch_bend(int pValue) {
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public Instrument instrument(int pInstrumentID) {
        mChannel = pInstrumentID;
        return null;
    }

    public Instrument instrument() {
        return null;
    }

    public ArrayList<? extends Instrument> instruments() {
        return null;
    }

    public class NoteOffTask extends TimerTask {

        public void run() {
            noteOff();
        }
    }
}
