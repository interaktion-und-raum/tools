package de.hfkbremen.mesh.booleanoperations;

import processing.core.PVector;

/**
 * Representation of a 3d line or a ray (represented by a direction and a point).
 * <p>
 * D. H. Laidlaw, W. B. Trumbore, and J. F. Hughes.
 * "Constructive Solid Geometry for Polyhedral Objects"
 * SIGGRAPH Proceedings, 1986, p.161.
 *
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com)
 */

public class Line {

    private static final float TOL = 1e-8f;
    private PVector point;
    private PVector direction;

    public Line(Face face1, Face face2) {
        PVector normalFace1 = face1.getNormal();
        PVector normalFace2 = face2.getNormal();

        //direction: cross product of the faces normals
        direction = new PVector();
        PVector.cross(normalFace1, normalFace2, direction);

        //if direction lenght is not zero (the planes aren't parallel )...
        if (!(direction.mag() < TOL)) {
            //getting a line point, zero is set to a coordinate whose direction
            //component isn't zero (line intersecting its origin plan)
            point = new PVector();
            float d1 = -(normalFace1.x * face1.v1.x + normalFace1.y * face1.v1.y + normalFace1.z * face1.v1.z);
            float d2 = -(normalFace2.x * face2.v1.x + normalFace2.y * face2.v1.y + normalFace2.z * face2.v1.z);
            if (Math.abs(direction.x) > TOL) {
                point.x = 0;
                point.y = (d2 * normalFace1.z - d1 * normalFace2.z) / direction.x;
                point.z = (d1 * normalFace2.y - d2 * normalFace1.y) / direction.x;
            } else if (Math.abs(direction.y) > TOL) {
                point.x = (d1 * normalFace2.z - d2 * normalFace1.z) / direction.y;
                point.y = 0;
                point.z = (d2 * normalFace1.x - d1 * normalFace2.x) / direction.y;
            } else {
                point.x = (d2 * normalFace1.y - d1 * normalFace2.y) / direction.z;
                point.y = (d1 * normalFace2.x - d2 * normalFace1.x) / direction.z;
                point.z = 0;
            }
        }

        direction.normalize();
    }

    public Line(PVector direction, PVector point) {
        this.direction = new PVector().set(direction);
        this.point = new PVector().set(point);
        direction.normalize();
    }

    public Line copy() {
        return new Line(direction, point);
    }

    public String toString() {
        return "Direction: " + direction.toString() + "\nPoint: " + point.toString();
    }

    public PVector getPoint() {
        return new PVector().set(point);
    }

    public void setPoint(PVector pPoint) {
        this.point.set(pPoint);
    }

    public PVector getDirection() {
        return new PVector().set(direction);
    }

    public void setDirection(PVector pDirection) {
        this.direction.set(pDirection);
    }

    public float computePointToPointDistance(PVector otherPoint) {
        float distance = PVector.dist(otherPoint, point);
        PVector vec = new PVector(otherPoint.x - point.x, otherPoint.y - point.y, otherPoint.z - point.z);
        vec.normalize();
        if (vec.dot(direction) < 0) {
            return -distance;
        } else {
            return distance;
        }
    }

    public PVector computeLineIntersection(Line otherLine) {
        PVector linePoint = otherLine.getPoint();
        PVector lineDirection = otherLine.getDirection();

        final float t;
        if (Math.abs(direction.y * lineDirection.x - direction.x * lineDirection.y) > TOL) {
            t = (-point.y * lineDirection.x + linePoint.y * lineDirection.x + lineDirection.y * point.x - lineDirection.y * linePoint.x) / (direction.y * lineDirection.x - direction.x * lineDirection.y);
        } else if (Math.abs(-direction.x * lineDirection.z + direction.z * lineDirection.x) > TOL) {
            t = -(-lineDirection.z * point.x + lineDirection.z * linePoint.x + lineDirection.x * point.z - lineDirection.x * linePoint.z) / (-direction.x * lineDirection.z + direction.z * lineDirection.x);
        } else if (Math.abs(-direction.z * lineDirection.y + direction.y * lineDirection.z) > TOL) {
            t = (point.z * lineDirection.y - linePoint.z * lineDirection.y - lineDirection.z * point.y + lineDirection.z * linePoint.y) / (-direction.z * lineDirection.y + direction.y * lineDirection.z);
        } else {
            return null;
        }

        float x = point.x + direction.x * t;
        float y = point.y + direction.y * t;
        float z = point.z + direction.z * t;

        return new PVector(x, y, z);
    }

    public PVector computePlaneIntersection(PVector normal, PVector planePoint) {
        float A = normal.x;
        float B = normal.y;
        float C = normal.z;
        float D = -(normal.x * planePoint.x + normal.y * planePoint.y + normal.z * planePoint.z);

        float numerator = A * point.x + B * point.y + C * point.z + D;
        float denominator = A * direction.x + B * direction.y + C * direction.z;

        //if line is paralel to the plane...
        if (Math.abs(denominator) < TOL) {
            //if line is contained in the plane...
            if (Math.abs(numerator) < TOL) {
                return new PVector().set(point);
            } else {
                return null;
            }
        }
        //if line intercepts the plane...
        else {
            float t = -numerator / denominator;
            PVector resultPoint = new PVector();
            resultPoint.x = point.x + t * direction.x;
            resultPoint.y = point.y + t * direction.y;
            resultPoint.z = point.z + t * direction.z;

            return resultPoint;
        }
    }

    public void perturbDirection() {
        direction.x += 1e-5 * Math.random();
        direction.y += 1e-5 * Math.random();
        direction.z += 1e-5 * Math.random();
    }
}
