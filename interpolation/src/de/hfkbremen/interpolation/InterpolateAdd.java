package de.hfkbremen.interpolation;

public class InterpolateAdd implements InterpolatorKernel {

    private final float _myAddValue;

    public InterpolateAdd(float theAddValue) {
        _myAddValue = theAddValue;
    }

    public float get(final float theDelta) {
        return theDelta + _myAddValue;
    }
}
