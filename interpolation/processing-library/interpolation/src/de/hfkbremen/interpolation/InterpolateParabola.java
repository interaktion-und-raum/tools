package de.hfkbremen.interpolation;

public class InterpolateParabola implements InterpolatorKernel {

    private final float _myExponent;

    public InterpolateParabola(float theExponent) {
        _myExponent = theExponent;
    }

    public float get(final float theDelta) {
        final float myValue = (theDelta * 2) - 1;
        return (float) Math.pow(myValue, _myExponent);
    }
}
