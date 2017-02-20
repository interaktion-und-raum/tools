package de.hfkbremen.synthesizer;

import com.jsyn.data.SegmentedEnvelope;
import com.jsyn.engine.SynthesisEngine;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SquareOscillator;
import com.jsyn.unitgen.TriangleOscillator;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitOscillator;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.WhiteNoise;
import com.softsynth.shared.time.TimeStamp;
import controlP5.ControlElement;

public class InstrumentJSyn extends Instrument {

    private final SynthesisEngine mSynth;

    private final LineOut mLineOut;
    private final VariableRateMonoReader mEnvPlayer;
    private UnitGenerator mOsc;
    private SegmentedEnvelope mEnvData;
    private float mAmp = 0.9f;
    private boolean mDumpWarningLFO = true;
    private boolean mDumpWarningFILTER = true;
    private float mFreqOffset = 0.0f;
    private float mFreq = 0.0f;

    public InstrumentJSyn(SynthesisEngine pSynth, LineOut pLineOut, int pName) {
        super(pName);
        mSynth = pSynth;
        mLineOut = pLineOut;

        update_data();

        mEnvPlayer = new VariableRateMonoReader();
        mSynth.add(mEnvPlayer);
        mEnvPlayer.start();

        mOsc = new SineOscillator();
        connectOsc(mOsc);
    }

    private void connectOsc(UnitGenerator o) {
        mSynth.add(o);
        if (o instanceof UnitOscillator) {
            UnitOscillator uo = (UnitOscillator) o;
            uo.amplitude.set(0);
            uo.frequency.set(220);
            uo.output.connect(0, mLineOut.input, 0);
            uo.output.connect(0, mLineOut.input, 1);
            mEnvPlayer.output.connect(uo.amplitude);
        } else if (o instanceof WhiteNoise) {
            WhiteNoise uo = (WhiteNoise) o;
            uo.amplitude.set(0);
            uo.output.connect(0, mLineOut.input, 0);
            uo.output.connect(0, mLineOut.input, 1);
            mEnvPlayer.output.connect(uo.amplitude);
        }
    }

    private void disconnectOsc(UnitGenerator o) {
        o.stop();
        if (o instanceof UnitOscillator) {
            UnitOscillator uo = (UnitOscillator) o;
            uo.amplitude.set(0);
            uo.output.disconnect(mLineOut.input);
            uo.output.disconnectAll();
            mEnvPlayer.output.disconnect(uo.amplitude);
        } else if (o instanceof WhiteNoise) {
            WhiteNoise uo = (WhiteNoise) o;
            uo.amplitude.set(0);
            uo.output.disconnect(mLineOut.input);
            uo.output.disconnectAll();
            mEnvPlayer.output.disconnect(uo.amplitude);
        }
        mSynth.remove(o);
    }

    private void update_data() {
        double[] mData = {mAttack, 1.0 * mAmp, // get_attack
                          mDecay, // get_decay
                          mSustain * mAmp, // get_sustain
                          mRelease, 0.0, // get_release
        };
        mEnvData = new SegmentedEnvelope(mData);
    }

    public void noteOff() {
        mEnvPlayer.dataQueue.queueOff(mEnvData, true, new TimeStamp(mSynth.getCurrentTime()));
    }

    public void noteOn(float pFreq, float pAmp) {
        update_data();
        mEnvData.setSustainBegin(2);
        mEnvData.setSustainEnd(2);
        TimeStamp mTimeStamp = new TimeStamp(mSynth.getCurrentTime());
        mFreq = pFreq;
        update_freq();
//        if (mOsc instanceof UnitOscillator) {
//            UnitOscillator uo = (UnitOscillator) mOsc;
//            uo.frequency.set(frequency);
//        }
        mEnvPlayer.amplitude.set(pAmp, mTimeStamp);
        mEnvPlayer.dataQueue.queueOn(mEnvData, mTimeStamp);
    }

    public void update_freq() {
        if (mOsc instanceof UnitOscillator) {
            UnitOscillator uo = (UnitOscillator) mOsc;
            uo.frequency.set(mFreq + mFreqOffset);
        }
    }

