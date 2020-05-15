package de.hfkbremen.gewebe.examples;

import de.hfkbremen.gewebe.RendererCycles;
import processing.core.PApplet;

public class SketchRenderingWithCycles extends PApplet {

    /**
     * # NOTES ON USING CYCLES RENDERER
     *
     * - `background()` is ignored. background color must be set manually via `RendererCycles.BACKGROUND_COLOR`
     * - the path to the cycles executable can be changed via `RendererCycles.CYCLES_BINARY_PATH`
     * - sketch can be forced to wait until renderer is finished via `RendererCycles.RENDERING_PROCESS_BLOCKING`
     * - image output file type ( jpg, png, tga ) can be selected via `RendererCycles.OUTPUT_IMAGE_FILE_TYPE`
     *
     * ## KNOWN LIMITATIONS
     *
     * - if image size is not equal to sketch size the viewport is not scaled
     * - `lights()` does not work yet
     */

    private boolean record = false;

    public void settings() {
        size(640, 480, P3D);
    }

    public void setup() {
        noStroke();
        sphereDetail(12);
    }

    public void draw() {
        String mOutputFile = "";
        if (record) {
            RendererCycles.NUMBER_OF_SAMPLES = 25;
            RendererCycles.OUTPUT_IMAGE_FILE_TYPE = RendererCycles.IMAGE_FILE_TYPE_PNG;
            RendererCycles.RENDERING_PROCESS_BLOCKING = true;
            RendererCycles.DEBUG_PRINT_RENDER_PROGRESS = false;
            RendererCycles.BACKGROUND_COLOR.set(1.0f, 0.5f, 1.0f);
            RendererCycles.RENDER_VIEWPORT_SCALE = 1.0f;
            mOutputFile = "cycles" + frameCount + ".xml";
            beginRaw(createGraphics(width, height, RendererCycles.name(), mOutputFile));
        }

        background(255, 127, 255);

        // @TODO `lights` is not handled yet
        // lights();

        translate(width / 2.0f, height / 2.0f);
        rotateX(frameCount * 0.0133f);
        rotateY(0.9f);
        scale(1, 1, 1);

        /* 2D shape */
        noStroke();
        fill(255, 127, 0);
        rect(-50, -50, 100, 100);

        /* 3D shapes */
        noStroke();
        fill(0, 127, 255);
        sphere(30);

        strokeWeight(1);
        stroke(0);
        fill(255, 127, 0);
        box(50);

        noStroke();
        fill(255);
        pushMatrix();
        translate(100, 0, 0);
        box(25);
        popMatrix();

        /* lines */
        noFill();
        stroke(255, 0, 127);
        strokeWeight(2);
        for (int i = 0; i < 100; i++) {
            line(random(-width, width), random(-height, height), random(-width, width),
                 random(-width, width), random(-height, height), random(-width, width));
        }

        /* polygon */
        stroke(0);
        fill(0, 127, 255);
        strokeWeight(5);
        beginShape();
        vertex(-100, -100, 10);
        vertex(-100, 100, 10);
        vertex(100, 100, 10);
        vertex(100, -100, 10);
        endShape(CLOSE);

        if (record) {
            endRaw();
            saveFrame(mOutputFile + "screen.png");
            record = false;
        }
    }

    public void keyPressed() {
        if (key == 'R' || key == 'r') {
            record = true;
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchRenderingWithCycles.class.getName());
    }
}
