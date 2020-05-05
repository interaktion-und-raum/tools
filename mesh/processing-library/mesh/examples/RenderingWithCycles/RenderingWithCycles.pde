import de.hfkbremen.mesh.*; 
import java.awt.*; 
import java.awt.geom.*; 


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
        RendererCycles mCycles = RendererCycles.beginRaw(this, System.getProperty("user.home") +  "/Desktop/foobar.xml");
        mCycles.getXML().print();
    }
    background(255);
    translate(width / 2.0f, height / 2.0f);
    rotateX(0.5f);
    rotateY(0.9f);
    scale(1, 1, 1);
    fill(255, 127, 0);
    rect(-50, -50, 100, 100);
    for (int i = 0; i < 5; i++) {
        fill(0, 127, i * 255 / 5.0f);
        translate(10, 5, 1);
        box(100);
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
void keyPressed() {
    if (key == 'R' || key == 'r') { // Press R to save the file
        record = true;
    }
}
