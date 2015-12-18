package de.hfkbremen.interpolation;

public class InterpolateRandom implements InterpolatorKernel {

    private Random mRandom;

    public InterpolateRandom() {
        mRandom = new Random(0);
    }

    public InterpolateRandom(long theRandomSeed) {
        mRandom = new Random(theRandomSeed);
    }

    public float get(final float theDelta) {
        return (float) mRandom.getFloat();
    }

    private static class Random {

        private static final java.util.Random SEED_GENERATOR = new java.util.Random(System.currentTimeMillis());

        private final java.util.Random myRandomNumberGenerator;

        private static final Random INSTANCE;

        static {
            INSTANCE = new Random();
        }

        public static float FLOAT(float theStart, float theEnd) {
            return INSTANCE.getFloat(theStart, theEnd);
        }

        public static float INT(int theStart, int theEnd) {
            return INSTANCE.getInt(theStart, theEnd);
        }

        public Random() {
            this(SEED_GENERATOR.nextLong());
        }

        public Random(long theSeed) {
            myRandomNumberGenerator = new java.util.Random(theSeed);
        }

        public void setSeed(long theSeed) {
            myRandomNumberGenerator.setSeed(theSeed);
        }

        public int getInt(int theStart,
                          int theEnd) {
            int myDiff = (theEnd + 1) - theStart;
            return myRandomNumberGenerator.nextInt(myDiff) + theStart;
        }

        public int getInt() {
            return myRandomNumberGenerator.nextInt();
        }

        public float getFloat(float theStart,
                              float theEnd) {
            final float myDiff = theEnd - theStart;
            final float myRandomValue = myRandomNumberGenerator.nextFloat() * myDiff;
            return myRandomValue + theStart;
        }

        public float getFloat() {
            return myRandomNumberGenerator.nextFloat();
        }
    }
}
