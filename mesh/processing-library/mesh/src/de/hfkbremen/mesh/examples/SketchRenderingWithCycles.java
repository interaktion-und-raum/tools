package de.hfkbremen.mesh.examples;

import de.hfkbremen.mesh.RendererCycles;
import processing.core.PApplet;

public class SketchRenderingWithCycles extends PApplet {

    private boolean record = false;

    public void settings() {
        size(640, 480, P3D);
    }

    public void setup() {
        noStroke();
        sphereDetail(12);
    }

    public void draw() {
        if (record) {
            // @TODO need to set path to cycles manually
//            RendererCycles.CYCLES_BINARY_PATH = "/Users/dennisppaul/dev/tools/git/tools/mesh/lib/";
            de.hfkbremen.mesh.RendererCycles.OPTION_NUMBER_OF_SAMPLES = 25;
            de.hfkbremen.mesh.RendererCycles.OPTION_IMAGE_FILE_TYPE =
                    de.hfkbremen.mesh.RendererCycles.IMAGE_FILE_TYPE_JPG;
            de.hfkbremen.mesh.RendererCycles.RENDERING_PROCESS_BLOCKING = true;
            de.hfkbremen.mesh.RendererCycles.DEBUG_PRINT_RENDER_PROGRESS = true;

            String mOutputFile = System.getProperty("user.home") + "/Desktop/foobar" + frameCount + ".xml";
            beginRaw(createGraphics(640 * 2, 480 * 2, RendererCycles.name(), mOutputFile));
        }

        // @TODO `background` is not handled yet
        background(0);

        translate(width / 2.0f, height / 2.0f);
        rotateX(frameCount * 0.0133f);
        rotateY(0.9f);
        scale(1, 1, 1);
        fill(255, 127, 0);
        rect(-50, -50, 100, 100);

        // @TODO `stroke` is not working yet
        //        stroke(0);

        for (int i = 0; i < 5; i++) {
            // @TODO `fill` is not handled yet
            fill(0, 127, i * 255 / 5.0f);
            translate(10, 5, 1);
            box(100);
        }

        // @TODO `line` is not handled yet
        for (int i = 0; i < 100; i++) {
            line(random(-width), random(-height), random(-width), random(width), random(height), random(width));
        }

        //        lights();
        //        background(0);
        //        translate(width / 3, height / 3, -200);
        //        rotateZ(map(mouseY, 0, height, 0, PI));
        //        rotateY(map(mouseX, 0, width, 0, HALF_PI));
        //        for (int y = -2; y < 2; y++) {
        //            for (int x = -2; x < 2; x++) {
        //                for (int z = -2; z < 2; z++) {
        //                    pushMatrix();
        //                    translate(120 * x, 120 * y, -120 * z);
        //                    sphere(30);
        //                    popMatrix();
        //                }
        //            }
        //        }
        if (record) {
            endRaw();
            record = false; // Stop recording to the file
        }
    }

    public void keyPressed() {
        if (key == 'R' || key == 'r') { // Press R to save the file
            record = true;
        }
    }

    public static void main(String[] args) {
        PApplet.main(SketchRenderingWithCycles.class.getName());
    }
}
