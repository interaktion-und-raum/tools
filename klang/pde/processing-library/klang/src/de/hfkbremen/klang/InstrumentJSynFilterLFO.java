package de.hfkbremen.klang;

import com.jsyn.engine.SynthesisEngine;
import com.jsyn.unitgen.Add;
import com.jsyn.unitgen.FilterLowPass;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.UnitGenerator;
import com.jsyn.unitgen.UnitOscillator;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.WhiteNoise;
import controlP5.ControlElement;

public class InstrumentJSynFilterLFO extends InstrumentJSynBasic {

//    private final SynthesisEngine mSynth;
//    private final LineOut mLineOut;
//    private final VariableRateMonoReader mEnvPlayer;
//    private UnitGenerator mOsc;
//    private SegmentedEnvelope mEnvData;
//    private float mAmp = 0.9f;
//    private float mFreq = 0.0f;
//    private float mFreqOffset = 0.0f;

    private FilterLowPass mLowPassFilter;
    private SineOscillator mLFO;
    private Add mAddUnit;

    public InstrumentJSynFilterLFO(SynthesisEngine pSynth, LineOut pLineOut, int pName) {
        super(pSynth, pLineOut, pName);
    }

    protected void setupModules() {
        if (mLowPassFilter == null) {
            mLowPassFilter = new FilterLowPass();
            mLowPassFilter.output.connect(0, mLineOut.input, 0);
            mLowPassFilter.output.connect(0, mLineOut.input, 1);
            mLowPassFilter.frequency.set(2000);
            mLowPassFilter.Q.set(1);
            mSynth.add(mLowPassFilter);
        }
        if (mLFO == null) {
            mLFO = new SineOscillator();
            mLFO.amplitude.set(3);
            mLFO.frequency.set(10.0f);
            mSynth.add(mLFO);
        }
        if (mAddUnit == null) {
            mAddUnit = new Add();
            mAddUnit.inputA.set(220);
            mLFO.output.connect(mAddUnit.inputB);
            mSynth.add(mAddUnit);
        }
    }

    protected void connectOsc(UnitGenerator o) {
        super.setupModules();
        setupModules();
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

    protected void disconnectOsc(UnitGenerator o) {
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

    public void update_freq() {
        mAddUnit.inputA.set(mFreq + mFreqOffset);
    }

    @ControlElement(properties = {"min=0.0", "max=100.0", "type=knob", "radius=20", "resolution=1000"}, x = 250, y = 0)
    public void lfo_amp(float pLFOAmp) {
        mLFO.amplitude.set(pLFOAmp);
    }
//
//    @ControlElement(properties = {"min=0.0", "max=10.0", "type=knob", "radius=20", "resolution=1000"}, x = 0, y = 0)
//    public void attack(float pAttack) {
//        super.attack(pAttack);
//    }
//
//    @ControlElement(properties = {"min=0.0", "max=10.0", "type=knob", "radius=20", "resolution=1000"}, x = 50, y = 0)
//    public void decay(float pDecay) {
//        super.decay(pDecay);
//    }
//
//    @ControlElement(properties = {"min=0.0", "max=1.0", "type=knob", "radius=20", "resolution=100"}, x = 100, y = 0)
//    public void sustain(float pSustain) {
//        super.sustain(pSustain);
//    }
//
//    @ControlElement(properties = {"min=0.0", "max=10.0", "type=knob", "radius=20", "resolution=1000"}, x = 150, y = 0)
//    public void release(float pRelease) {
//        super.release(pRelease);
//    }
//
//    @ControlElement(properties = {"min=0.0",
//                                  "max=" + (NUMBER_OF_OSCILLATORS - 1),
//                                  "type=knob",
//                                  "radius=20",
//                                  "resolution=" + (NUMBER_OF_OSCILLATORS - 1)}, x = 200, y = 0)
//    public void osc_type(int pOsc) {
//        disconnectOsc(mOsc);
//        /*
//         SINE,
//         TRIANGLE,
//         SAWTOOTH,
//         SQUARE,
//         NOISE
//         */
//        switch (pOsc) {
//            case SINE:
//                mOsc = new SineOscillator();
//                break;
//            case TRIANGLE:
//                mOsc = new TriangleOscillator();
//                break;
//            case SAWTOOTH:
//                mOsc = new SawtoothOscillator();
//                break;
//            case SQUARE:
//                mOsc = new SquareOscillator();
//                break;
//            case NOISE:
//                mOsc = new WhiteNoise();
//                break;
//        }
//        connectOsc(mOsc);
//    }
//
//    public int get_osc_type() {
//        int mOscID = -1;
//        if (mOsc instanceof SineOscillator) {
//            mOscID = SINE;
//        } else if (mOsc instanceof TriangleOscillator) {
//            mOscID = TRIANGLE;
//        } else if (mOsc instanceof SawtoothOscillator) {
//            mOscID = SAWTOOTH;
//        } else if (mOsc instanceof SquareOscillator) {
//            mOscID = SQUARE;
//        } else if (mOsc instanceof WhiteNoise) {
//            mOscID = NOISE;
//        }
//        return mOscID;
//    }

    public float get_lfo_amp() {
        return (float) mLFO.amplitude.get();
    }

    @ControlElement(properties = {"min=0.0", "max=100.0", "type=knob", "radius=20", "resolution=1000"}, x = 300, y = 0)
    public void lfo_freq(float pLFOFreq) {
        mLFO.frequency.set(pLFOFreq);
    }

    public float get_lfo_freq() {
        return (float) mLFO.frequency.get();
    }

    @ControlElement(properties = {"min=0.0", "max=5", "type=knob", "radius=20", "resolution=100"}, x = 350, y = 0)
    public void filter_q(float pQ) {
        mLowPassFilter.Q.set(pQ);
    }

    public float get_filter_q() {
        return (float) mLowPassFilter.Q.get();
    }

    @ControlElement(properties = {"min=0.0", "max=30000", "type=knob", "radius=20", "resolution=300"}, x = 400, y = 0)
    public void filter_freq(float pFreq) {
        mLowPassFilter.frequency.set(pFreq);
    }

    public float get_filter_freq() {
        return (float) mLowPassFilter.frequency.get();
    }

    VariableRateMonoReader env() {
        return mEnvPlayer;
    }
}
