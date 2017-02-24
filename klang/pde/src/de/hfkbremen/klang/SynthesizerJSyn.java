package de.hfkbremen.klang;

import com.jsyn.devices.javasound.JavaSoundAudioDevice;
import com.jsyn.engine.SynthesisEngine;
import com.jsyn.unitgen.LineOut;
import com.softsynth.shared.time.TimeStamp;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static de.hfkbremen.klang.Instrument.NUMBER_OF_OSCILLATORS;
import static de.hfkbremen.klang.SynthUtil.clamp127;
import static de.hfkbremen.klang.SynthUtil.note_to_frequency;
import static processing.core.PApplet.constrain;

public class SynthesizerJSyn extends Synthesizer {

    private static final boolean USE_AMP_FRACTION = false;
    private final SynthesisEngine mSynth;
    private final ArrayList<InstrumentJSyn> mInstruments;
    private final Timer mTimer;
    private int mInstrumentID;
    private boolean mIsPlaying = false;

    public SynthesizerJSyn() {
        this(INSTRUMENT_BASIC);
    }

    public SynthesizerJSyn(int pDefaultInstrumentType) {
        mSynth = new SynthesisEngine();
        prepareExitHandler();

        LineOut mLineOut = new LineOut();
        mSynth.add(mLineOut);
        mLineOut.start();

        mInstruments = new ArrayList<>();
        for (int i = 0; i < NUMBERS_OF_INSTRUMENTS; i++) {
            final InstrumentJSyn mInstrumentJSyn;
            switch (pDefaultInstrumentType) {
                case INSTRUMENT_WITH_FILTER_AND_LFO:
                    mInstrumentJSyn = new InstrumentJSynFilterLFO(mSynth, mLineOut, i);
                    break;
                case INSTRUMENT_SIMPLE:
                    mInstrumentJSyn = new InstrumentJSyn(mSynth, mLineOut, i);
                    break;
                default:
                    mInstrumentJSyn = new InstrumentJSynBasic(mSynth, mLineOut, i);
            }
            mInstrumentJSyn.osc_type(i % NUMBER_OF_OSCILLATORS);
            mInstrumentJSyn.amp(1.0f);
            mInstruments.add(mInstrumentJSyn);
        }
        mInstrumentID = 0;

//        SynthUtil.dumpAudioDeviceInfo(mDevice);
        final JavaSoundAudioDevice mDevice = new JavaSoundAudioDevice();
        mSynth.start(44100, mDevice.getDefaultInputDeviceID(), 2, mDevice.getDefaultOutputDeviceID(), 2);

        mTimer = new Timer();
    }

    public SynthesisEngine synth() {
        return mSynth;
    }

    private void prepareExitHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("### shutting down JSyn");
                mSynth.stop();
            }
        }));
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
        InstrumentJSyn mInstrument = getInstrument(getInstrumentID());
        mInstrument.amp(mAmp);
        mInstrument.set_freq(mFreq);
        if (mInstrument instanceof InstrumentJSynBasic) {
            InstrumentJSynBasic mInstrumentJSynBasic = (InstrumentJSynBasic) mInstrument;
            mInstrumentJSynBasic.env().start(mOnTime);
            mInstrumentJSynBasic.env().stop(mOffTime);
            mInstrumentJSynBasic.trigger();
        }
    }

    public void noteOn(int pNote, int pVelocity) {
        mIsPlaying = true;
        final float mFreq = note_to_frequency(clamp127(pNote));
        float mAmp = clamp127(pVelocity) / 127.0f;
        if (USE_AMP_FRACTION) {
            mAmp /= (float) NUMBERS_OF_INSTRUMENTS;
        }
        getInstrument(getInstrumentID()).noteOn(mFreq, mAmp);
    }

    public void noteOff(int pNote) {
        noteOff();
    }

    public void noteOff() {
        mIsPlaying = false;
        getInstrument(getInstrumentID()).noteOff();
//        if (mInstruments.get(getInstrumentID()) instanceof InstrumentJSyn) {
//        }
//        if (mInstruments.get(getInstrumentID()) instanceof InstrumentJSynFilterLFO) {
//            InstrumentJSynFilterLFO mInstrument = (InstrumentJSynFilterLFO) mInstruments.get(getInstrumentID());
//            mInstrument.noteOff();
//        }
    }

    public void control_change(int pCC, int pValue) {
        // not used in jsyn klang
    }

    public void pitch_bend(int pValue) {
        final float mRange = 110;
        final float mValue = mRange * ((float) (constrain(pValue, 0, 16383) - 8192) / 8192.0f);
        mInstruments.get(getInstrumentID()).pitch_bend(mValue);
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public final Instrument instrument(int pInstrumentID) {
        mInstrumentID = pInstrumentID;
        return instruments().get(mInstrumentID);
    }

    public Instrument instrument() {
        return mInstruments.get(mInstrumentID);
    }

    public ArrayList<? extends Instrument> instruments() {
        return mInstruments;
    }

    public InstrumentJSyn getInstrument(int pInstrumentID) {
        return mInstruments.get(mInstrumentID);
    }

    public InstrumentJSynBasic getInstrumentBasic(int pInstrumentID) {
        if (mInstruments.get(mInstrumentID) instanceof InstrumentJSynBasic) {
            return (InstrumentJSynBasic) mInstruments.get(mInstrumentID);
        } else {
            return null;
        }
    }

    public InstrumentJSynFilterLFO getInstrumentFilterLFO(int pInstrumentID) {
        if (mInstruments.get(mInstrumentID) instanceof InstrumentJSynFilterLFO) {
            return (InstrumentJSynFilterLFO) mInstruments.get(mInstrumentID);
        } else {
            return null;
        }
    }

    private int getInstrumentID() {
        return Math.max(mInstrumentID, 0) % mInstruments.size();
    }

    public class NoteOffTask extends TimerTask {

        public void run() {
            mIsPlaying = false;
        }
    }
}
