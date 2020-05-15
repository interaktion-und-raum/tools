package de.hfkbremen.mesh.booleanoperations;

import processing.core.PVector;

import java.util.ArrayList;

/**
 * Represents of a 3d face vertex.
 * <p>
 * D. H. Laidlaw, W. B. Trumbore, and J. F. Hughes.
 * "Constructive Solid Geometry for Polyhedral Objects"
 * SIGGRAPH Proceedings, 1986, p.161.
 *
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com)
 */
public class Vertex {

    public static final int UNKNOWN = 1;
    public static final int INSIDE = 2;
    public static final int OUTSIDE = 3;
    public static final int BOUNDARY = 4;
    private static final float TOL = 1e-5f;
    public float x;
    public float y;
    public float z;
    private ArrayList<Vertex> adjacentVertices;
    private int status;

    public Vertex(PVector position) {
        x = position.x;
        y = position.y;
        z = position.z;

        adjacentVertices = new ArrayList<>();
        status = UNKNOWN;
    }

    public Vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;

        adjacentVertices = new ArrayList<>();
        status = UNKNOWN;
    }

    public Vertex(PVector position, int status) {
        x = position.x;
        y = position.y;
        z = position.z;

        adjacentVertices = new ArrayList<>();
        this.status = status;
    }

    public Vertex(float x, float y, float z, int status) {
        this.x = x;
        this.y = y;
        this.z = z;

        adjacentVertices = new ArrayList<>();
        this.status = status;
    }

    public Vertex copy() {
        Vertex clone = new Vertex(x, y, z, status);
        for (int i = 0; i < adjacentVertices.size(); i++) {
            clone.adjacentVertices.add(adjacentVertices.get(i).copy());
        }
        return clone;
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
        if (!(anObject instanceof Vertex)) {
            return false;
        } else {
            Vertex vertex = (Vertex) anObject;
            return Math.abs(x - vertex.x) < TOL &&
                    Math.abs(y - vertex.y) < TOL &&
                    Math.abs(z - vertex.z) < TOL;
        }
    }

    public PVector getPosition() {
        return new PVector(x, y, z);
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

    public void setStatus(int status) {
        if (status >= UNKNOWN && status <= BOUNDARY) {
            this.status = status;
        }
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
        for (Vertex adjacentVert : adjacentVerts) {
            if (adjacentVert.getStatus() == Vertex.UNKNOWN) {
                adjacentVert.mark(status);
            }
        }
    }
}
