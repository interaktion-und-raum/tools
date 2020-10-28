import de.hfkbremen.ton.*; 
import controlP5.*; 
import ddf.minim.*; 
import com.jsyn.unitgen.*; 


SoundSource mSoundSource;
void settings() {
    size(640, 480);
}
void setup() {
    size(640, 480);
    frameRate(60);
    noFill();
    rectMode(CENTER);
    ellipseMode(CENTER);
    smooth();
    /* start jsyn */
    SynthesizerJSyn mSynth = new SynthesizerJSyn(Synthesizer.INSTRUMENT_EMPTY);
    MixerMono mMixerMono = new MixerMono(1);
    mMixerMono.amplitude.set(0.85f);
    mMixerMono.output.connect(0, mSynth.line_out().input, 0);
    mMixerMono.output.connect(0, mSynth.line_out().input, 1);
    mSoundSource = new SoundSource(mSynth, mMixerMono, 0);
    mSoundSource.triggerposition().set(width / 2.0f, height / 2.0f, 0);
    mSoundSource.position().set(random(width), random(height), 0);
}
void draw() {
    /* compute */
    if (mousePressed) {
        if (mouseX > mSoundSource.position().x - 30
                && mouseX < mSoundSource.position().x + 30
                && mouseY > mSoundSource.position().y - 30
                && mouseY < mSoundSource.position().y + 30) {
            mSoundSource.position().set(mouseX, mouseY, 0);
        }
    }
    mSoundSource.update();
    /* draw */
    background(255);
    stroke(0, 32);
    line(mSoundSource.triggerposition().x, mSoundSource.triggerposition().y,
            mSoundSource.position().x, mSoundSource.position().y);
    stroke(255, 127, 0, 127);
    ellipse(mSoundSource.position().x, mSoundSource.position().y, 20, 20);
    stroke(0, 127);
    ellipse(mSoundSource.triggerposition().x, mSoundSource.triggerposition().y,
            mSoundSource.mMaxDistance * 2, mSoundSource.mMaxDistance * 2);
}
class SoundSource {
    final UnitOscillator mOsc;
    final PVector myPosition;
    final PVector mTriggerPosition;
    final float mMaxDistance = 100;
    float mFreqPointer = 0;
    float mAmpPointer = 0;
    SoundSource(SynthesizerJSyn pSynth, MixerMono pMixerMono, int pMixerChannel) {
        myPosition = new PVector();
        mTriggerPosition = new PVector();
        /* create oscillators */
        mOsc = new SawtoothOscillator();
        pSynth.add(mOsc);
        mOsc.start();
        mOsc.output.connect(pMixerMono.input.getConnectablePart(pMixerChannel));
        /* default values */
        mOsc.amplitude.set(0.0f);
        mOsc.frequency.set(200.0f);
    }
    PVector position() {
        return myPosition;
    }
    PVector triggerposition() {
        return mTriggerPosition;
    }
    void update() {
        float myDistanceRatio = (1 - min(1, myPosition.dist(mTriggerPosition) / mMaxDistance));
        mAmpPointer += 0.65f;
        float mAmp = noise(mAmpPointer) * noise(mAmpPointer * 1.3f);
        if (noise(mAmpPointer * 0.45f) > 0.5f) {
            mOsc.amplitude.set(myDistanceRatio * mAmp);
        } else {
            mOsc.amplitude.set(0);
        }
        /* get frequency from perlin noise */
        mFreqPointer += 0.03f;
        float mFreq = noise(mFreqPointer);
        mOsc.frequency.set(400 * mFreq + 75);
    }
}
