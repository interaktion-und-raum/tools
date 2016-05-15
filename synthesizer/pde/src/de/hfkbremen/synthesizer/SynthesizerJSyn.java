package de.hfkbremen.synthesizer;

import com.jsyn.devices.javasound.JavaSoundAudioDevice;
import com.jsyn.engine.SynthesisEngine;
import com.jsyn.unitgen.LineOut;
import com.softsynth.shared.time.TimeStamp;
import static de.hfkbremen.synthesizer.Instrument.*;
import static de.hfkbremen.synthesizer.SynthUtil.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SynthesizerJSyn extends Synthesizer {

    private final SynthesisEngine mSynth;

    private final ArrayList<Instrument> mInstruments;

    private final LineOut mLineOut;

    private int mInstrumentID;

    private static final boolean USE_AMP_FRACTION = false;

    private final Timer mTimer;

    private boolean mIsPlaying = false;

    public SynthesizerJSyn() {
        this(false);
    }

    public SynthesizerJSyn(boolean pAdvancedInstruments) {
        final JavaSoundAudioDevice mDevice = new JavaSoundAudioDevice();

//        SynthUtil.dumpAudioDeviceInfo(mDevice);
        mSynth = new SynthesisEngine();
        prepareExitHandler();

        mLineOut = new LineOut();
        mSynth.add(mLineOut);
        mLineOut.start();

        mInstruments = new ArrayList<Instrument>();
        for (int i = 0; i < NUMBERS_OF_INSTRUMENTS; i++) {
            if (pAdvancedInstruments) {
                InstrumentJSynAdv mInstrumentJSyn = new InstrumentJSynAdv(mSynth, mLineOut, i);
                mInstruments.add(mInstrumentJSyn);
                mInstrumentJSyn.osc_type(i % NUMBER_OF_OSCILLATORS);
                mInstrumentJSyn.amp(1.0f);
            } else {
                InstrumentJSyn mInstrumentJSyn = new InstrumentJSyn(mSynth, mLineOut, i);
                mInstruments.add(mInstrumentJSyn);
                mInstrumentJSyn.osc_type(i % NUMBER_OF_OSCILLATORS);
                mInstrumentJSyn.amp(1.0f);
            }
        }
        mInstrumentID = 0;

        mSynth.start(44100,
                     mDevice.getDefaultInputDeviceID(), 2,
                     mDevice.getDefaultOutputDeviceID(), 2);

        mTimer = new Timer();
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public SynthesisEngine synth() {
        return mSynth;
    }

    public ArrayList<Instrument> instruments() {
        return mInstruments;
    }

    private void prepareExitHandler() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(
                        new Runnable() {
                    public void run() {
                        System.out.println("### shutting down JSyn");
                        mSynth.stop();
                    }
                }
                )
        );
    }

    public void noteOn(int pNote, int pVelocity, float pDuration) {
        TimerTask mTask = new NoteOffTask();
        mTimer.schedule(mTask, (long) (pDuration * 1000));
        mIsPlaying = true;
        final float mFreq = note_to_frequency(clamp127(pNote));
        float mAmp = clamp127(pVelocity) / 127.0f;
        if (USE_AMP_FRACTION) {
            mAmp /= (float) NUMBERS_OF_INSTRUMENTS;
        }
        TimeStamp mOnTime = new TimeStamp(mSynth.getCurrentTime());
        TimeStamp mOffTime = mOnTime.makeRelative(pDuration);
        if (mInstruments.get(getInstrumentID()) instanceof InstrumentJSyn) {
            InstrumentJSyn mInstrument = (InstrumentJSyn) mInstruments.get(getInstrumentID());
            mInstrument.amp(mAmp);
            mInstrument.freq(mFreq);
            mInstrument.env().start(mOnTime);
            mInstrument.env().stop(mOffTime);
            mInstrument.trigger();
        }
        if (mInstruments.get(getInstrumentID()) instanceof InstrumentJSynAdv) {
            InstrumentJSynAdv mInstrument = (InstrumentJSynAdv) mInstruments.get(getInstrumentID());
            mInstrument.amp(mAmp);
            mInstrument.freq(mFreq);
            mInstrument.env().start(mOnTime);
            mInstrument.env().stop(mOffTime);
            mInstrument.trigger();
        }
    }

    public void noteOn(int pNote, int pVelocity) {
        mIsPlaying = true;
        final float mFreq = note_to_frequency(clamp127(pNote));
        float mAmp = clamp127(pVelocity) / 127.0f;
        if (USE_AMP_FRACTION) {
            mAmp /= (float) NUMBERS_OF_INSTRUMENTS;
        }
        if (mInstruments.get(getInstrumentID()) instanceof InstrumentJSyn) {
            InstrumentJSyn mInstrument = (InstrumentJSyn) mInstruments.get(getInstrumentID());
            mInstrument.noteOn(mFreq, mAmp);
        }
        if (mInstruments.get(getInstrumentID()) instanceof InstrumentJSynAdv) {
            InstrumentJSynAdv mInstrument = (InstrumentJSynAdv) mInstruments.get(getInstrumentID());
            mInstrument.noteOn(mFreq, mAmp);
        }
    }

    private int getInstrumentID() {
        return Math.max(mInstrumentID, 0) % mInstruments.size();
    }

    public void noteOff(int pNote) {
        noteOff();
    }

    public void noteOff() {
        mIsPlaying = false;
        if (mInstruments.get(getInstrumentID()) instanceof InstrumentJSyn) {
            InstrumentJSyn mInstrument = (InstrumentJSyn) mInstruments.get(getInstrumentID());
            mInstrument.noteOff();
        }
        if (mInstruments.get(getInstrumentID()) instanceof InstrumentJSynAdv) {
            InstrumentJSynAdv mInstrument = (InstrumentJSynAdv) mInstruments.get(getInstrumentID());
            mInstrument.noteOff();
        }
    }

    public void controller(int pCC, int pValue) {
    }

    public final Instrument instrument(int pInstrumentID) {
        mInstrumentID = pInstrumentID;
        return instruments().get(mInstrumentID);
    }

    public Instrument instrument() {
        return instruments().get(mInstrumentID);
    }

    public class NoteOffTask extends TimerTask {

        public void run() {
            mIsPlaying = false;
        }
    }
}
