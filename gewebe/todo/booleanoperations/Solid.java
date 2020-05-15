package de.hfkbremen.mesh.booleanoperations;

import de.hfkbremen.mesh.Mesh;
import processing.core.PGraphics;
import processing.core.PVector;

/**
 * Class representing a 3D create.
 *
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com)
 */

public class Solid {

    protected int[] indices;

    protected PVector[] vertices;

    public Solid(PVector[] theVertices, int[] theIndices) {
        vertices = theVertices;
        indices = theIndices;
    }

    public PVector[] getVertices() {
        PVector[] newVertices = new PVector[vertices.length];
        for (int i = 0; i < newVertices.length; i++) {
            newVertices[i] = new PVector().set(vertices[i]);
        }
        return newVertices;
    }

    public int[] getIndices() {
        int[] newIndices = new int[indices.length];
        System.arraycopy(indices, 0, newIndices, 0, indices.length);
        return newIndices;
    }

    public boolean isEmpty() {
        return indices.length == 0;
    }

    public void translate(float dx, float dy, float dz) {
        for (PVector vertice : vertices) {
            vertice.x += dx;
            vertice.y += dy;
            vertice.z += dz;
        }
    }

    public void draw(PGraphics g) {
        g.beginShape(PGraphics.TRIANGLES);
        for (int i = 0; i < getIndices().length; i++) {
            final PVector v = getVertices()[getIndices()[i]];
            g.vertex(v.x, v.y, v.z);
        }
        g.endShape();
    }

    public static Solid create(Mesh pMesh) {
        return create(pMesh.vertices());
    }

    public static Solid create(float[] pMeshData) {
        PVector[] mVertices = new PVector[pMeshData.length / 3];
        for (int i = 0; i < pMeshData.length; i += 3) {
            mVertices[i / 3] = new PVector(pMeshData[i + 0], pMeshData[i + 1], pMeshData[i + 2]);
        }
        return create(mVertices);
    }

    public static Solid create(PVector[] pVertices) {
        int[] mIndices = new int[pVertices.length];
        for (int i = 0; i < mIndices.length; i++) {
            mIndices[i] = i;
        }
        return new Solid(pVertices, mIndices);
    }
}
