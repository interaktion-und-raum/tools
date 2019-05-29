package de.hfkbremen.filter.examples;

import de.hfkbremen.filter.Filter;
import de.hfkbremen.filter.FilterAverage;
import processing.core.PApplet;

public class SketchSimpleFiltering extends PApplet {

    private Filter mFilterAverage;

    private int mViewPointer;

    public void settings() {
        size(640, 480);
    }

    public void setup() {
        mFilterAverage = new FilterAverage(50);
        background(255);
    }

    public void draw() {
        mViewPointer++;
        mViewPointer %= width;

        stroke(255);
        line(mViewPointer, 0, mViewPointer, height);

        float mRawData = sensor_data();
        stroke(0);
        point(mViewPointer, mRawData);

        mFilterAverage.add_sample(mRawData);
        stroke(255, 127, 0);
        point(mViewPointer, mFilterAverage.get());
    }

    private float sensor_data() {
        return mouseX + random(-height * 0.05f, height * 0.05f);
    }

    public static void main(String[] args) {
        PApplet.main(SketchSimpleFiltering.class.getName());
    }
}
