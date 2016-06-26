import de.hfkbremen.mesh.*;

CGALAlphaShape3 cgal;
float[] mPoints3f;
float[] mMeshData;
void settings() {
    size(640, 480, P3D);
}
void setup() {
    cgal = new CGALAlphaShape3();
    cgal.version();
    final int NUMBER_OF_POINTS = 50;
    mPoints3f = new float[NUMBER_OF_POINTS * 3];
    final float mRange = 1;
    for (int i = 0; i < NUMBER_OF_POINTS; i++) {
        PVector p = new PVector().set(random(-1, 1), random(-1, 1), random(-1, 1));
        p.normalize();
        p.mult(random(mRange / 2, mRange));
        mPoints3f[i * 3 + 0] = p.x;
        mPoints3f[i * 3 + 1] = p.y;
        mPoints3f[i * 3 + 2] = p.z;
    }
    cgal.compute_cgal_alpha_shape(mPoints3f);
    computeAlphaShape(0.5f);
}
void draw() {
    background(255);
    directionalLight(126, 126, 126, 0, 0, -1);
    ambientLight(102, 102, 102);
    translate(width / 2, height / 2);
    scale(100);
    rotateX(frameCount * 0.01f);
    rotateY(frameCount * 0.003f);
    fill(0, 127, 255);
    noStroke();
    if (mMeshData != null) {
        beginShape(TRIANGLES);
        for (int i = 0; i < mMeshData.length; i += 3) {
            PVector p = new PVector().set(mMeshData[i + 0], mMeshData[i + 1], mMeshData[i + 2]);
            vertex(p.x, p.y, p.z);
        }
        endShape();
    }
    strokeWeight(1f / 25f);
    stroke(255, 127, 0);
    noFill();
    beginShape(POINTS);
    for (int i = 0; i < mPoints3f.length; i += 3) {
        vertex(mPoints3f[i + 0], mPoints3f[i + 1], mPoints3f[i + 2]);
    }
    endShape();
}
void computeAlphaShape(float mAlpha) {
    mMeshData = cgal.compute_regular_mesh(mAlpha);
    final boolean DEBUG = false;
    if (DEBUG) {
        System.out.println("+++ compute_regular_mesh with alpha value : " + mAlpha);
        System.out.println("+++ number of points                      : " + mMeshData.length / 3);
    }
}
void mouseMoved() {
    computeAlphaShape(mouseX / (float) width);
}
