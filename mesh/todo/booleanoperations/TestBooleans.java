package claylike.booleanoperations;


import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;

import mathematik.Vector3f;

import data.Resource;
import processing.core.PApplet;


public class TestBooleans
    extends PApplet {

    private Solid myA;

    private Solid myB;

    public void setup() {
        size(640, 480, OPENGL);

        ModelData myModelDataA = ModelLoaderOBJ.getModelData(Resource.getStream("booleanoperations/cube.obj"));
        Vector3f[] myVerticesA = new Vector3f[myModelDataA.vertices.length / 3];
        for (int i = 0; i < myModelDataA.vertices.length; i += 3) {
            myVerticesA[i / 3] = new Vector3f(myModelDataA.vertices[i + 0],
                                              myModelDataA.vertices[i + 1],
                                              myModelDataA.vertices[i + 2]);
        }
        int[] myFacesA = new int[myVerticesA.length];
        for (int i = 0; i < myFacesA.length; i++) {
            myFacesA[i] = i;
        }
        myA = new Solid(myVerticesA, myFacesA);

        ModelData myModelDataB = ModelLoaderOBJ.getModelData(Resource.getStream("booleanoperations/sphere.obj"));
        Vector3f[] myVerticesB = new Vector3f[myModelDataB.vertices.length / 3];
        for (int i = 0; i < myModelDataB.vertices.length; i += 3) {
            myVerticesB[i / 3] = new Vector3f(myModelDataB.vertices[i + 0],
                                              myModelDataB.vertices[i + 1],
                                              myModelDataB.vertices[i + 2]);
        }
        int[] myFacesB = new int[myVerticesB.length];
        for (int i = 0; i < myFacesB.length; i++) {
            myFacesB[i] = i;
        }
        myB = new Solid(myVerticesB, myFacesB);
    }


    private float myRotation;

    public void draw() {
        background(255);
        translate(width / 2, height / 2);
        rotateY(myRotation += 1 / 30f * 0.5f);

        stroke(127);
        if (mousePressed) {
            noFill();
//            myB.translate(1, 0, 0);
            BooleanModeller myBooleanModeller = new BooleanModeller(myB, myA);
//            Solid myIntersection = myBooleanModeller.getDifference();
//            Solid myIntersection = myBooleanModeller.getIntersection();
            Solid myIntersection = myBooleanModeller.getUnion();
            drawSolid(myIntersection);
        } else {
            fill(255, 0, 0);
            drawSolid(myA);
            fill(0, 255, 0);
            drawSolid(myB);
        }
    }


    private void drawSolid(Solid theSolid) {
        beginShape(TRIANGLES);
        for (int i = 0; i < theSolid.getIndices().length; i++) {
            final Vector3f v = theSolid.getVertices()[theSolid.getIndices()[i]];
            vertex(v.x, v.y, v.z);
        }
        endShape();
    }


    public static void main(String[] args) {
        PApplet.main(new String[] {TestBooleans.class.getName()});
    }
}
