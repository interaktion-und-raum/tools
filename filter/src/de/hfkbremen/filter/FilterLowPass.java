package de.hfkbremen.filter;

public class FilterLowPass implements Filter {

    private float mAverage = 0.0f;

    private final float mSmoothing;

    public FilterLowPass(float pSmoothing) {
        mSmoothing = pSmoothing;
    }

    public void add_sample(float pSample) {
        mAverage += (pSample - mAverage) * mSmoothing;
    }

    public float get() {
        return mAverage;
    }
}
