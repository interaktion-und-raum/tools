package de.hfkbremen.mesh.booleanoperations;

import processing.core.PVector;

import java.util.ArrayList;

/**
 * Data structure about a 3d create to apply boolean operations in it.
 * <p>
 * <br><br>Tipically, two 'Object3d' objects are created to apply boolean operation. The
 * methods splitFaces() and classifyFaces() are called in this sequence for both objects,
 * always using the other one as parameter. Then the faces from both objects are collected
 * according their status.
 * <p>
 * D. H. Laidlaw, W. B. Trumbore, and J. F. Hughes.
 * "Constructive Solid Geometry for Polyhedral Objects"
 * SIGGRAPH Proceedings, 1986, p.161.
 *
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com)
 */
public class Object3D {

    private static final float TOL = 1e-8f;
    private ArrayList<Vertex> vertices;
    private ArrayList<Face> faces;
    private Bound bound;

    public Object3D(Solid solid) {
        Vertex v1, v2, v3, vertex;
        PVector[] verticesPoints = solid.getVertices();
        int[] indices = solid.getIndices();
        ArrayList<Vertex> verticesTemp = new ArrayList<>();

        //create vertices
        vertices = new ArrayList<>();
        for (PVector verticesPoint : verticesPoints) {
            vertex = addVertex(verticesPoint, Vertex.UNKNOWN);
            verticesTemp.add(vertex);
        }

        //create faces
        faces = new ArrayList<>();
        for (int i = 0; i < indices.length; i = i + 3) {
            v1 = verticesTemp.get(indices[i]);
            v2 = verticesTemp.get(indices[i + 1]);
            v3 = verticesTemp.get(indices[i + 2]);
            addFace(v1, v2, v3);
        }

        //create bound
        bound = new Bound(verticesPoints);
    }

    private Object3D(ArrayList<Vertex> vertices, ArrayList<Face> faces, Bound bound) {
        this.vertices = vertices;
        this.faces = faces;
        this.bound = bound;
    }

    public Object3D copy() {
        ArrayList<Vertex> mVertices = new ArrayList<>();
        for (int i = 0; i < mVertices.size(); i++) {
            mVertices.add(mVertices.get(i).copy());
        }
        ArrayList<Face> mFaces = new ArrayList<>();
        for (int i = 0; i < mVertices.size(); i++) {
            mFaces.add(mFaces.get(i).copy());
        }
        return new Object3D(mVertices, mFaces, bound);
    }

    public int getNumFaces() {
        return faces.size();
    }

    /**
     * Gets a face reference for a given position
     *
     * @param index required face position
     * @return face reference , null if the position is invalid
     */
    public Face getFace(int index) {
        if (index < 0 || index >= faces.size()) {
            return null;
        } else {
            return faces.get(index);
        }
    }

    public Bound getBound() {
        return bound;
    }

