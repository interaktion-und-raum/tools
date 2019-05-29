package de.hfkbremen.filter;

public class FilterMedian implements Filter {

    private final int N;
    private final float[] val;
    private boolean[] big;
    private int med, i;
    private float mMedian;

    public FilterMedian(int window) {
        N = window;
        big = new boolean[N];
        val = new float[N];
        big = new boolean[N];
        i = 0;
        for (int j = 0; j < N; j++) {
            val[j] = 0;
            big[j] = j > N / 2;
        }
        med = 0;
        mMedian = 0;
    }

    private int findmax() {
        float m = Float.MIN_VALUE;
        int n = -1;
        for (int j = 0; j < N; j++) {
            if (j == med) {
                continue;
            }
            if (!big[j]) { //find max
                if (val[j] > m) {
                    m = val[j];
                    n = j;
                }
            }
        }
        return n;
    }

    private int findmin() {
        float m = Float.MAX_VALUE;
        int n = -1;
        for (int j = 0; j < N; j++) {
            if (big[j]) { //find min
                if (val[j] < m) {
                    m = val[j];
                    n = j;
                }
            }
        }
        return n;
    }

    public void add_sample(float pSample) {
//        public float process(float in) {
        //the value at position 'i' is to be replaced by 'in' and the new median is computed
        //var 'median' refers to the old median
        //  val[j] <= median <= val[k]
        //by convention the mediam is considered small
        val[i] = pSample;
        if (i == med) { //the median itself is removed (not the value but the actual sample)
            if (pSample <= mMedian) { //the new value is smaller than or equal to the old median and may be the new median
                med = -1;  //hack to include the median cell in the comparison
                med = findmax(); //the largest small value is the new median
            } else { //the new value is larger than the old median and may be the new median
                big[i] = true; //add the new val to the big set, which is now 1 too large
                med = findmin();
                big[med] = false;
            }
        } else if (!big[i]) {//old value is removed from small values
            if (pSample <= mMedian) {
                //replace small with small, median not affected
            } else { //the new value is large
                big[i] = true;
                med = findmin();
                big[med] = false;
            }
        } else //old value is large
         if (pSample <= mMedian) { //but the new value is small
                big[i] = false;
                big[med] = true;
                med = findmax();
            } else {//new value is also large
                //replace large with large, median not affected
            }
        if (++i >= N) {
            i = 0;
        }
        if (med >= 0) {
            mMedian = val[med];
        }
//            return median;
    }

    public float get() {
        return mMedian;
    }
}
