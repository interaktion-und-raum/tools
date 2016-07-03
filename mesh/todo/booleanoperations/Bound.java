package de.hfkbremen.mesh.booleanoperations;

import processing.core.PVector;

/**
 * Representation of a bound - the extremes of a 3d component for each coordinate.
 * <p>
 * D. H. Laidlaw, W. B. Trumbore, and J. F. Hughes.
 * "Constructive Solid Geometry for Polyhedral Objects"
 * SIGGRAPH Proceedings, 1986, p.161.
 *
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com)
 */

public class Bound {

    private static final float TOL = 1e-8f;
    private float xMax;
    private float xMin;
    private float yMax;
    private float yMin;
    private float zMax;
    private float zMin;

    public Bound(PVector p1, PVector p2, PVector p3) {
        xMax = xMin = p1.x;
        yMax = yMin = p1.y;
        zMax = zMin = p1.z;

        checkVertex(p2);
        checkVertex(p3);
    }

    public Bound(PVector[] vertices) {
        xMax = xMin = vertices[0].x;
        yMax = yMin = vertices[0].y;
        zMax = zMin = vertices[0].z;

        for (int i = 1; i < vertices.length; i++) {
            checkVertex(vertices[i]);
        }
    }

    public String toString() {
        return "x: " + xMin + " .. " + xMax + "\ny: " + yMin + " .. " + yMax + "\nz: " + zMin + " .. " + zMax;
    }

    public boolean overlap(Bound bound) {
        return !((xMin > bound.xMax + TOL) || (xMax < bound.xMin - TOL) || (yMin > bound.yMax + TOL) ||
                (yMax < bound.yMin - TOL) || (zMin > bound.zMax + TOL) || (zMax < bound.zMin - TOL));
    }

    private void checkVertex(PVector vertex) {
        if (vertex.x > xMax) {
            xMax = vertex.x;
        } else if (vertex.x < xMin) {
            xMin = vertex.x;
        }

        if (vertex.y > yMax) {
            yMax = vertex.y;
        } else if (vertex.y < yMin) {
            yMin = vertex.y;
        }

        if (vertex.z > zMax) {
            zMax = vertex.z;
        } else if (vertex.z < zMin) {
            zMin = vertex.z;
        }
    }
}