    private Face addFace(Vertex v1, Vertex v2, Vertex v3) {
        if (!(v1.equals(v2) || v1.equals(v3) || v2.equals(v3))) {
            Face face = new Face(v1, v2, v3);
            if (face.getArea() > TOL) {
                faces.add(face);
                return face;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private Vertex addVertex(PVector pos, int status) {
        int i;
        //if already there is an equal vertex, it is not inserted
        Vertex vertex = new Vertex(pos, status);
        for (i = 0; i < vertices.size(); i++) {
            if (vertex.equals(vertices.get(i))) {
                break;
            }
        }
        if (i == vertices.size()) {
            vertices.add(vertex);
            return vertex;
        } else {
            vertex = vertices.get(i);
            vertex.setStatus(status);
            return vertex;
        }

    }

    public void splitFaces(Object3D object) {
        Line line;
        Face face1, face2;
        Segment segments[], segment1, segment2;
        float distFace1Vert1, distFace1Vert2, distFace1Vert3, distFace2Vert1, distFace2Vert2, distFace2Vert3;
        int signFace1Vert1, signFace1Vert2, signFace1Vert3, signFace2Vert1, signFace2Vert2, signFace2Vert3;
        int numFacesBefore = getNumFaces();
        int numFacesStart = getNumFaces();
        int facesIgnored = 0;

        //if the objects bounds overlap...
        if (getBound().overlap(object.getBound())) {
            //for each object1 face...
            for (int i = 0; i < getNumFaces(); i++) {
                //if object1 face bound and object2 bound overlap ...
                face1 = getFace(i);

                if (face1.getBound().overlap(object.getBound())) {
                    //for each object2 face...
                    for (int j = 0; j < object.getNumFaces(); j++) {
                        //if object1 face bound and object2 face bound overlap...
                        face2 = object.getFace(j);
                        if (face1.getBound().overlap(face2.getBound())) {
                            //PART I - DO TWO POLIGONS INTERSECT?
                            //POSSIBLE RESULTS: INTERSECT, NOT_INTERSECT, COPLANAR

                            //distance from the face1 vertices to the face2 plane
                            distFace1Vert1 = computeDistance(face1.v1, face2);
                            distFace1Vert2 = computeDistance(face1.v2, face2);
                            distFace1Vert3 = computeDistance(face1.v3, face2);

                            //distances signs from the face1 vertices to the face2 plane
                            signFace1Vert1 = (distFace1Vert1 > TOL ? 1 : (distFace1Vert1 < -TOL ? -1 : 0));
                            signFace1Vert2 = (distFace1Vert2 > TOL ? 1 : (distFace1Vert2 < -TOL ? -1 : 0));
                            signFace1Vert3 = (distFace1Vert3 > TOL ? 1 : (distFace1Vert3 < -TOL ? -1 : 0));

                            //if all the signs are zero, the planes are coplanar
                            //if all the signs are positive or negative, the planes do not intersect
                            //if the signs are not equal...
                            if (!(signFace1Vert1 == signFace1Vert2 && signFace1Vert2 == signFace1Vert3)) {
                                //distance from the face2 vertices to the face1 plane
                                distFace2Vert1 = computeDistance(face2.v1, face1);
                                distFace2Vert2 = computeDistance(face2.v2, face1);
                                distFace2Vert3 = computeDistance(face2.v3, face1);

                                //distances signs from the face2 vertices to the face1 plane
                                signFace2Vert1 = (distFace2Vert1 > TOL ? 1 : (distFace2Vert1 < -TOL ? -1 : 0));
                                signFace2Vert2 = (distFace2Vert2 > TOL ? 1 : (distFace2Vert2 < -TOL ? -1 : 0));
                                signFace2Vert3 = (distFace2Vert3 > TOL ? 1 : (distFace2Vert3 < -TOL ? -1 : 0));

                                //if the signs are not equal...
                                if (!(signFace2Vert1 == signFace2Vert2 && signFace2Vert2 == signFace2Vert3)) {
                                    line = new Line(face1, face2);

                                    //intersection of the face1 and the plane of face2
                                    segment1 = new Segment(line, face1, signFace1Vert1, signFace1Vert2, signFace1Vert3);

                                    //intersection of the face2 and the plane of face1
                                    segment2 = new Segment(line, face2, signFace2Vert1, signFace2Vert2, signFace2Vert3);

                                    //if the two segments intersect...
                                    if (segment1.intersect(segment2)) {
                                        //PART II - SUBDIVIDING NON-COPLANAR POLYGONS
                                        int lastNumFaces = getNumFaces();
                                        this.splitFace(i, segment1, segment2);

                                        //prevent from infinite loop (with a loss of faces...)
                                        if(numFacesStart*21<getNumFaces())
                                        {
                                        	System.out.println("possible infinite loop situation: terminating faces split");
                                        	return;
                                        }

                                        //if the face in the position isn't the same, there was a break
                                        if (face1 != getFace(i)) {
                                            //if the generated create is equal the origin...
                                            if (face1.equals(getFace(getNumFaces() - 1))) {
                                                //return it to its position and jump it
                                                if (i != (getNumFaces() - 1)) {
                                                    faces.remove(getNumFaces() - 1);
                                                    faces.add(i, face1);
                                                } else {
                                                    continue;
                                                }
                                            }
                                            //else: test next face
                                            else {
                                                i--;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //        System.out.println("END OF SPLIT VERTICES");
    }

    private float computeDistance(Vertex vertex, Face face) {
        PVector normal = face.getNormal();
        float a = normal.x;
        float b = normal.y;
        float c = normal.z;
        float d = -(a * face.v1.x + b * face.v1.y + c * face.v1.z);
        return a * vertex.x + b * vertex.y + c * vertex.z + d;
    }

    private void splitFace(int facePos, Segment segment1, Segment segment2) {
        Vertex startPosVertex, endPosVertex;
        PVector startPos, endPos;
        int startType, endType, middleType;
        float startDist, endDist;

        Face face = getFace(facePos);
        Vertex startVertex = segment1.getStartVertex();
        Vertex endVertex = segment1.getEndVertex();

        //starting point: deeper starting point
        if (segment2.getStartDistance() > segment1.getStartDistance() + TOL) {
            startDist = segment2.getStartDistance();
            startType = segment1.getIntermediateType();
            startPos = segment2.getStartPosition();
        } else {
            startDist = segment1.getStartDistance();
            startType = segment1.getStartType();
            startPos = segment1.getStartPosition();
        }

        //ending point: deepest ending point
        if (segment2.getEndDistance() < segment1.getEndDistance() - TOL) {
            endDist = segment2.getEndDistance();
            endType = segment1.getIntermediateType();
            endPos = segment2.getEndPosition();
        } else {
            endDist = segment1.getEndDistance();
            endType = segment1.getEndType();
            endPos = segment1.getEndPosition();
        }
        middleType = segment1.getIntermediateType();

        //set vertex to BOUNDARY if it is start type
        if (startType == Segment.VERTEX) {
            startVertex.setStatus(Vertex.BOUNDARY);
        }

        //set vertex to BOUNDARY if it is end type
        if (endType == Segment.VERTEX) {
            endVertex.setStatus(Vertex.BOUNDARY);
        }

        //VERTEX-_______-VERTEX
        if (startType == Segment.VERTEX && endType == Segment.VERTEX) {
            return;
        }

        //______-EDGE-______
        else if (middleType == Segment.EDGE) {
            //gets the edge
            int splitEdge;
            if ((startVertex == face.v1 && endVertex == face.v2) || (startVertex == face.v2 && endVertex == face.v1)) {
                splitEdge = 1;
            } else if ((startVertex == face.v2 && endVertex == face.v3) || (startVertex == face.v3 && endVertex == face.v2)) {
                splitEdge = 2;
            } else {
                splitEdge = 3;
            }

            //VERTEX-EDGE-EDGE
            if (startType == Segment.VERTEX) {
                breakFaceInTwo(facePos, endPos, splitEdge);
                return;
            }

            //EDGE-EDGE-VERTEX
            else if (endType == Segment.VERTEX) {
                breakFaceInTwo(facePos, startPos, splitEdge);
                return;
            }

            // EDGE-EDGE-EDGE
            else if (startDist == endDist) {
                breakFaceInTwo(facePos, endPos, splitEdge);
            } else {
                if ((startVertex == face.v1 && endVertex == face.v2) ||
                        (startVertex == face.v2 && endVertex == face.v3) || (startVertex == face.v3 && endVertex == face.v1)) {
                    breakFaceInThree(facePos, startPos, endPos, splitEdge);
                } else {
                    breakFaceInThree(facePos, endPos, startPos, splitEdge);
                }
            }
            return;
        }

        //______-FACE-______

        //VERTEX-FACE-EDGE
        else if (startType == Segment.VERTEX && endType == Segment.EDGE) {
            breakFaceInTwo(facePos, endPos, endVertex);
        }
        //EDGE-FACE-VERTEX
        else if (startType == Segment.EDGE && endType == Segment.VERTEX) {
            breakFaceInTwo(facePos, startPos, startVertex);
        }
        //VERTEX-FACE-FACE
        else if (startType == Segment.VERTEX && endType == Segment.FACE) {
            breakFaceInThree(facePos, endPos, startVertex);
        }
        //FACE-FACE-VERTEX
        else if (startType == Segment.FACE && endType == Segment.VERTEX) {
            breakFaceInThree(facePos, startPos, endVertex);
        }
        //EDGE-FACE-EDGE
        else if (startType == Segment.EDGE && endType == Segment.EDGE) {
            breakFaceInThree(facePos, startPos, endPos, startVertex, endVertex);
        }
        //EDGE-FACE-FACE
        else if (startType == Segment.EDGE && endType == Segment.FACE) {
            breakFaceInFour(facePos, startPos, endPos, startVertex);
        }
        //FACE-FACE-EDGE
        else if (startType == Segment.FACE && endType == Segment.EDGE) {
            breakFaceInFour(facePos, endPos, startPos, endVertex);
        }
        //FACE-FACE-FACE
        else if (startType == Segment.FACE && endType == Segment.FACE) {
            PVector segmentVector = new PVector(startPos.x - endPos.x, startPos.y - endPos.y, startPos.z - endPos.z);

            //if the intersection segment is a point only...
            if (Math.abs(segmentVector.x) < TOL && Math.abs(segmentVector.y) < TOL && Math.abs(segmentVector.z) < TOL) {
                breakFaceInThree(facePos, startPos);
                return;
            }

            //gets the vertex more lined with the intersection segment
            int linedVertex;
            PVector linedVertexPos;
            PVector vertexVector = new PVector(endPos.x - face.v1.x, endPos.y - face.v1.y, endPos.z - face.v1.z);
            vertexVector.normalize();
            float dot1 = Math.abs(segmentVector.dot(vertexVector));
            vertexVector = new PVector(endPos.x - face.v2.x, endPos.y - face.v2.y, endPos.z - face.v2.z);
            vertexVector.normalize();
            float dot2 = Math.abs(segmentVector.dot(vertexVector));
            vertexVector = new PVector(endPos.x - face.v3.x, endPos.y - face.v3.y, endPos.z - face.v3.z);
            vertexVector.normalize();
            float dot3 = Math.abs(segmentVector.dot(vertexVector));
            if (dot1 > dot2 && dot1 > dot3) {
                linedVertex = 1;
                linedVertexPos = face.v1.getPosition();
            } else if (dot2 > dot3 && dot2 > dot1) {
                linedVertex = 2;
                linedVertexPos = face.v2.getPosition();
            } else {
                linedVertex = 3;
                linedVertexPos = face.v3.getPosition();
            }

            // Now find which of the intersection endpoints is nearest to that vertex.
            if (PVector.dist(linedVertexPos, startPos) > PVector.dist(linedVertexPos, endPos)) {
                breakFaceInFive(facePos, startPos, endPos, linedVertex);
            } else {
                breakFaceInFive(facePos, endPos, startPos, linedVertex);
            }
        }
    }

    private void breakFaceInTwo(int facePos, PVector newPos, int splitEdge) {
        Face face = faces.get(facePos);
        faces.remove(facePos);

        Vertex vertex = addVertex(newPos, Vertex.BOUNDARY);

        if (splitEdge == 1) {
            addFace(face.v1, vertex, face.v3);
            addFace(vertex, face.v2, face.v3);
        } else if (splitEdge == 2) {
            addFace(face.v2, vertex, face.v1);
            addFace(vertex, face.v3, face.v1);
        } else {
            addFace(face.v3, vertex, face.v2);
            addFace(vertex, face.v1, face.v2);
        }
    }

    private void breakFaceInTwo(int facePos, PVector newPos, Vertex endVertex) {
        Face face = faces.get(facePos);
        faces.remove(facePos);

        Vertex vertex = addVertex(newPos, Vertex.BOUNDARY);

        if (endVertex.equals(face.v1)) {
            addFace(face.v1, vertex, face.v3);
            addFace(vertex, face.v2, face.v3);
        } else if (endVertex.equals(face.v2)) {
            addFace(face.v2, vertex, face.v1);
            addFace(vertex, face.v3, face.v1);
        } else {
            addFace(face.v3, vertex, face.v2);
            addFace(vertex, face.v1, face.v2);
        }
    }

    private void breakFaceInThree(int facePos, PVector newPos1, PVector newPos2, int splitEdge) {
        Face face = faces.get(facePos);
        faces.remove(facePos);

        Vertex vertex1 = addVertex(newPos1, Vertex.BOUNDARY);
        Vertex vertex2 = addVertex(newPos2, Vertex.BOUNDARY);

        if (splitEdge == 1) {
            addFace(face.v1, vertex1, face.v3);
            addFace(vertex1, vertex2, face.v3);
            addFace(vertex2, face.v2, face.v3);
        } else if (splitEdge == 2) {
            addFace(face.v2, vertex1, face.v1);
            addFace(vertex1, vertex2, face.v1);
            addFace(vertex2, face.v3, face.v1);
        } else {
            addFace(face.v3, vertex1, face.v2);
            addFace(vertex1, vertex2, face.v2);
            addFace(vertex2, face.v1, face.v2);
        }
    }

    private void breakFaceInThree(int facePos, PVector newPos, Vertex endVertex) {
        Face face = faces.get(facePos);
        faces.remove(facePos);

        Vertex vertex = addVertex(newPos, Vertex.BOUNDARY);

        if (endVertex.equals(face.v1)) {
            addFace(face.v1, face.v2, vertex);
            addFace(face.v2, face.v3, vertex);
            addFace(face.v3, face.v1, vertex);
        } else if (endVertex.equals(face.v2)) {
            addFace(face.v2, face.v3, vertex);
            addFace(face.v3, face.v1, vertex);
            addFace(face.v1, face.v2, vertex);
        } else {
            addFace(face.v3, face.v1, vertex);
            addFace(face.v1, face.v2, vertex);
            addFace(face.v2, face.v3, vertex);
        }
    }

    private void breakFaceInThree(int facePos, PVector newPos1, PVector newPos2, Vertex startVertex, Vertex endVertex) {
        Face face = faces.get(facePos);
        faces.remove(facePos);

        Vertex vertex1 = addVertex(newPos1, Vertex.BOUNDARY);
        Vertex vertex2 = addVertex(newPos2, Vertex.BOUNDARY);

        if (startVertex.equals(face.v1) && endVertex.equals(face.v2)) {
            addFace(face.v1, vertex1, vertex2);
            addFace(face.v1, vertex2, face.v3);
            addFace(vertex1, face.v2, vertex2);
        } else if (startVertex.equals(face.v2) && endVertex.equals(face.v1)) {
            addFace(face.v1, vertex2, vertex1);
            addFace(face.v1, vertex1, face.v3);
            addFace(vertex2, face.v2, vertex1);
        } else if (startVertex.equals(face.v2) && endVertex.equals(face.v3)) {
            addFace(face.v2, vertex1, vertex2);
            addFace(face.v2, vertex2, face.v1);
            addFace(vertex1, face.v3, vertex2);
        } else if (startVertex.equals(face.v3) && endVertex.equals(face.v2)) {
            addFace(face.v2, vertex2, vertex1);
            addFace(face.v2, vertex1, face.v1);
            addFace(vertex2, face.v3, vertex1);
        } else if (startVertex.equals(face.v3) && endVertex.equals(face.v1)) {
            addFace(face.v3, vertex1, vertex2);
            addFace(face.v3, vertex2, face.v2);
            addFace(vertex1, face.v1, vertex2);
        } else {
            addFace(face.v3, vertex2, vertex1);
            addFace(face.v3, vertex1, face.v2);
            addFace(vertex2, face.v1, vertex1);
        }
    }

    private void breakFaceInThree(int facePos, PVector newPos) {
        Face face = faces.get(facePos);
        faces.remove(facePos);

        Vertex vertex = addVertex(newPos, Vertex.BOUNDARY);

        addFace(face.v1, face.v2, vertex);
        addFace(face.v2, face.v3, vertex);
        addFace(face.v3, face.v1, vertex);
    }

    private void breakFaceInFour(int facePos, PVector newPos1, PVector newPos2, Vertex endVertex) {
        Face face = faces.get(facePos);
        faces.remove(facePos);

        Vertex vertex1 = addVertex(newPos1, Vertex.BOUNDARY);
        Vertex vertex2 = addVertex(newPos2, Vertex.BOUNDARY);

        if (endVertex.equals(face.v1)) {
            addFace(face.v1, vertex1, vertex2);
            addFace(vertex1, face.v2, vertex2);
            addFace(face.v2, face.v3, vertex2);
            addFace(face.v3, face.v1, vertex2);
        } else if (endVertex.equals(face.v2)) {
            addFace(face.v2, vertex1, vertex2);
            addFace(vertex1, face.v3, vertex2);
            addFace(face.v3, face.v1, vertex2);
            addFace(face.v1, face.v2, vertex2);
        } else {
            addFace(face.v3, vertex1, vertex2);
            addFace(vertex1, face.v1, vertex2);
            addFace(face.v1, face.v2, vertex2);
            addFace(face.v2, face.v3, vertex2);
        }
    }

    private void breakFaceInFive(int facePos, PVector newPos1, PVector newPos2, int linedVertex) {
        Face face = faces.get(facePos);
        faces.remove(facePos);

        Vertex vertex1 = addVertex(newPos1, Vertex.BOUNDARY);
        Vertex vertex2 = addVertex(newPos2, Vertex.BOUNDARY);

        float cont = 0;
        if (linedVertex == 1) {
            addFace(face.v2, face.v3, vertex1);
            addFace(face.v2, vertex1, vertex2);
            addFace(face.v3, vertex2, vertex1);
            addFace(face.v2, vertex2, face.v1);
            addFace(face.v3, face.v1, vertex2);
        } else if (linedVertex == 2) {
            addFace(face.v3, face.v1, vertex1);
            addFace(face.v3, vertex1, vertex2);
            addFace(face.v1, vertex2, vertex1);
            addFace(face.v3, vertex2, face.v2);
            addFace(face.v1, face.v2, vertex2);
        } else {
            addFace(face.v1, face.v2, vertex1);
            addFace(face.v1, vertex1, vertex2);
            addFace(face.v2, vertex2, vertex1);
            addFace(face.v1, vertex2, face.v3);
            addFace(face.v2, face.v3, vertex2);
        }
    }

    public void classifyFaces(Object3D object) {
        //calculate adjacency information
        Face face;
        for (int i = 0; i < this.getNumFaces(); i++) {
            face = this.getFace(i);
            face.v1.addAdjacentVertex(face.v2);
            face.v1.addAdjacentVertex(face.v3);
            face.v2.addAdjacentVertex(face.v1);
            face.v2.addAdjacentVertex(face.v3);
            face.v3.addAdjacentVertex(face.v1);
            face.v3.addAdjacentVertex(face.v2);
        }

        //for each face
        for (int i = 0; i < getNumFaces(); i++) {
            face = getFace(i);

            //if the face vertices aren't classified to make the simple classify
            if (!face.simpleClassify()) {
                //makes the ray trace classification
                face.rayTraceClassify(object);

                //mark the vertices
                if (face.v1.getStatus() == Vertex.UNKNOWN) {
                    face.v1.mark(face.getStatus());
                }
                if (face.v2.getStatus() == Vertex.UNKNOWN) {
                    face.v2.mark(face.getStatus());
                }
                if (face.v3.getStatus() == Vertex.UNKNOWN) {
                    face.v3.mark(face.getStatus());
                }
            }
        }
    }

    public void invertInsideFaces() {
        Face face;
        for (int i = 0; i < getNumFaces(); i++) {
            face = getFace(i);
            if (face.getStatus() == Face.INSIDE) {
                face.invert();
            }
        }
    }
}
