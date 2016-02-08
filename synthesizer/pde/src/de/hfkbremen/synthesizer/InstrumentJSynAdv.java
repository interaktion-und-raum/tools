package de.hfkbremen.synthesizer;

import com.jsyn.data.SegmentedEnvelope;
import com.jsyn.engine.SynthesisEngine;
import com.jsyn.unitgen.Add;
import com.jsyn.unitgen.FilterLowPass;
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

public class InstrumentJSynAdv extends Instrument {

    private final SynthesisEngine mSynth;

    private final LineOut mLineOut;

    private final FilterLowPass mLowPassFilter;

    private UnitGenerator mOsc;

    private final SineOscillator mLFO;

    private final Add mAddUnit;

    private SegmentedEnvelope mEnvData;

    private final VariableRateMonoReader mEnvPlayer;

    private float mAmp = 0.9f;

    public InstrumentJSynAdv(SynthesisEngine pSynth, LineOut pLineOut, int pName) {
        super(pName);
        mSynth = pSynth;
        mLineOut = pLineOut;

        mLowPassFilter = new FilterLowPass();
        mLowPassFilter.output.connect(0, mLineOut.input, 0);
        mLowPassFilter.output.connect(0, mLineOut.input, 1);
        mLowPassFilter.frequency.set(2000);
        mLowPassFilter.Q.set(1);

        update_data();

        mEnvPlayer = new VariableRateMonoReader();

        mOsc = new SineOscillator();

        mLFO = new SineOscillator();
        mLFO.amplitude.set(3);
        mLFO.frequency.set(10.0f);

        mAddUnit = new Add();
        mAddUnit.inputA.set(220);
        mLFO.output.connect(mAddUnit.inputB);

        mSynth.add(mLowPassFilter);
        mSynth.add(mEnvPlayer);
        mSynth.add(mLFO);
        mSynth.add(mAddUnit);
        connectOsc(mOsc);
        mEnvPlayer.start();
    }

    private void connectOsc(UnitGenerator o) {
        mSynth.add(o);
        if (o instanceof UnitOscillator) {
            UnitOscillator uo = (UnitOscillator) o;
            uo.amplitude.set(0);
            uo.output.connect(mLowPassFilter.input);
//            uo.output.connect(0, mLineOut.input, 0);
//            uo.output.connect(0, mLineOut.input, 1);
            mEnvPlayer.output.connect(uo.amplitude);
            mAddUnit.output.connect(uo.frequency);
        } else if (o instanceof WhiteNoise) {
            WhiteNoise uo = (WhiteNoise) o;
            uo.amplitude.set(0);
            uo.output.connect(mLowPassFilter.input);
//            uo.output.connect(0, mLineOut.input, 0);
//            uo.output.connect(0, mLineOut.input, 1);
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
        double[] mData = {
            mAttack, 1.0 * mAmp, // get_attack
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
        freq(pFreq);
        mEnvPlayer.amplitude.set(pAmp, mTimeStamp);
        mEnvPlayer.dataQueue.queueOn(mEnvData, mTimeStamp);
    }

    public void trigger() {
        mEnvPlayer.dataQueue.clear();
        update_data();
        mEnvPlayer.dataQueue.queue(mEnvData, 0, mEnvData.getNumFrames());
    }

    public void freq(float pFreq) {
        mAddUnit.inputA.set(pFreq);
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

    public float get_lfo_amp() {
        return (float) mLFO.amplitude.get();
    }

    public float get_lfo_freq() {
        return (float) mLFO.frequency.get();
    }

    public float get_filter_q() {
        return (float) mLowPassFilter.Q.get();
    }

    public float get_filter_freq() {
        return (float) mLowPassFilter.frequency.get();
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

    @ControlElement(properties = {"min=0.0", "max=" + (NUMBER_OF_OSCILLATORS - 1), "type=knob", "radius=20", "resolution=" + (NUMBER_OF_OSCILLATORS - 1)}, x = 200, y = 0)
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

    @ControlElement(properties = {"min=0.0", "max=100.0", "type=knob", "radius=20", "resolution=1000"}, x = 250, y = 0)
    public void lfo_amp(float pLFOAmp) {
        mLFO.amplitude.set(pLFOAmp);
    }

    @ControlElement(properties = {"min=0.0", "max=100.0", "type=knob", "radius=20", "resolution=1000"}, x = 300, y = 0)
    public void lfo_freq(float pLFOFreq) {
        mLFO.frequency.set(pLFOFreq);
    }

    @ControlElement(properties = {"min=0.0", "max=5", "type=knob", "radius=20", "resolution=100"}, x = 350, y = 0)
    public void filter_q(float pQ) {
        mLowPassFilter.Q.set(pQ);
    }

    @ControlElement(properties = {"min=0.0", "max=30000", "type=knob", "radius=20", "resolution=300"}, x = 400, y = 0)
    public void filter_freq(float pFreq) {
        mLowPassFilter.frequency.set(pFreq);
    }
}