    public void amp(float pAmp) {
        mAmp = pAmp;
        if (mOsc instanceof UnitOscillator) {
            UnitOscillator uo = (UnitOscillator) mOsc;
            uo.amplitude.set(pAmp);
        } else if (mOsc instanceof WhiteNoise) {
            WhiteNoise uo = (WhiteNoise) mOsc;
            uo.amplitude.set(pAmp);
        }
    }

    VariableRateMonoReader env() {
        return mEnvPlayer;
    }

    public void trigger() {
        mEnvPlayer.dataQueue.clear();
        update_data();
        mEnvPlayer.dataQueue.queue(mEnvData, 0, mEnvData.getNumFrames());
    }

    @ControlElement(properties = {"min=0.0", "max=10.0", "type=knob", "radius=20", "resolution=1000"}, x = 0, y = 0)
    public void attack(float pAttack) {
        super.attack(pAttack);
    }

    @ControlElement(properties = {"min=0.0", "max=10.0", "type=knob", "radius=20", "resolution=1000"}, x = 50, y = 0)
    public void decay(float pDecay) {
        super.decay(pDecay);
    }

    @ControlElement(properties = {"min=0.0", "max=1.0", "type=knob", "radius=20", "resolution=100"}, x = 100, y = 0)
    public void sustain(float pSustain) {
        super.sustain(pSustain);
    }

    @ControlElement(properties = {"min=0.0", "max=10.0", "type=knob", "radius=20", "resolution=1000"}, x = 150, y = 0)
    public void release(float pRelease) {
        super.release(pRelease);
    }

    @ControlElement(properties = {"min=0.0",
                                  "max=" + (NUMBER_OF_OSCILLATORS - 1),
                                  "type=knob",
                                  "radius=20",
                                  "resolution=" + (NUMBER_OF_OSCILLATORS - 1)}, x = 200, y = 0)
    public void osc_type(int pOsc) {
        disconnectOsc(mOsc);
        /*
         SINE,
         TRIANGLE,
         SAWTOOTH,
         SQUARE,
         NOISE
         */
        switch ((int) pOsc) {
            case SINE:
                mOsc = new SineOscillator();
                break;
            case TRIANGLE:
                mOsc = new TriangleOscillator();
                break;
            case SAWTOOTH:
                mOsc = new SawtoothOscillator();
                break;
            case SQUARE:
                mOsc = new SquareOscillator();
                break;
            case NOISE:
                mOsc = new WhiteNoise();
                break;
        }
        connectOsc(mOsc);
    }

    public int get_osc_type() {
        int mOscID = -1;
        if (mOsc instanceof SineOscillator) {
            mOscID = SINE;
        } else if (mOsc instanceof TriangleOscillator) {
            mOscID = TRIANGLE;
        } else if (mOsc instanceof SawtoothOscillator) {
            mOscID = SAWTOOTH;
        } else if (mOsc instanceof SquareOscillator) {
            mOscID = SQUARE;
        } else if (mOsc instanceof WhiteNoise) {
            mOscID = NOISE;
        }
        return mOscID;
    }

    @Override
    public void lfo_amp(float pLFOAmp) {
        if (mDumpWarningLFO) {
            System.out.println("### LFO not implemented.");
            mDumpWarningLFO = false;
        }
    }

    @Override
    public float get_lfo_amp() {
        return 0;
    }

    @Override
    public void lfo_freq(float pLFOFreq) {
        if (mDumpWarningLFO) {
            System.out.println("### LFO not implemented.");
            mDumpWarningLFO = false;
        }
    }

    @Override
    public float get_lfo_freq() {
        return 0;
    }

    @Override
    public void filter_q(float f) {
        if (mDumpWarningFILTER) {
            System.out.println("### FILTER not implemented.");
            mDumpWarningFILTER = false;
        }
    }

    @Override
    public float get_filter_q() {
        return 0;
    }

    @Override
    public void filter_freq(float f) {
        if (mDumpWarningFILTER) {
            System.out.println("### FILTER not implemented.");
            mDumpWarningFILTER = false;
        }
    }

    @Override
    public float get_filter_freq() {
        return 0;
    }

    public void pitch_bend(float freq_offset) {
        mFreqOffset = freq_offset;
        update_freq();
    }

    public void set_freq(float freq) {
        mFreq = freq;
        update_freq();
    }
}
