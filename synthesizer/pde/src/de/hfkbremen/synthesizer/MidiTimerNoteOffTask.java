package de.hfkbremen.synthesizer;

import java.util.TimerTask;
import rwmidi.MidiOutput;

public class MidiTimerNoteOffTask extends TimerTask {

    private final MidiOutput mMidiOutput;
    private final int mChannel;
    private final int mNote;
    private final int mVelocity;

    public MidiTimerNoteOffTask(MidiOutput pMidiOutput, int pChannel, int pNote, int pVelocity) {
        mMidiOutput = pMidiOutput;
        mChannel = pChannel;
        mNote = pNote;
        mVelocity = pVelocity;
    }

    @Override
    public void run() {
        mMidiOutput.sendNoteOff(mChannel, mNote, mVelocity);
    }
}
