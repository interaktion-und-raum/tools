package claylike.booleanoperations;


import mathematik.Vector3f;


/**
 * Class representing a 3D solid.
 *
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com)
 */

public class Solid {

    protected int[] indices;

    protected Vector3f[] vertices;

    public Solid(Vector3f[] theVertices, int[] theIndices) {
        vertices = theVertices;
        indices = theIndices;
    }


    public Vector3f[] getVertices() {
        Vector3f[] newVertices = new Vector3f[vertices.length];
        for (int i = 0; i < newVertices.length; i++) {
            newVertices[i] = (Vector3f) vertices[i].clone();
        }
        return newVertices;
    }


    public int[] getIndices() {
        int[] newIndices = new int[indices.length];
        System.arraycopy(indices, 0, newIndices, 0, indices.length);
        return newIndices;
    }


    public boolean isEmpty() {
        if (indices.length == 0) {
            return true;
        } else {
            return false;
        }
    }


    public void translate(float dx, float dy, float dz) {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i].x += dx;
            vertices[i].y += dy;
            vertices[i].z += dz;
        }
    }
}
