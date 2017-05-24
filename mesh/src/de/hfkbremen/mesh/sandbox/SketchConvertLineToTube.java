package de.hfkbremen.mesh.sandbox;

import de.hfkbremen.mesh.ArcBall;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class SketchConvertLineToTube extends PApplet {

    private ArcBall mArcball;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        mArcball = ArcBall.setupRotateAroundCenter(this, false);
    }

    public void draw() {
        background(0, 127, 255);
        lights();
        translate(width / 2, height / 2);

        noStroke();
        fill(255);
        float v = 200;
        PVector p = null;
        for (int i = -10; i < 360; i += 10) {
            float r = radians(i);
            float x = sin(r) * v;
            float y = cos(r) * v;
            drawLineSegment(new PVector(-y, x, v), new PVector(x, y, -v), 1);
            drawLineSegment(new PVector(x, y, v), new PVector(x, y, v * 1.1f), 0.5f);
            drawLineSegment(new PVector(x, y, -v), new PVector(x, y, -v * 1.1f), 0.5f);
            if (p != null) {
                drawLineSegment(new PVector(p.x, p.y, v), new PVector(x, y, v), 2);
                drawLineSegment(new PVector(-p.y, p.x, -v), new PVector(-y, x, -v), 4);
            }
            p = new PVector(x, y, v);
        }
    }

    private void drawLineSegment(PVector p0, PVector p1, float mSize) {
        ArrayList<PVector> mTriangles = extrudeLine(p0, p1, mSize, null);
        beginShape(TRIANGLES);
        for (PVector p : mTriangles) {
            vertex(p);
        }
        endShape();
    }

    private boolean parallel(PVector p0, PVector p1) {
        final float mDot = abs(PVector.dot(p0.normalize(null), p1.normalize(null)));
        return mDot == 1.0f;
    }

    private boolean isAlmostParallel(PVector p0, PVector p1, float pEpsilon) {
        final float mDot = abs(PVector.dot(p0.normalize(null), p1.normalize(null))) - 1.0f;
        return mDot > -pEpsilon && mDot < pEpsilon;
    }

    private ArrayList<PVector> extrudeLine(PVector p0, PVector p1, float pSize, ArrayList<PVector> mTriangles) {
        if (mTriangles == null) {
            mTriangles = new ArrayList<>();
        }

        PVector mUpApprox = approximateUpVector(p0, p1);
        PVector mForward = PVector.sub(p0, p1).normalize(null);
        PVector mSide = PVector.cross(mForward, mUpApprox, null).normalize(null);
        PVector mUp = PVector.cross(mForward, mSide, null).normalize(null);

        PVector s00 = PVector.add(p0, PVector.mult(mSide, pSize));
        PVector s01 = PVector.add(p0, PVector.mult(mUp, pSize));
        PVector s02 = PVector.add(p0, PVector.mult(mSide, -pSize));
        PVector s03 = PVector.add(p0, PVector.mult(mUp, -pSize));

        PVector s10 = PVector.add(p1, PVector.mult(mSide, pSize));
        PVector s11 = PVector.add(p1, PVector.mult(mUp, pSize));
        PVector s12 = PVector.add(p1, PVector.mult(mSide, -pSize));
        PVector s13 = PVector.add(p1, PVector.mult(mUp, -pSize));

        // cap front
        mTriangles.add(s00);
        mTriangles.add(s01);
        mTriangles.add(s02);

        mTriangles.add(s00);
        mTriangles.add(s02);
        mTriangles.add(s03);

        // cap back
        mTriangles.add(s10);
        mTriangles.add(s11);
        mTriangles.add(s12);

        mTriangles.add(s10);
        mTriangles.add(s12);
        mTriangles.add(s13);

        // hull
        mTriangles.add(s00);
        mTriangles.add(s10);
        mTriangles.add(s11);

        mTriangles.add(s00);
        mTriangles.add(s11);
        mTriangles.add(s01);

        mTriangles.add(s01);
        mTriangles.add(s11);
        mTriangles.add(s12);

        mTriangles.add(s01);
        mTriangles.add(s12);
        mTriangles.add(s02);

        mTriangles.add(s02);
        mTriangles.add(s12);
        mTriangles.add(s13);

        mTriangles.add(s02);
        mTriangles.add(s13);
        mTriangles.add(s03);

        mTriangles.add(s03);
        mTriangles.add(s13);
        mTriangles.add(s10);

        mTriangles.add(s03);
        mTriangles.add(s10);
        mTriangles.add(s00);

        return mTriangles;
    }

    private PVector approximateUpVector(PVector p0, PVector p1) {
        PVector d = PVector.sub(p0, p1);
        PVector mUpApprox = new PVector(1, 0, 0); // x
        if (!parallel(d, mUpApprox)) {
            return mUpApprox;
        }
        mUpApprox.set(0, 1, 0); // y
        if (!parallel(d, mUpApprox)) {
            return mUpApprox;
        }
        mUpApprox.set(0, 0, 1); // z
        return mUpApprox;
    }

    private void drawExtrudedLine(PVector p0, PVector p1, float pSize) {
        PVector mUpApprox = approximateUpVector(p0, p1);
        PVector mForward = PVector.sub(p0, p1).normalize(null);
        PVector mSide = PVector.cross(mForward, mUpApprox, null).normalize(null);
        PVector mUp = PVector.cross(mForward, mSide, null).normalize(null);

        //        drawCap(p0, mSide, mUp, mSize);
        //        drawCap(p1, mSide, mUp, mSize);

        drawHull(p0, p1, mSide, mUp, pSize);
    }

    private void drawCap(PVector c, PVector pSide, PVector pUp, float pSize) {
        PVector s0 = PVector.add(c, PVector.mult(pSide, pSize));
        PVector s1 = PVector.add(c, PVector.mult(pUp, pSize));
        PVector s2 = PVector.add(c, PVector.mult(pSide, -pSize));
        PVector s3 = PVector.add(c, PVector.mult(pUp, -pSize));
        beginShape();
        vertex(s0);
        vertex(s1);
        vertex(s2);
        vertex(s3);
        endShape(CLOSE);
    }

    private void drawHull(PVector p0, PVector p1, PVector mSide, PVector mUp, float mSize) {
        PVector s00 = PVector.add(p0, PVector.mult(mSide, mSize));
        PVector s01 = PVector.add(p0, PVector.mult(mUp, mSize));
        PVector s02 = PVector.add(p0, PVector.mult(mSide, -mSize));
        PVector s03 = PVector.add(p0, PVector.mult(mUp, -mSize));

        PVector s10 = PVector.add(p1, PVector.mult(mSide, mSize));
        PVector s11 = PVector.add(p1, PVector.mult(mUp, mSize));
        PVector s12 = PVector.add(p1, PVector.mult(mSide, -mSize));
        PVector s13 = PVector.add(p1, PVector.mult(mUp, -mSize));

        beginShape(TRIANGLES);

        // cap front
        vertex(s00);
        vertex(s01);
        vertex(s02);

        vertex(s00);
        vertex(s02);
        vertex(s03);

        // cap back
        vertex(s10);
        vertex(s11);
        vertex(s12);

        vertex(s10);
        vertex(s12);
        vertex(s13);

        // hull
        vertex(s00);
        vertex(s10);
        vertex(s11);

        vertex(s00);
        vertex(s11);
        vertex(s01);

        vertex(s01);
        vertex(s11);
        vertex(s12);

        vertex(s01);
        vertex(s12);
        vertex(s02);

        vertex(s02);
        vertex(s12);
        vertex(s13);

        vertex(s02);
        vertex(s13);
        vertex(s03);

        vertex(s03);
        vertex(s13);
        vertex(s10);

        vertex(s03);
        vertex(s10);
        vertex(s00);

        endShape();
    }

    private void vertex(PVector p) {
        vertex(p.x, p.y, p.z);
    }

    public static void main(String[] args) {
        PApplet.main(SketchConvertLineToTube.class.getName());
    }
}
