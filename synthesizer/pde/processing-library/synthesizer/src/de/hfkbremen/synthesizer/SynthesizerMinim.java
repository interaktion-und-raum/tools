package de.hfkbremen.synthesizer;

import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import static de.hfkbremen.synthesizer.Instrument.*;
import static de.hfkbremen.synthesizer.SynthUtil.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SynthesizerMinim extends Synthesizer {

    private final Minim mMinim;

    private final AudioOutput mOut;

    private final ArrayList<Instrument> mInstruments;

    private int mInstrumentID;

    private static final boolean USE_AMP_FRACTION = false;

    private final Timer mTimer;

    private boolean mIsPlaying = false;

    public SynthesizerMinim() {
        mMinim = new Minim(this);
        mOut = mMinim.getLineOut(Minim.MONO, 2048);
        mTimer = new Timer();

        mInstruments = new ArrayList<Instrument>();
        for (int i = 0; i < NUMBERS_OF_INSTRUMENTS; i++) {
            mInstruments.add(new InstrumentMinim(mMinim, i));
            mInstruments.get(i).osc_type(i % NUMBER_OF_OSCILLATORS);
            ((InstrumentMinim) mInstruments.get(i)).amp(1.0f);
        }
    }

    public ArrayList<Instrument> instruments() {
        return mInstruments;
    }

    public void noteOn(int pNote, int pVelocity, float pDuration) {
        noteOn(pNote, pVelocity);
        TimerTask mTask = new NoteOffTask();
        mTimer.schedule(mTask, (long) (pDuration * 1000));
    }

    public void noteOn(int pNote, int pVelocity) {
        mIsPlaying = true;
        final float mFreq = note_to_frequency(clamp127(pNote));
        float mAmp = clamp127(pVelocity) / 127.0f;
        if (USE_AMP_FRACTION) {
            mAmp /= (float) NUMBERS_OF_INSTRUMENTS;
        }
        if (mInstruments.get(getInstrumentID()) instanceof InstrumentMinim) {
            InstrumentMinim mInstrument = (InstrumentMinim) mInstruments.get(getInstrumentID());
            mInstrument.noteOn(mFreq, mAmp);
        }
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    private int getInstrumentID() {
        return Math.max(mInstrumentID, 0) % mInstruments.size();
    }

    public void noteOff(int pNote) {
        noteOff();
    }

    public void noteOff() {
        if (mInstruments.get(getInstrumentID()) instanceof InstrumentMinim) {
            InstrumentMinim mInstrument = (InstrumentMinim) mInstruments.get(getInstrumentID());
            mInstrument.noteOff();
            mIsPlaying = false;
        }
    }

    public Instrument instrument(int pInstrumentID) {
        mInstrumentID = pInstrumentID;
        return instruments().get(mInstrumentID);
    }

    public Instrument instrument() {
        return instruments().get(mInstrumentID);
    }

    public class NoteOffTask extends TimerTask {

        public void run() {
            noteOff();
        }
    }
}
