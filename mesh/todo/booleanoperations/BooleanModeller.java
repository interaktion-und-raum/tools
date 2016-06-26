package claylike.booleanoperations;


import java.util.ArrayList;

import mathematik.Vector3f;


/**
 * Class used to apply boolean operations on solids.
 *
 * <br><br>Two 'Solid' objects are submitted to this class constructor. There is a methods for
 * each boolean operation. Each of these return a 'Solid' resulting from the application
 * of its operation into the submitted solids.
 *
 * D. H. Laidlaw, W. B. Trumbore, and J. F. Hughes.
 * "Constructive Solid Geometry for Polyhedral Objects"
 * SIGGRAPH Proceedings, 1986, p.161.
 *
 * @author Danilo Balby Silva Castanheira (danbalby@yahoo.com)
 */

public class BooleanModeller
    implements Cloneable {

    private Object3D object1;

    private Object3D object2;


    public BooleanModeller(Solid solid1, Solid solid2) {
        //representation to apply boolean operations
        object1 = new Object3D(solid1);
        object2 = new Object3D(solid2);

        //split the faces so that none of them intercepts each other
        object1.splitFaces(object2);
        object2.splitFaces(object1);

        //classify faces as being inside or outside the other solid
        object1.classifyFaces(object2);
        object2.classifyFaces(object1);
    }


    public Object clone() {
        try {
            BooleanModeller clone = (BooleanModeller)super.clone();
            clone.object1 = (Object3D) object1.clone();
            clone.object2 = (Object3D) object2.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }


    public Solid getUnion() {
        return composeSolid(Face.OUTSIDE, Face.SAME, Face.OUTSIDE);
    }

    public Solid getIntersection() {
        return composeSolid(Face.INSIDE, Face.SAME, Face.INSIDE);
    }


    public Solid getDifference() {
        object2.invertInsideFaces();
        Solid result = composeSolid(Face.OUTSIDE, Face.OPPOSITE, Face.INSIDE);
        object2.invertInsideFaces();

        return result;
    }



    private Solid composeSolid(int faceStatus1, int faceStatus2, int faceStatus3) {
        ArrayList vertices = new ArrayList();
        ArrayList indices = new ArrayList();

        //group the elements of the two solids whose faces fit with the desired status
        groupObjectComponents(object1, vertices, indices, faceStatus1, faceStatus2);
        groupObjectComponents(object2, vertices, indices, faceStatus3, faceStatus3);

        //turn the arrayLists to arrays
        Vector3f[] verticesArray = new Vector3f[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            verticesArray[i] = ( (Vertex) vertices.get(i)).getPosition();
        }
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = ( (Integer) indices.get(i)).intValue();
        }

        //returns the solid containing the grouped elements
        return new Solid(verticesArray, indicesArray);
    }


    private void groupObjectComponents(Object3D object,
                                       ArrayList vertices,
                                       ArrayList indices,
                                       int faceStatus1, int faceStatus2) {
        Face face;
        //for each face..
        for (int i = 0; i < object.getNumFaces(); i++) {
            face = object.getFace(i);
            //if the face status fits with the desired status...
            if (face.getStatus() == faceStatus1 || face.getStatus() == faceStatus2) {
                //adds the face elements into the arrays
                Vertex[] faceVerts = {face.v1, face.v2, face.v3};
                for (int j = 0; j < faceVerts.length; j++) {
                    if (vertices.contains(faceVerts[j])) {
                        indices.add(new Integer(vertices.indexOf(faceVerts[j])));
                    } else {
                        indices.add(new Integer(vertices.size()));
                        vertices.add(faceVerts[j]);
                    }
                }
            }
        }
    }
}
