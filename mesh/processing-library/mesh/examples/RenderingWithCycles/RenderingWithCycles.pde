import de.hfkbremen.mesh.*; 
import java.awt.*; 
import java.awt.geom.*; 


/**
 * # NOTES ON USING CYCLES RENDERER
 *
 * - `background()` is ignored. background color must be set manually via `RendererCycles.BACKGROUND_COLOR`
 * - the path to the cycles executable can be changed via `RendererCycles.CYCLES_BINARY_PATH`
 * - sketch can be forced to wait until renderer is finished via `RendererCycles.RENDERING_PROCESS_BLOCKING`
 * - image output file type ( jpg, png, tga ) can be selected via `RendererCycles.OPTION_IMAGE_FILE_TYPE`
 *
 * ## KNOWN LIMITATIONS
 *
 * - if image size is not equal to sketch size the viewport is not scaled
 * - `stroke()` does not work
 * - `line()` does not work yet
 * - `lights()` does not work yet
 * - `begin/endShape()` might not work as expected
 */
boolean record = false;
void settings() {
    size(640, 480, P3D);
}
void setup() {
    noStroke();
    sphereDetail(12);
}
void draw() {
    if (record) {
        // RendererCycles.CYCLES_BINARY_PATH = "/Users/dennisppaul/dev/tools/git/tools/mesh/lib/";
        RendererCycles.OPTION_NUMBER_OF_SAMPLES = 25;
        RendererCycles.OPTION_IMAGE_FILE_TYPE = RendererCycles.IMAGE_FILE_TYPE_JPG;
        RendererCycles.RENDERING_PROCESS_BLOCKING = true;
        RendererCycles.DEBUG_PRINT_RENDER_PROGRESS = false;
        RendererCycles.BACKGROUND_COLOR.set(1.0f, 0.5f, 1.0f);
        String mOutputFile = System.getProperty("user.home") + "/Desktop/foobar/foobar" + frameCount + ".xml";
        // @TODO need to re-scale camera viewport
        beginRaw(createGraphics(640 * 2, 480 * 2, RendererCycles.name(), mOutputFile));
    }
    background(50);
    translate(width / 2.0f, height / 2.0f);
    rotateX(frameCount * 0.0133f);
    rotateY(0.9f);
    scale(1, 1, 1);
    /* 2D shape */
    debug_println("rect");
    noStroke();
    fill(255, 127, 0);
    rect(-50, -50, 100, 100);
    /* 3D shapes */
    debug_println("sphere");
    noStroke();
    // @TODO there is a problem with white colors
    noFill();
    stroke(255, 255, 255, 127);
    sphere(30);
    debug_println("box");
    fill(0, 127, 255);
    box(50);
    /* lines */
    // @TODO `stroke` is not working properly yet
    // @TODO `line` is not handled yet
    noFill();
    stroke(255, 0, 127);
    for (int i = 0; i < 100; i++) {
        line(random(-width, width), random(-height, height), random(-width, width),
             random(-width, width), random(-height, height), random(-width, width));
    }
    // @TODO `lights` is not handled yet
    //        lights();
    if (record) {
        endRaw();
        record = false; // Stop recording to the file
    }
}
void keyPressed() {
    if (key == 'R' || key == 'r') { // Press R to save the file
        record = true;
    }
}
void debug_println(String s) {
    if (record) { println(s); }
}
