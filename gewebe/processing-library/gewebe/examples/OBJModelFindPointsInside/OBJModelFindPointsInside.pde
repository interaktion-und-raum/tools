import de.hfkbremen.gewebe.*; 
import java.awt.*; 
import java.awt.geom.*; 
import org.sunflow.*; 


static PVector mCenterOfMass;
PVector mPoint = new PVector();
ArrayList<Triangle> mTriangles;
ArrayList<PVector> mPoints;
void settings() {
    size(640, 480, P3D);
}
void setup() {
    ModelData mModelData = ModelLoaderOBJ.parseModelData(OBJWeirdObject.DATA);
    Mesh mModelMesh = mModelData.mesh();
    mTriangles = mModelMesh.triangles();
    mCenterOfMass = mModelMesh.calcCenterOfMass();
    mPoints = new ArrayList();
}
void draw() {
    background(255);
    prepareView();
    drawMesh();
    queryPointPosition();
    drawPointsInside();
}
void prepareView() {
    translate(width / 2, height / 2, -200);
    rotateX(sin(frameCount * 0.01f) * TWO_PI);
    rotateY(cos(frameCount * 0.0037f) * TWO_PI);
}
void drawMesh() {
    stroke(50);
    noFill();
    beginShape(TRIANGLES);
    for (Triangle t : mTriangles) {
        vertex(t.a.x, t.a.y, t.a.z);
        vertex(t.b.x, t.b.y, t.b.z);
        vertex(t.c.x, t.c.y, t.c.z);
    }
    endShape();
}
void drawPointsInside() {
    for (PVector p : mPoints) {
        noStroke();
        fill(0, 127, 255);
        pushMatrix();
        translate(p.x, p.y, p.z);
        sphere(10);
        popMatrix();
    }
}
void queryPointPosition() {
    /* is random point inside mesh? */
    final float r = 400;
    mPoint.x = random(-r, r);
    mPoint.y = random(-r, r);
    mPoint.z = random(-r, r);
    final PVector mDirection = PVector.sub(mCenterOfMass, mPoint);
    if (MeshUtil.isPointInsideMesh(mTriangles, mPoint, mDirection)) {
        mPoints.add(new PVector().set(mPoint));
    }
}
