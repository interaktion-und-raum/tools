package de.hfkbremen.interpolation;

public class InterpolateBezier implements InterpolatorKernel {

    private final float _myStartControl;

    private final float _myEndControl;

    public InterpolateBezier(final float theStartControl, final float theEndControl) {
        _myStartControl = theStartControl;
        _myEndControl = theEndControl;
    }

    public float get(final float theDelta) {
        final float c = 3 * _myStartControl;
        final float b = 3 * (_myEndControl - _myStartControl) - c;
        final float a = 1 - c - b;
        return a * theDelta * theDelta * theDelta + b * theDelta * theDelta + c * theDelta;
    }
}
