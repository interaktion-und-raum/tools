package de.hfkbremen.filter;

import processing.core.PApplet;

public class FilterButterworth implements Filter {

    /* http://baumdevblog.blogspot.de/2010/11/butterworth-lowpass-filter-coefficients.html */
    private float mAverage = 0.0f;

    private final float[] xv = new float[3];

    private final float[] yv = new float[3];

    private final float mCutOff; // 5000

    private final int mSampleRate; // 441000

    public FilterButterworth(int pSampleRate, float pCutOff) {
        mSampleRate = pSampleRate;
        mCutOff = pCutOff;
    }

    public void add_sample(float pSample) {
        float[] ax = new float[3];
        float[] by = new float[3];

        getLPCoefficientsButterworth2Pole(mSampleRate, mCutOff, ax, by);

        xv[2] = xv[1];
        xv[1] = xv[0];
        xv[0] = pSample;
        yv[2] = yv[1];
        yv[1] = yv[0];

        yv[0] = (ax[0] * xv[0] + ax[1] * xv[1] + ax[2] * xv[2]
                 - by[1] * yv[0]
                 - by[2] * yv[1]);

        mAverage = yv[0];
    }

    public float get() {
        return mAverage;
    }

    private void getLPCoefficientsButterworth2Pole(int samplerate, float cutoff, float[] ax, float[] by) {
        float PI = 3.1415926535897932385f;
        float sqrt2 = 1.4142135623730950488f;

        float QcRaw = (2 * PI * cutoff) / samplerate; // Find cutoff frequency in [0..PI]
        float QcWarp = PApplet.tan(QcRaw); // Warp cutoff frequency

        float gain = 1 / (1 + sqrt2 / QcWarp + 2 / (QcWarp * QcWarp));
        by[2] = (1 - sqrt2 / QcWarp + 2 / (QcWarp * QcWarp)) * gain;
        by[1] = (2 - 2 * 2 / (QcWarp * QcWarp)) * gain;
        by[0] = 1;
        ax[0] = 1 * gain;
        ax[1] = 2 * gain;
        ax[2] = 1 * gain;
    }

    void filter(float[] samples) {
        float[] ax = new float[3];
        float[] by = new float[3];

        getLPCoefficientsButterworth2Pole(44100, 5000, ax, by);

        for (int i = 0; i < samples.length; i++) {
            xv[2] = xv[1];
            xv[1] = xv[0];
            xv[0] = samples[i];
            yv[2] = yv[1];
            yv[1] = yv[0];

            yv[0] = (ax[0] * xv[0] + ax[1] * xv[1] + ax[2] * xv[2]
                     - by[1] * yv[0]
                     - by[2] * yv[1]);

            samples[i] = yv[0];
        }
    }
}
