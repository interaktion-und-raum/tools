import de.hfkbremen.filter.*;

Filter mFilterAverage;
int mViewPointer;
void settings() {
    size(640, 480);
}
void setup() {
    mFilterAverage = new FilterAverage(50);
    background(255);
}
void draw() {
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
float sensor_data() {
    return mouseX + random(-height * 0.05f, height * 0.05f);
}
