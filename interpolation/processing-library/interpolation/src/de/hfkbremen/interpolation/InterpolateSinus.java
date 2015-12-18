package de.hfkbremen.interpolation;

public class InterpolateSinus implements InterpolatorKernel {

    private float _myXOffset;

    private final float _myYOffset;

    private final float _myFrequency;

    private final float _myAmplitude;

    public InterpolateSinus(float theXOffset, float theYOffset, float theFrequency, float theAmplitude) {
        _myXOffset = theXOffset;
        _myYOffset = theYOffset;
        _myFrequency = theFrequency;
        _myAmplitude = theAmplitude;
    }

    public float get(final float theDelta) {
        return (float) (Math.sin((theDelta + _myXOffset) * Math.PI * _myFrequency) + _myYOffset) * _myAmplitude;
    }

    public void setXOffset(float theXOffset) {
        _myXOffset = theXOffset;
    }

}
