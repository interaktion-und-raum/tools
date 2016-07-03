package de.hfkbremen.mesh.booleanoperations;

import processing.core.PVector;

/**
 * Representation of a 3D face (triangle).
 * <p>
 * D. H. Laidlaw, W. B. Trumbore, and J. F. Hughes.
 * "Constructive Solid Geometry for Polyhedral Objects"
 * SIGGRAPH Proceedings, 1986, p.161.
 *
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com)
 */

public class Face {

    /**
     * face status if it is still unknown
     */
    public static final int UNKNOWN = 1;
    /**
     * face status if it is inside a create
     */
    public static final int INSIDE = 2;
    /**
     * face status if it is outside a create
     */
    public static final int OUTSIDE = 3;
    /**
     * face status if it is coincident with a create face
     */
    public static final int SAME = 4;
    /**
     * face status if it is coincident with a create face with opposite orientation
     */
    public static final int OPPOSITE = 5;
    /**
     * point status if it is up relative to an edge - see linePositionIn_ methods
     */
    private static final int UP = 6;
    /**
     * point status if it is down relative to an edge - see linePositionIn_ methods
     */
    private static final int DOWN = 7;
    /**
     * point status if it is on an edge - see linePositionIn_ methods
     */
    private static final int ON = 8;
    /**
     * point status if it isn't up, down or on relative to an edge - see linePositionIn_ methods
     */
    private static final int NONE = 9;
    private static final float TOL = 1e-8f;
    /**
     * first vertex
     */
    public Vertex v1;
    /**
     * second vertex
     */
    public Vertex v2;
    /**
     * third vertex
     */
    public Vertex v3;
    /**
     * face status relative to a create
     */
    private int status;

    //---------------------------------CONSTRUCTORS---------------------------------//

    /**
     * Constructs a face with unknown status.
     *
     * @param v1 a face vertex
     * @param v2 a face vertex
     * @param v3 a face vertex
     */
    public Face(Vertex v1, Vertex v2, Vertex v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;

        status = UNKNOWN;
    }

    //-----------------------------------OVERRIDES----------------------------------//

    public Face copy() {
        Face clone = new Face(v1.copy(), v2.copy(), v3.copy());
        clone.status = status;
        return clone;
    }

    /**
     * Makes a string definition for the Face object
     *
     * @return the string definition
     */
    public String toString() {
        return v1.toString() + "\n" + v2.toString() + "\n" + v3.toString();
    }

    /**
     * Checks if a face is equal to another. To be equal, they have to have equal
     * vertices in the same order
     *
     * @param anObject the other face to be tested
     * @return true if they are equal, false otherwise.
     */
    public boolean equals(Object anObject) {
        if (!(anObject instanceof Face)) {
            return false;
        } else {
            Face face = (Face) anObject;
            boolean cond1 = v1.equals(face.v1) && v2.equals(face.v2) && v3.equals(face.v3);
            boolean cond2 = v1.equals(face.v2) && v2.equals(face.v3) && v3.equals(face.v1);
            boolean cond3 = v1.equals(face.v3) && v2.equals(face.v1) && v3.equals(face.v2);

            return cond1 || cond2 || cond3;
        }
    }

    //-------------------------------------GETS-------------------------------------//

    /**
     * Gets the face bound
     *
     * @return face bound
     */
    public Bound getBound() {
        return new Bound(v1.getPosition(), v2.getPosition(), v3.getPosition());
    }

