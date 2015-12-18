package de.hfkbremen.interpolation;

public class InterpolateExponential implements InterpolatorKernel {

    private final float _myExponent;

    public InterpolateExponential(float theExponent) {
        _myExponent = theExponent;
    }

    public float get(final float theDelta) {
        return (float) Math.pow(theDelta, _myExponent);
    }
}
