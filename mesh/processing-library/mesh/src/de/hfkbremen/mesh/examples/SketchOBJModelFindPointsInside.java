package de.hfkbremen.mesh.examples;

import de.hfkbremen.mesh.Mesh;
import de.hfkbremen.mesh.MeshUtil;
import de.hfkbremen.mesh.ModelData;
import de.hfkbremen.mesh.ModelLoaderOBJ;
import de.hfkbremen.mesh.OBJWeirdObject;
import de.hfkbremen.mesh.Triangle;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class SketchOBJModelFindPointsInside extends PApplet {

    private static PVector mCenterOfMass;
    PVector mPoint = new PVector();
    private ArrayList<Triangle> mTriangles;
    private ArrayList<PVector> mPoints;

    public void settings() {
        size(640, 480, P3D);
    }

    public void setup() {
        ModelData mModelData = ModelLoaderOBJ.parseModelData(OBJWeirdObject.DATA);
        Mesh mModelMesh = mModelData.mesh();
        mTriangles = mModelMesh.triangles();
        mCenterOfMass = mModelMesh.calcCenterOfMass();
        mPoints = new ArrayList<>();
    }

    public void draw() {
        background(255);
        prepareView();
        drawMesh();
        queryPointPosition();
        drawPointsInside();
    }

    private void prepareView() {
        translate(width / 2, height / 2, -200);
        rotateX(sin(frameCount * 0.01f) * TWO_PI);
        rotateY(cos(frameCount * 0.0037f) * TWO_PI);
    }

    private void drawMesh() {
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

    private void drawPointsInside() {
        for (PVector p : mPoints) {
            noStroke();
            fill(0, 127, 255);
            pushMatrix();
            translate(p.x, p.y, p.z);
            sphere(10);
            popMatrix();
        }
    }

    public void queryPointPosition() {
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

    public static void main(String[] args) {
        PApplet.main(SketchOBJModelFindPointsInside.class.getName());
    }
}