    /**
     * Gets the face normal
     *
     * @return face normal
     */
    public PVector getNormal() {
        PVector p1 = v1.getPosition();
        PVector p2 = v2.getPosition();
        PVector p3 = v3.getPosition();
        PVector xy, xz, normal;

        xy = new PVector(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
        xz = new PVector(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);

        normal = new PVector();
        PVector.cross(xy, xz, normal);
        normal.normalize();

        return normal;
    }

    /**
     * Gets the face status
     *
     * @return face status - UNKNOWN, INSIDE, OUTSIDE, SAME OR OPPOSITE
     */
    public int getStatus() {
        return status;
    }

    /**
     * Gets the face area
     *
     * @return face area
     */
    public float getArea() {
        //area = (a * c * sen(B))/2
        PVector p1 = v1.getPosition();
        PVector p2 = v2.getPosition();
        PVector p3 = v3.getPosition();
        PVector xy = new PVector(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
        PVector xz = new PVector(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);

        float a = PVector.dist(p1, p2);
        float c = PVector.dist(p1, p3);
        float b = PVector.angleBetween(xy, xz);

        return (a * c * (float) Math.sin(b)) / 2f;
    }

    //-------------------------------------OTHERS-----------------------------------//

    /**
     * Invert face direction (normal direction)
     */
    public void invert() {
        Vertex vertexTemp = v2;
        v2 = v1;
        v1 = vertexTemp;
    }

    //------------------------------------CLASSIFIERS-------------------------------//

    /**
     * Classifies the face if one of its vertices are classified as INSIDE or OUTSIDE
     *
     * @return true if the face could be classified, false otherwise
     */
    public boolean simpleClassify() {
        int status1 = v1.getStatus();
        int status2 = v2.getStatus();
        int status3 = v3.getStatus();

        if (status1 == Vertex.INSIDE || status1 == Vertex.OUTSIDE) {
            this.status = status1;
            return true;
        } else if (status2 == Vertex.INSIDE || status2 == Vertex.OUTSIDE) {
            this.status = status2;
            return true;
        } else if (status3 == Vertex.INSIDE || status3 == Vertex.OUTSIDE) {
            this.status = status3;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Classifies the face based on the ray trace technique
     *
     * @param object object3d used to compute the face status
     */
    public void rayTraceClassify(Object3D object) {
        //creating a ray starting starting at the face baricenter going to the normal direction
        PVector p0 = new PVector();
        p0.x = (v1.x + v2.x + v3.x) / 3f;
        p0.y = (v1.y + v2.y + v3.y) / 3f;
        p0.z = (v1.z + v2.z + v3.z) / 3f;
        Line ray = new Line(getNormal(), p0);

        boolean success;
        float dotProduct, distance;
        PVector intersectionPoint;
        Face closestFace = null;
        float closestDistance;

        do {
            success = true;
            closestDistance = Float.MAX_VALUE;
            //for each face from the other create...
            for (int i = 0; i < object.getNumFaces(); i++) {
                Face face = object.getFace(i);
                dotProduct = face.getNormal().dot(ray.getDirection());
                intersectionPoint = ray.computePlaneIntersection(face.getNormal(), face.v1.getPosition());

                //if ray intersects the plane...
                if (intersectionPoint != null) {
                    distance = ray.computePointToPointDistance(intersectionPoint);

                    //if ray lies in plane...
                    if (Math.abs(distance) < TOL && Math.abs(dotProduct) < TOL) {
                        //disturb the ray in order to not lie into another plane
                        ray.perturbDirection();
                        success = false;
                        break;
                    }

                    //if ray starts in plane...
                    if (Math.abs(distance) < TOL && Math.abs(dotProduct) > TOL) {
                        //if ray intersects the face...
                        if (face.hasPoint(intersectionPoint)) {
                            //faces coincide
                            closestFace = face;
                            closestDistance = 0;
                            break;
                        }
                    }

                    //if ray intersects plane...
                    else if (Math.abs(dotProduct) > TOL && distance > TOL) {
                        if (distance < closestDistance) {
                            //if ray intersects the face;
                            if (face.hasPoint(intersectionPoint)) {
                                //this face is the closest face untill now
                                closestDistance = distance;
                                closestFace = face;
                            }
                        }
                    }
                }
            }
        } while (!success);

        //none face found: outside face
        if (closestFace == null) {
            status = OUTSIDE;
        }
        //face found: test dot product
        else {
            dotProduct = closestFace.getNormal().dot(ray.getDirection());

            //distance = 0: coplanar faces
            if (Math.abs(closestDistance) < TOL) {
                if (dotProduct > TOL) {
                    status = SAME;
                } else if (dotProduct < -TOL) {
                    status = OPPOSITE;
                }
            }

            //dot product > 0 (same direction): inside face
            else if (dotProduct > TOL) {
                status = INSIDE;
            }

            //dot product < 0 (opposite direction): outside face
            else if (dotProduct < -TOL) {
                status = OUTSIDE;
            }
        }
    }

    //------------------------------------PRIVATES----------------------------------//

    /**
     * Checks if the the face contains a point
     *
     * @param point
     * @return
     */
    private boolean hasPoint(PVector point) {
        int result1, result2, result3;
        boolean hasUp, hasDown, hasOn;
        PVector normal = getNormal();

        //if x is constant...
        if (Math.abs(normal.x) > TOL) {
            //tests on the x plane
            result1 = linePositionInX(point, v1.getPosition(), v2.getPosition());
            result2 = linePositionInX(point, v2.getPosition(), v3.getPosition());
            result3 = linePositionInX(point, v3.getPosition(), v1.getPosition());
        }

        //if y is constant...
        else if (Math.abs(normal.y) > TOL) {
            //tests on the y plane
            result1 = linePositionInY(point, v1.getPosition(), v2.getPosition());
            result2 = linePositionInY(point, v2.getPosition(), v3.getPosition());
            result3 = linePositionInY(point, v3.getPosition(), v1.getPosition());
        } else {
            //tests on the z plane
            result1 = linePositionInZ(point, v1.getPosition(), v2.getPosition());
            result2 = linePositionInZ(point, v2.getPosition(), v3.getPosition());
            result3 = linePositionInZ(point, v3.getPosition(), v1.getPosition());
        }

        //if the point is up and down two lines...
        //if the point is on of the lines...
        return ((result1 == UP) || (result2 == UP) || (result3 == UP)) && ((result1 == DOWN) || (result2 == DOWN) || (result3 == DOWN)) || (result1 == ON) || (result2 == ON) || (result3 == ON);
    }

    /**
     * Gets the position of a point relative to a line in the x plane
     *
     * @param point      point to be tested
     * @param pointLine1 one of the line ends
     * @param pointLine2 one of the line ends
     * @return position of the point relative to the line - UP, DOWN, ON, NONE
     */
    private static int linePositionInX(PVector point, PVector pointLine1, PVector pointLine2) {
        float a, b, z;
        if ((Math.abs(pointLine1.y - pointLine2.y) > TOL) && (((point.y >= pointLine1.y) && (point.y <= pointLine2.y)) || ((point.y <= pointLine1.y) && (point.y >= pointLine2.y)))) {
            a = (pointLine2.z - pointLine1.z) / (pointLine2.y - pointLine1.y);
            b = pointLine1.z - a * pointLine1.y;
            z = a * point.y + b;
            if (z > point.z + TOL) {
                return UP;
            } else if (z < point.z - TOL) {
                return DOWN;
            } else {
                return ON;
            }
        } else {
            return NONE;
        }
    }

    /**
     * Gets the position of a point relative to a line in the y plane
     *
     * @param point      point to be tested
     * @param pointLine1 one of the line ends
     * @param pointLine2 one of the line ends
     * @return position of the point relative to the line - UP, DOWN, ON, NONE
     */

    private static int linePositionInY(PVector point, PVector pointLine1, PVector pointLine2) {
        float a, b, z;
        if ((Math.abs(pointLine1.x - pointLine2.x) > TOL) && (((point.x >= pointLine1.x) && (point.x <= pointLine2.x)) || ((point.x <= pointLine1.x) && (point.x >= pointLine2.x)))) {
            a = (pointLine2.z - pointLine1.z) / (pointLine2.x - pointLine1.x);
            b = pointLine1.z - a * pointLine1.x;
            z = a * point.x + b;
            if (z > point.z + TOL) {
                return UP;
            } else if (z < point.z - TOL) {
                return DOWN;
            } else {
                return ON;
            }
        } else {
            return NONE;
        }
    }

    /**
     * Gets the position of a point relative to a line in the z plane
     *
     * @param point      point to be tested
     * @param pointLine1 one of the line ends
     * @param pointLine2 one of the line ends
     * @return position of the point relative to the line - UP, DOWN, ON, NONE
     */

    private static int linePositionInZ(PVector point, PVector pointLine1, PVector pointLine2) {
        float a, b, y;
        if ((Math.abs(pointLine1.x - pointLine2.x) > TOL) && (((point.x >= pointLine1.x) && (point.x <= pointLine2.x)) || ((point.x <= pointLine1.x) && (point.x >= pointLine2.x)))) {
            a = (pointLine2.y - pointLine1.y) / (pointLine2.x - pointLine1.x);
            b = pointLine1.y - a * pointLine1.x;
            y = a * point.x + b;
            if (y > point.y + TOL) {
                return UP;
            } else if (y < point.y - TOL) {
                return DOWN;
            } else {
                return ON;
            }
        } else {
            return NONE;
        }
    }
}
