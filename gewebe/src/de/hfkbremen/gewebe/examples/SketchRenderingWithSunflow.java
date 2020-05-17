package de.hfkbremen.gewebe.examples;

import de.hfkbremen.gewebe.RendererSunflow;
import processing.core.PApplet;

public class SketchRenderingWithSunflow extends PApplet {

    private boolean record = false;

    public void settings() {
        size(1024, 768, P3D);
    }

    public void setup() {
    }

    public void draw() {
        String mOutputFile = "";
        if (record) {
            RendererSunflow.BACKGROUND_COLOR.set(0.2f);
            mOutputFile = "sunflow" + nf(frameCount, 4);
            beginRaw(createGraphics(width, height, RendererSunflow.name(), mOutputFile));
        }

        background(50);
        translate(width / 2.0f, height / 2.0f);
        rotateX(frameCount * 0.007f);
        rotateY(frameCount * 0.013f);

        for (int i = 0; i < 30; i++) {
            pushMatrix();
            noStroke();
            fill(random(255), random(255), random(255));
            final float mRange = 100;
            translate(
                    random(-mRange, mRange),
                    random(-mRange, mRange),
                    random(-mRange, mRange)
                     );
            sphere(random(10, 30));
            popMatrix();
        }

        noStroke();
        fill(255);

        sphere(100);

        translate(-50, 0);
        sphere(80);

        translate(100, 0);
        sphere(90);

        if (record) {
            endRaw();
            saveFrame(mOutputFile + ".screen.png");
            record = false;
        }
    }

    public void keyPressed() {
        if (key == ' ') {
            record = true;
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchRenderingWithSunflow.class.getName());
    }
}
