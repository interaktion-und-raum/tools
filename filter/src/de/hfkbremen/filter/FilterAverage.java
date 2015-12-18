package de.hfkbremen.filter;

public class FilterAverage implements Filter {

    private final float[] mSampler;

    private float mAverage = 0.0f;

    private float mAverageSum = 0.0f;

    private int mPtr = 0;

    public FilterAverage(int pSamplerSize) {
        mSampler = new float[pSamplerSize];
    }

    public void add_sample(float pSample) {
        mAverageSum -= mSampler[mPtr]; // sub old sample
        mSampler[mPtr] = pSample; // store new sample
        mAverageSum += pSample;
        mAverage = mAverageSum / mSampler.length;
        mPtr++; // advance pointer
        mPtr %= mSampler.length; // wrap pointer
    }

    public float get() {
        return mAverage;
    }
}
