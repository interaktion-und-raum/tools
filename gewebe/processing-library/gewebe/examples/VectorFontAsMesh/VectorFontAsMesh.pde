import de.hfkbremen.gewebe.*; 
import java.awt.*; 
import java.awt.geom.*; 
import org.sunflow.*; 


VectorFont mPathCreator;
void settings() {
    size(640, 480, P3D);
}
void setup() {
    mPathCreator = new VectorFont("Helvetica", 128);
}
void draw() {
    mPathCreator.outline_flatness((float) mouseX / (float) width * 5);
    ArrayList<PVector> mVertices = mPathCreator.vertices("01.01.1970");
    Mesh mMesh = MeshUtil.mesh(mVertices);
    background(255);
    noFill();
    stroke(0);
    translate(0, mouseY);
    mMesh.draw(g);
}
