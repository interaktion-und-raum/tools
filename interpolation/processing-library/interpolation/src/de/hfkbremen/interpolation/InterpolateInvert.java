package de.hfkbremen.interpolation;

public class InterpolateInvert implements InterpolatorKernel {

    private final float _myBase;

    public InterpolateInvert(float theBase) {
        _myBase = theBase;
    }

    public InterpolateInvert() {
        this(1);
    }

    public float get(final float theDelta) {
        return _myBase - theDelta;
    }
}
