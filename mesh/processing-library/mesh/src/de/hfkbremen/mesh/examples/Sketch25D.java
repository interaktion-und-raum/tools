package de.hfkbremen.mesh.examples;

import processing.core.PApplet;

public class Sketch25D extends PApplet {

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
        rectMode(CORNERS);
        //        new ArcBall(this);
    }

    public void draw() {
        background(255);

        // camera
        translate(width / 2.0f, height / 2.0f);
        rotateX(radians(mouseY * 0.25f));
        rotateZ(radians(mouseX * 0.25f));

        stroke(0);
        noFill();
        drawMirror(0, 0, radians(frameCount));

        rect(-width / 2.0f, -height / 2.0f, width / 2.0f, height / 2.0f);

        stroke(255, 0, 0);
        pushMatrix();
        translate(0, 0, 50);
        line(300, 100, 0, 0);
        popMatrix();
    }

    private void drawMirror(float x, float y, float r) {
        final float mMirrorWidth = 50;
        final float mMirrorHeight = 100;
        float x1 = sin(r) * mMirrorWidth + x;
        float y1 = cos(r) * mMirrorWidth + y;
        float x2 = sin(r - PI) * mMirrorWidth + x;
        float y2 = cos(r - PI) * mMirrorWidth + y;
        line(x1, y1, x2, y2);

        pushMatrix();
        translate(x, y);
        rotateX(PI / 2);
        rotateY(-r);
        rect(-mMirrorWidth / 2, 0, mMirrorWidth / 2, mMirrorHeight);
        popMatrix();
    }

    public static void main(String[] args) {
        PApplet.main(Sketch25D.class.getName());
    }
}
