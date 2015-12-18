package de.hfkbremen.interpolation;

public class Interpolator implements InterpolatorKernel {

    private final float _myStart;

    private final float _myEnd;

    private final float _myDifference;

    private InterpolatorKernel _myKernel;

    public Interpolator(float theStart, float theEnd, InterpolatorKernel theKernel) {
        _myStart = theStart;
        _myEnd = theEnd;
        _myDifference = _myEnd - _myStart;
        _myKernel = theKernel;
    }

    public Interpolator(InterpolatorKernel theKernel) {
        this(0, 1, theKernel);
    }

    public void setKernel(InterpolatorKernel theKernel) {
        _myKernel = theKernel;
    }

    public float get(final float theDelta) {
        return _myStart + _myDifference * _myKernel.get(theDelta);
    }
}
