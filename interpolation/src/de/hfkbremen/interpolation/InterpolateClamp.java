package de.hfkbremen.interpolation;

public class InterpolateClamp implements InterpolatorKernel {

    private final float _myMin;

    private final float _myMax;

    public InterpolateClamp(final float theMin, final float theMax) {
        _myMin = theMin;
        _myMax = theMax;
    }

    public float get(final float theDelta) {
        if (theDelta <= _myMin) {
            return 0.0f;
        } else if (theDelta >= _myMax) {
            return 1.0f;
        } else {
            return (theDelta - _myMin) / (_myMax - _myMin);
        }
    }
}
