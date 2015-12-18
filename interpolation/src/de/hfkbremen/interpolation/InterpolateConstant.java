package de.hfkbremen.interpolation;

public class InterpolateConstant implements InterpolatorKernel {

    private final float _myConstant;

    public InterpolateConstant(float theConstant) {
        _myConstant = theConstant;
    }

    public float get(final float theDelta) {
        return _myConstant;
    }
}
