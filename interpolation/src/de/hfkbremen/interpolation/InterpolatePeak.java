package de.hfkbremen.interpolation;

public class InterpolatePeak implements InterpolatorKernel {

    private final float _myPeak;

    public InterpolatePeak(final float thePeak) {
        _myPeak = thePeak;
    }

    public float get(float theDelta) {
        if (theDelta < _myPeak) {
            theDelta = theDelta / _myPeak;
        } else {
            theDelta = 1.0f - (theDelta - _myPeak) / (1.0f - _myPeak);
        }
        return theDelta;
    }
}
