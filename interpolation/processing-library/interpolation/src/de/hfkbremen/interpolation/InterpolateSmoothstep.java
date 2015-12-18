package de.hfkbremen.interpolation;

public class InterpolateSmoothstep implements InterpolatorKernel {

    private final float _myMin;

    private final float _myMax;

    public InterpolateSmoothstep(float theMin, float theMax) {
        _myMin = theMin;
        _myMax = theMax;
    }

    public float get(final float theDelta) {
        if (theDelta <= _myMin) {
            return 0.0f;
        } else if (theDelta >= _myMax) {
            return 1.0f;
        } else {
            float t = (theDelta - _myMin) / (_myMax - _myMin);
            return t * t * (3.0f - 2.0f * t);
        }
    }

}
