package de.hfkbremen.filter.examples;

import jkalman.JKalman;
import processing.core.PApplet;
import jama.Matrix;

/**
 * demo of JKalman ( http://sourceforge.net/projects/jkalman )
 */
public class SketchTODOKalmanFilter extends PApplet {

    float d = 0.0f; // used for noise (if not mouse input)
    float pv; // previous 'meassured' value
    float pk; // previous kalman corrected value

    int px = -5;
    int x = -5;

    JKalman kalman;
    Matrix c;
    Matrix m;

    public void settings() {
        size(500, 200);
    }

    public void setup() {
        smooth();
        background(33);

        try {
            kalman = new JKalman(4, 2); // for two values actually…

            // correct and measurement matrizes for both values
            c = new Matrix(4, 1);
            m = new Matrix(2, 1);
            m.set(0, 0, 0);
            m.set(1, 0, 0);

            // heck, I have no idea…
            double[][] tr = {
                {
                    1, 0, 1, 0}, {
                    0, 1, 0, 1}, {
                    0, 0, 1, 0}, {
                    0, 0, 0, 1}
            };

            kalman.setTransition_matrix(new Matrix(tr));
            kalman.setError_cov_post(kalman.getError_cov_post().identity());
        } catch (Exception e) {
        }
    }

    public void draw() {
        float v = noise(d) * height;
        d += 0.08; // quite noisy

        // predict
        kalman.Predict();
        // measure
        m.set(0, 0, v);
        //m.set(1, 0, 1.0f); // nothing actually…
        // correct
        c = kalman.Correct(m);

        strokeWeight(0.5f);
        stroke(150);
        line(px, pv, x, v);

        strokeWeight(0.8f);
        stroke(255, 255, 0);
        line(px, pk, x, (float) c.get(0, 0));

        pv = v;
        pk = (float) c.get(0, 0);

        px = x;
        x++;
        if (x >= width) {
            background(33);
            x = -5;
            px = -5;
        }
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{SketchTODOKalmanFilter.class.getName()});
    }
}
