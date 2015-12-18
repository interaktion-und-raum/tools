package de.hfkbremen.interpolation;

public class InterpolateOffset implements InterpolatorKernel {

    private final float _myMin;

    private final float _myMax;

    public InterpolateOffset(final float theMin, final float theMax) {
        _myMin = theMin;
        _myMax = theMax;
    }

    public float get(final float theDelta) {
        return _myMin + theDelta * (_myMax - _myMin);
    }
}
