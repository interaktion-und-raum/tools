import de.hfkbremen.filter.*;

Filter mFilterAverage;
Filter mFilterLowPassFilter;
Filter mFilterButterworthFilter;
static final int WIDTH = 640;
final float[] mRawDataBuffer = new float[WIDTH];
final float[] mSamplerAverageBuffer = new float[WIDTH];
final float[] mSamplerLowPassFilterBuffer = new float[WIDTH];
final float[] mSamplerButterworthBuffer = new float[WIDTH];
int mViewPointer;
void settings() {
    size(WIDTH, 480);
}
void setup() {
    mFilterAverage = new FilterAverage(50);
    mFilterLowPassFilter = new FilterLowPass(0.035f);
    mFilterButterworthFilter = new FilterButterworth(21000, 750);
//    mFilterButterworthFilter = new FilterMedian(50);
}
void draw() {
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
float sensor_data() {
//    return mouseX + (noise(frameCount * 0.1f) * (height * 0.1f)) - height * 0.2f;
    return mouseX + random(-height * 0.1f, height * 0.1f);
}
