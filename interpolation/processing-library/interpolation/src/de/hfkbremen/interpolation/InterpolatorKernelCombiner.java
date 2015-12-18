package de.hfkbremen.interpolation;

public class InterpolatorKernelCombiner implements InterpolatorKernel {

    private final InterpolatorKernel _myInnerKernel;

    private final InterpolatorKernel _myOutterKernel;

    public InterpolatorKernelCombiner(InterpolatorKernel theInnerKernel, InterpolatorKernel theOutterKernel) {
        _myInnerKernel = theInnerKernel;
        _myOutterKernel = theOutterKernel;
    }

    public float get(final float theDelta) {
        return _myOutterKernel.get(_myInnerKernel.get(theDelta));
    }
}
