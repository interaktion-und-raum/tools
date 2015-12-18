package de.hfkbremen.interpolation.examples;

import de.hfkbremen.interpolation.*;
import processing.core.PApplet;
import processing.core.PVector;

public class SketchDrawInterpolators extends PApplet {

    private float mCounter;

    private Interpolator mInterpolator;

    private final PVector mPrevPosition = new PVector();

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mInterpolator = new Interpolator(new InterpolateLinear());
        mCounter = 0;
        background(255);
    }

    public void draw() {
        /* interpolate between 0 and 1 */
        mCounter += 1.0f / frameRate * 0.25f;
        float mX = mCounter;
        float mY = mInterpolator.get(mCounter);

        /* draw interpolated ( flip y-axis ) */
        scale(1, -1);
        translate(0, -height);
        line(mPrevPosition.x * width,
             mPrevPosition.y * height,
             mX * width,
             mY * height);
        mPrevPosition.set(mX, mY);

        /* wrap */
        if (mCounter > 1.0f) {
            reset();
        }

        /* change interpolator */
        if (keyPressed) {
            if (key == '1') {
                mInterpolator = new Interpolator(new InterpolateLinear());
            }
            if (key == '2') {
                mInterpolator = new Interpolator(new InterpolateExponential(3f));
            }
            if (key == '3') {
                mInterpolator = new Interpolator(new InterpolateOffset(0.2f, 0.7f));
            }
            if (key == '4') {
                mInterpolator = new Interpolator(new InterpolateSinus(0, 0, 0.5f, 1.0f));
            }
            if (key == '5') {
                mInterpolator = new Interpolator(new InterpolateSmoothstep(0.1f, 0.9f));
            }
            if (key == '6') {
                mInterpolator = new Interpolator(new InterpolateBezier(0.7f, 0.2f));
            }
            if (key == '7') {
                InterpolatorKernel myInner = new InterpolateExponential(3f);
                InterpolatorKernel myOutter = new InterpolateBezier(0.7f, 0.2f);
                InterpolatorKernel myCombiner = new InterpolatorKernelCombiner(myInner, myOutter);
                InterpolatorKernel myOutterCombiner = new InterpolatorKernelCombiner(new InterpolateClamp(0.2f, 0.8f), myCombiner);
                mInterpolator = new Interpolator(myOutterCombiner);
            }
            if (key == '8') {
                mInterpolator = new Interpolator(new InterpolateRandom());
            }
            if (key == '9') {
                mInterpolator = new Interpolator(new InterpolateInvert());
            }
            if (key == '0') {
                mInterpolator = new Interpolator(new InterpolateConstant(0.5f));
            }
            if (key == 'a') {
                mInterpolator = new Interpolator(new InterpolateParabola(8));
            }
            if (key == 'b') {
                mInterpolator = new Interpolator(new InterpolatePeak(0.6f));
            }
            reset();
        }
    }

    private void reset() {
        background(255);
        mCounter = 0;
        mPrevPosition.set(0, 0);
    }

    public static void main(String[] args) {
        PApplet.main(SketchDrawInterpolators.class.getName());
    }
}
