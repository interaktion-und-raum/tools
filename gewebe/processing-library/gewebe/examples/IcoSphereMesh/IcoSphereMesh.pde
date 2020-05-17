import de.hfkbremen.gewebe.*; 
import java.awt.*; 
import java.awt.geom.*; 
import org.sunflow.*; 


Mesh mMesh;
void settings() {
    size(640, 480, P3D);
}
void setup() {
    mMesh = IcoSphere.mesh(4);
}
void draw() {
    background(255);
    stroke(0);
    noFill();
    strokeWeight(1.0f / 100.0f);
    translate(width / 2, height / 2);
    rotateX(frameCount / 180.0f);
    rotateY(0.33f * frameCount / 180.0f);
    scale(100);
    mMesh.draw(g);
}
