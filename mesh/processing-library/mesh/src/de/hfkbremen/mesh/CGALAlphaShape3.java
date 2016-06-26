package de.hfkbremen.mesh;

import processing.core.PVector;

/**
 * http://doc.cgal.org/latest/Alpha_shapes_3/
 * <p>
 * http://www.loria.fr/~pougetma/software/alpha_shape/alpha_shape.html
 */

public class CGALAlphaShape3 {

    static {
        System.out.print("### loading native lib `CGALAlphaShape3` ...");
        System.loadLibrary("CGALAlphaShape3");
        System.out.println(" ok");
    }

    private long ptr2cgalAlphaShape;

    /**
     * print version of library.
     *
     * @return
     */
    public native int version();

    /**
     * Initialize the alpha_shape from the array of points coordinates
     * (coord), return a pointer to the c++ alpha_shape object for
     * subsequent native method calls.
     *
     * @param pts_coord
     * @return
     */
    private native long init_alpha_shape(float[] pts_coord);

    /**
     * For a given value of alpha and a given class_type for the facets,
     * sets the alpha value of the alpha_shape to alpha. Returns the array
     * of facet indices from the alpha_shape.
     *
     * @param classification_type
     * @param alpha
     * @param ptr
     * @return
     */
    private native int[] get_alpha_shape_facets(String classification_type, float alpha, long ptr);

    /**
     * For a given number of solid components and a given class_type for
     * the facets, sets the alpha value of the alpha_shape A such that A
     * satisfies the following two properties: (1) all data points are
     * either on the boundary or in the interior of the regularized
     * version of A; (2) the number of solid component of A is equal to or
     * smaller than nb_components. Returns the array of facet indices from
     * the alpha_shape.
     *
     * @param classification_type
     * @param nb_sc
     * @param ptr
     * @return
     */
    private native int[] get_alpha_shape_facets_optimal(String classification_type, int nb_sc, long ptr);

    /**
     * gives the alpha value of the current alpha_shape
     *
     * @param ptr
     * @return
     */
    private native float get_alpha(long ptr);

    /**
     * Returns the number of solid components of the current alpha_shape,
     * that is, the number of components of its regularized version.
     *
     * @param ptr
     * @return
     */
    private native int number_of_solid_components(long ptr);

    /**
     *
     * @param classification_type
     * @param alpha
     * @param ptr
     * @return
     */
    public native float[] get_alpha_shape_mesh(String classification_type, float alpha, long ptr);

    /* --- */

    public int number_of_solid_components() {
        return number_of_solid_components(ptr2cgalAlphaShape);
    }

    public void compute_cgal_alpha_shape(float[] pts_coord) {
        ptr2cgalAlphaShape = init_alpha_shape(pts_coord);
    }

    public void compute_cgal_alpha_shape(PVector[] vertices) {
        float[] pts_coord = new float[vertices.length * 3];
        for (int i = 0; i < vertices.length; i++) {
            pts_coord[3 * i + 0] = vertices[i].x;
            pts_coord[3 * i + 1] = vertices[i].y;
            pts_coord[3 * i + 2] = vertices[i].z;
        }

        ptr2cgalAlphaShape = init_alpha_shape(pts_coord);
    }

    public float[] compute_regular_mesh(float m_alpha) {
        return get_alpha_shape_mesh("REGULAR", m_alpha, ptr2cgalAlphaShape);
    }

    /**
     * Update the regular_facets geometry for a given alpha value
     */
    public int[] compute_regular_facets(float m_alpha) {
        /* call the native method */
        return get_alpha_shape_facets("REGULAR", m_alpha, ptr2cgalAlphaShape);
    }

    /**
     * Update the singular_facets geometry for a given alpha value
     */
    public int[] compute_singular_facets(float m_alpha) {
        return get_alpha_shape_facets("SINGULAR", m_alpha, ptr2cgalAlphaShape);
    }

    /**
     * Update the regular_facets geometry for a given nb of solid components
     */
    public int[] compute_regular_facets_optimal(int nb_of_solid_components) {
        return get_alpha_shape_facets_optimal("REGULAR", nb_of_solid_components, ptr2cgalAlphaShape);
    }

    /**
     * Update the singular_facets geometry for a given nb of solid components
     */
    public int[] compute_singular_facets_optimal(int nb_of_solid_components) {
        return get_alpha_shape_facets_optimal("SINGULAR", nb_of_solid_components, ptr2cgalAlphaShape);
    }

    public float getAlpha() {
        return get_alpha(ptr2cgalAlphaShape);
    }

    public int getNumberOfSolidComponents() {
        return number_of_solid_components(ptr2cgalAlphaShape);
    }

    //    /**
    //     * Compute the regular and singular facets of the alpha shape for the given alpha,
    //     * the nb of solid components is updated.
    //     */
    //    private void start(float m_alpha) {
    //        compute_regular_facets(m_alpha);
    //        compute_singular_facets(m_alpha);
    //        int nb_of_solid_components = number_of_solid_components(ptr2cgalAlphaShape);
    //    }
    //
    //    /**
    //     * Compute the regular and singular facets of the alpha shape for a given nb of solid components,
    //     * the alpha is updated.
    //     */
    //    private void startOptimal(int nb_of_solid_components) {
    //        this.compute_regular_facets_optimal(nb_of_solid_components);
    //        this.compute_singular_facets_optimal(nb_of_solid_components);
    //        double m_alpha = get_alpha(ptr2cgalAlphaShape);
    //    }
}
