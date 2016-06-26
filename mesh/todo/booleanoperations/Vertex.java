package claylike.booleanoperations;


import java.util.ArrayList;
import mathematik.Vector3f;


/**
 * Represents of a 3d face vertex.
 *
 * D. H. Laidlaw, W. B. Trumbore, and J. F. Hughes.
 * "Constructive Solid Geometry for Polyhedral Objects"
 * SIGGRAPH Proceedings, 1986, p.161.
 *
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com)
 */
public class Vertex
    implements Cloneable {

    public float x;

    public float y;

    public float z;

    private ArrayList<Vertex> adjacentVertices;

    private int status;

    private static final float TOL = 1e-5f;

    public static final int UNKNOWN = 1;

    public static final int INSIDE = 2;

    public static final int OUTSIDE = 3;

    public static final int BOUNDARY = 4;

    public Vertex(Vector3f position) {
        x = position.x;
        y = position.y;
        z = position.z;

        adjacentVertices = new ArrayList<Vertex> ();
        status = UNKNOWN;
    }


    public Vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;

        adjacentVertices = new ArrayList<Vertex> ();
        status = UNKNOWN;
    }


    public Vertex(Vector3f position, int status) {
        x = position.x;
        y = position.y;
        z = position.z;

        adjacentVertices = new ArrayList<Vertex> ();
        this.status = status;
    }


    public Vertex(float x, float y, float z, int status) {
        this.x = x;
        this.y = y;
        this.z = z;

        adjacentVertices = new ArrayList<Vertex> ();
        this.status = status;
    }


    public Object clone() {
        try {
            Vertex clone = (Vertex)super.clone();
            clone.x = x;
            clone.y = y;
            clone.z = z;

            clone.status = status;
            clone.adjacentVertices = new ArrayList<Vertex> ();
            for (int i = 0; i < adjacentVertices.size(); i++) {
                clone.adjacentVertices.add( (Vertex) (adjacentVertices.get(i).clone()));
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }


    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }


    /**
     * Checks if an vertex is equal to another. To be equal, they have to have the same
     * coordinates(with some tolerance) and color
     *
     * @param anObject the other vertex to be tested
     * @return true if they are equal, false otherwise.
     */
    public boolean equals(Object anObject) {
        if (! (anObject instanceof Vertex)) {
            return false;
        } else {
            Vertex vertex = (Vertex) anObject;
            return Math.abs(x - vertex.x) < TOL &&
                Math.abs(y - vertex.y) < TOL &&
                Math.abs(z - vertex.z) < TOL;
        }
    }


    public void setStatus(int status) {
        if (status >= UNKNOWN && status <= BOUNDARY) {
            this.status = status;
        }
    }


    public Vector3f getPosition() {
        return new Vector3f(x, y, z);
    }


    public Vertex[] getAdjacentVertices() {
        Vertex[] vertices = new Vertex[adjacentVertices.size()];
        for (int i = 0; i < adjacentVertices.size(); i++) {
            vertices[i] = adjacentVertices.get(i);
        }
        return vertices;
    }


    public int getStatus() {
        return status;
    }


    public void addAdjacentVertex(Vertex adjacentVertex) {
        if (!adjacentVertices.contains(adjacentVertex)) {
            adjacentVertices.add(adjacentVertex);
        }
    }


    public void mark(int status) {
        //mark vertex
        this.status = status;

        //mark adjacent vertices
        Vertex[] adjacentVerts = getAdjacentVertices();
        for (int i = 0; i < adjacentVerts.length; i++) {
            if (adjacentVerts[i].getStatus() == Vertex.UNKNOWN) {
                adjacentVerts[i].mark(status);
            }
        }
    }
}
