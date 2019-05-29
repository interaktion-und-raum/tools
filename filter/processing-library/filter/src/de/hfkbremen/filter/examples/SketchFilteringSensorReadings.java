package de.hfkbremen.filter.examples;

import de.hfkbremen.filter.Filter;
import de.hfkbremen.filter.FilterAverage;
import de.hfkbremen.filter.FilterButterworth;
import de.hfkbremen.filter.FilterLowPass;
import processing.core.PApplet;

public class SketchFilteringSensorReadings extends PApplet {

    private Filter mFilterAverage;

    private Filter mFilterLowPassFilter;

    private Filter mFilterButterworthFilter;

    private static final int WIDTH = 640;

    private final float[] mRawDataBuffer = new float[WIDTH];

    private final float[] mSamplerAverageBuffer = new float[WIDTH];

    private final float[] mSamplerLowPassFilterBuffer = new float[WIDTH];

    private final float[] mSamplerButterworthBuffer = new float[WIDTH];

    private int mViewPointer;

    public void settings() {
        size(WIDTH, 480);
    }

    public void setup() {
        mFilterAverage = new FilterAverage(50);
        mFilterLowPassFilter = new FilterLowPass(0.035f);
        mFilterButterworthFilter = new FilterButterworth(21000, 750);
//        mFilterButterworthFilter = new FilterMedian(50);
    }

    public void draw() {
        background(255);

        mViewPointer++;
        mViewPointer %= mRawDataBuffer.length;

        float mRawData = sensor_data();
        mRawDataBuffer[mViewPointer] = mRawData;

        mFilterAverage.add_sample(mRawData);
        mSamplerAverageBuffer[mViewPointer] = mFilterAverage.get();

        mFilterLowPassFilter.add_sample(mRawData);
        mSamplerLowPassFilterBuffer[mViewPointer] = mFilterLowPassFilter.get();

        mFilterButterworthFilter.add_sample(mRawData);
        mSamplerButterworthBuffer[mViewPointer] = mFilterButterworthFilter.get();

        /* draw raw data - grey */
        stroke(0, 32);
        for (int i = 1; i < mRawDataBuffer.length; i++) {
            line(i - 1, mRawDataBuffer[i - 1], i, mRawDataBuffer[i]);
        }

        /* draw averaged data - orange*/
        stroke(255, 127, 0, 127);
        for (int i = 1; i < mSamplerAverageBuffer.length; i++) {
            line(i - 1, mSamplerAverageBuffer[i - 1], i, mSamplerAverageBuffer[i]);
        }

        /* draw low pass filter data - blue */
        stroke(0, 127, 255, 127);
        for (int i = 1; i < mSamplerLowPassFilterBuffer.length; i++) {
            line(i - 1, mSamplerLowPassFilterBuffer[i - 1], i, mSamplerLowPassFilterBuffer[i]);
        }
        /* draw butterworth data - green */
        stroke(0, 255, 127, 127);
        for (int i = 1; i < mSamplerButterworthBuffer.length; i++) {
            line(i - 1, mSamplerButterworthBuffer[i - 1], i, mSamplerButterworthBuffer[i]);
        }
    }

    private float sensor_data() {
//        return mouseX + (noise(frameCount * 0.1f) * (height * 0.1f)) - height * 0.2f;
        return mouseX + random(-height * 0.1f, height * 0.1f);
    }

    public static void main(String[] args) {
        PApplet.main(SketchFilteringSensorReadings.class.getName());
    }
}
