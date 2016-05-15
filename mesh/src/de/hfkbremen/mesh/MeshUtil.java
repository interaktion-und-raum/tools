package de.hfkbremen.mesh;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;

public class MeshUtil {

    public static int isClockWise2D(final ArrayList<PVector> mPoints) {

        if (mPoints.size() < 3) {
            return (0);
        }

        int mCount = 0;
        for (int i = 0; i < mPoints.size(); i++) {
            final PVector p1 = mPoints.get(i);
            final PVector p2 = mPoints.get((i + 1) % mPoints.size());
            final PVector p3 = mPoints.get((i + 2) % mPoints.size());
            float z;
            z = (p2.x - p1.x) * (p3.y - p2.y);
            z -= (p2.y - p1.y) * (p3.x - p2.x);
            if (z < 0) {
                mCount--;
            } else if (z > 0) {
                mCount++;
            }
        }
        if (mCount > 0) {
            return (VectorFont.COUNTERCLOCKWISE);
        } else if (mCount < 0) {
            return (VectorFont.CLOCKWISE);
        } else {
            return (0);
        }
    }

    public static boolean inside2DPolygon(final PVector thePoint, final ArrayList<PVector> thePolygon) {
        float x = thePoint.x;
        float y = thePoint.y;

        int c = 0;
        for (int i = 0, j = thePolygon.size() - 1; i < thePolygon.size(); j = i++) {
            if ((((thePolygon.get(i).y <= y) && (y < thePolygon.get(j).y)) || ((thePolygon.get(j).y <= y) && (y < thePolygon.get(
                    i).y))) && (x < (thePolygon.get(j).x - thePolygon.get(i).x) * (y - thePolygon.get(i).y) / (thePolygon.get(
                    j).y - thePolygon.get(i).y) + thePolygon.get(i).x)) {
                c = (c + 1) % 2;
            }
        }
        return c == 1;
    }

    public static float[] toArray3f(final ArrayList<PVector> theData) {
        float[] myArray = new float[theData.size() * 3];
        for (int i = 0; i < theData.size(); i++) {
            final PVector v = theData.get(i);
            if (v != null) {
                myArray[i * 3 + 0] = v.x;
                myArray[i * 3 + 1] = v.y;
                myArray[i * 3 + 2] = v.z;
            }
        }
        return myArray;
    }

    public static void createNormals(float[] theVertices, float[] theNormals) {
        final int NUMBER_OF_VERTEX_COMPONENTS = 3;
        final int myNumberOfPoints = 3;
        for (int i = 0; i < theVertices.length; i += (myNumberOfPoints * NUMBER_OF_VERTEX_COMPONENTS)) {
            PVector a = new PVector(theVertices[i + 0], theVertices[i + 1], theVertices[i + 2]);
            PVector b = new PVector(theVertices[i + 3], theVertices[i + 4], theVertices[i + 5]);
            PVector c = new PVector(theVertices[i + 6], theVertices[i + 7], theVertices[i + 8]);
            PVector myNormal = new PVector();
            calculateNormal(a, b, c, myNormal);

            theNormals[i + 0] = myNormal.x;
            theNormals[i + 1] = myNormal.y;
            theNormals[i + 2] = myNormal.z;

            theNormals[i + 3] = myNormal.x;
            theNormals[i + 4] = myNormal.y;
            theNormals[i + 5] = myNormal.z;

            theNormals[i + 6] = myNormal.x;
            theNormals[i + 7] = myNormal.y;
            theNormals[i + 8] = myNormal.z;
        }
    }

    public static void calculateNormal(final PVector pointA,
                                       final PVector pointB,
                                       final PVector pointC,
                                       final PVector theResultNormal) {
        PVector TMP_BA = PVector.sub(pointB, pointA);
        PVector TMP_BC = PVector.sub(pointC, pointB);

        theResultNormal.cross(TMP_BA, TMP_BC);
        theResultNormal.normalize();
    }

    public static int[] toArray(final ArrayList<Integer> theData) {
        int[] myArray = new int[theData.size()];
        for (int i = 0; i < myArray.length; i++) {
            if (theData.get(i) != null) {
                myArray[i] = theData.get(i);
            }
        }
        return myArray;
    }

    public static Mesh mesh(ArrayList<PVector> pTriangles) {
        return mesh(pTriangles, false);
    }

    public static Mesh mesh(ArrayList<PVector> pTriangles, final boolean pCreateNormals) {
        final float[] mVertices = toArray3f(pTriangles);
        final float[] mNormals;
        if (pCreateNormals) {
            mNormals = new float[mVertices.length];
            createNormals(mVertices, mNormals);
        } else {
            mNormals = null;
        }
        return new Mesh(mVertices, 3, null, 4, null, 2, mNormals, PGraphics.TRIANGLES);
    }
}
