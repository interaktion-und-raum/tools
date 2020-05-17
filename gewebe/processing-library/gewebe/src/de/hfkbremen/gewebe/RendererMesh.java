package de.hfkbremen.gewebe;

import processing.core.PVector;
import processing.opengl.PGraphics3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class RendererMesh extends PGraphics3D {

    protected static final String STREAM_NAME_ERR = "ERR";
    protected static final String STREAM_NAME_OUT = "OUT";
    public static boolean DEBUG_PRINT_RENDER_PROGRESS = false;
    public static boolean RENDERING_PROCESS_BLOCKING = false;
    public static boolean LINE_EXPAND_WITH_CLOSED_CAPS = true;
    private final ArrayList<ShaderTriangleBucket> mShaderTriangleBuckets = new ArrayList<>();
    private final Color mColorFill = new Color();
    private ShaderTriangleBucket mBucket = null;

    public void beginDraw() {
        prepareFrame();
    }

    public void endDraw() {
        finalizeFrame();
        mShaderTriangleBuckets.clear();
    }

    protected ArrayList<ShaderTriangleBucket> bucket() {
        return mShaderTriangleBuckets;
    }

    public void beginShape(int kind) {
        shape = kind;
        beginFrame();
        vertexCount = 0;

        //        if ((shape != LINES) && (shape != TRIANGLES) && (shape != POLYGON)) {
        //            String err = "renderer can only be used with beginRaw(), " + "because it only supports lines
        //            and " +
        //                         "triangles_continuous";
        //            throw new RuntimeException(err);
        //        }
        //
        //        if ((shape == POLYGON) && fill) {
        //            throw new RuntimeException("renderer only supports non-filled shapes.");
        //        }
    }

    public void endShape(int mode) {
        endFrame(mode);
    }

    public void vertex(float x, float y) {
        vertex(x, y, 0);
    }

    public void vertex(float x, float y, float z) {
        float[] vertex = vertices[vertexCount];

        vertex[X] = x;  // note: not mx, my, mz like PGraphics3
        vertex[Y] = y;
        vertex[Z] = z;

        if (fill) {
            vertex[R] = fillR;
            vertex[G] = fillG;
            vertex[B] = fillB;
            vertex[A] = fillA;
        }

        if (stroke) {
            vertex[SR] = strokeR;
            vertex[SG] = strokeG;
            vertex[SB] = strokeB;
            vertex[SA] = strokeA;
            vertex[SW] = strokeWeight;
        }

        if (textureImage != null) {  // for the future?
            vertex[U] = textureU;
            vertex[V] = textureV;
        }
        vertexCount++;

        if ((shape == LINES) && (vertexCount == 2)) {
            writeLine(0, 1);
        } else if ((shape == TRIANGLES) && (vertexCount == 3)) {
            writeTriangle();
        } else if ((shape != TRIANGLES) && (shape != LINES)) {
            System.out.println("shape ( see `PConstants` ) not recognized: " + shape);
        }
    }

    public boolean is2D() {
        return false;
    }

    public boolean is3D() {
        return true;
    }

    public void setPath(String pPath) {
        path = pPath;
    }

    public boolean displayable() {
        return false;
    }

    protected abstract void beginFrame();

    protected abstract void endFrame(int mode);

    protected abstract void prepareFrame();

    protected abstract void finalizeFrame();

    protected void writeTriangle() {
        if (fill) {
            // @TODO individual vertex coloring is ignored. last vertex color is used to color triangle
            float[] vertex = vertices[2];
            RendererCycles.Color c = new RendererCycles.Color(vertex[R], vertex[G], vertex[B], vertex[A]);
            selectBucket(c);
            for (int i = 0; i < 3; i++) {
                addTriangleVertex(vertices[i][X], vertices[i][Y], vertices[i][Z]);
            }
        }
        vertexCount = 0;
    }

    protected void writeLine(int v0, int v1) {
        if (stroke) {
            float[] vertex = vertices[v0];
            RendererCycles.Color c = new RendererCycles.Color(vertex[SR], vertex[SG], vertex[SB], vertex[SA]);
            selectBucket(c);

            PVector p0 = new PVector(vertices[v0][X], vertices[v0][Y], vertices[v0][Z]);
            PVector p1 = new PVector(vertices[v1][X], vertices[v1][Y], vertices[v1][Z]);
            final float mSize = strokeWeight / 2.0f;
            ArrayList<PVector> pTriangles = Line3.triangles(p0, p1, mSize, LINE_EXPAND_WITH_CLOSED_CAPS, null);
            for (PVector p : pTriangles) {
                addTriangleVertex(p.x, p.y, p.z);
            }
            vertexCount = 0;
        }
    }

    protected void error(String pErrorMessage) {
        System.err.println("### @" + getClass().getSimpleName() + " / " + pErrorMessage);
    }

    protected void console(String pErrorMessage) {
        System.out.println("+++ @" + getClass().getSimpleName() + " / " + pErrorMessage);
    }

    protected static float[] toArray(List<Float> pFloatList) {
        float[] mArray = new float[pFloatList.size()];
        for (int i = 0; i < mArray.length; i++) {
            mArray[i] = pFloatList.get(i);
        }
        return mArray;
    }

    protected static String getLocation() {
        String mLocation = "";
        try {
            File mFile = new File(RendererCycles.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            mLocation = mFile.getParentFile().getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("### @" + RendererCycles.class.getSimpleName() + " / " + "can not locate library " +
                               "location using relative path.");
        }
        return mLocation;
    }

    protected void launchRenderProcess(String[] mCommandString) {
        try {
            Process mProcess = Runtime.getRuntime().exec(mCommandString);
            StringBuilder sb = new StringBuilder();
            for (String s : mCommandString) {
                sb.append(s);
                sb.append(' ');
            }
            console("start render process: `" + sb.toString() + "`");

            StreamConsumer mErrorStream = new StreamConsumer(mProcess.getErrorStream(), STREAM_NAME_ERR);
            StreamConsumer mOutputStream = new StreamConsumer(mProcess.getInputStream(), STREAM_NAME_OUT);
            mErrorStream.start();
            mOutputStream.start();

            if (RENDERING_PROCESS_BLOCKING) {
                try {
                    mProcess.waitFor();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            console("finish render process");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void addTriangleVertex(float x, float y, float z) {
        if (mBucket != null) {
            mBucket.triangles.add(x);
            mBucket.triangles.add(y);
            mBucket.triangles.add(z);
        }
    }

    private void selectBucket(RendererCycles.Color c) {
        if (detectShaderChange(c)) {
            mBucket = findBucket(c);
        }
    }

    private RendererCycles.ShaderTriangleBucket findBucket(RendererCycles.Color c) {
        for (RendererCycles.ShaderTriangleBucket s : mShaderTriangleBuckets) {
            if (s.color.isEqual(c)) {
                return s;
            }
        }
        RendererCycles.ShaderTriangleBucket s = new RendererCycles.ShaderTriangleBucket(c);
        mShaderTriangleBuckets.add(s);
        return s;
    }

    private boolean detectShaderChange(Color c) {
        if (mColorFill.isEqual(c)) {
            return false;
        } else {
            mColorFill.set(c);
            return true;
        }
    }

    protected static class StreamConsumer extends Thread {

        private final InputStream mStream;

        private final String mStreamName;

        StreamConsumer(InputStream theStream, String theStreamName) {
            mStream = theStream;
            mStreamName = theStreamName;
        }

        public void run() {
            try {
                final InputStreamReader isr = new InputStreamReader(mStream);
                final BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    if (mStreamName.equalsIgnoreCase(STREAM_NAME_ERR) || DEBUG_PRINT_RENDER_PROGRESS) {
                        System.out.println(mStreamName + "> " + line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected static class Color {

        public float r, g, b, a;

        public Color(float pR, float pG, float pB, float pA) {
            r = pR;
            g = pG;
            b = pB;
            a = pA;
        }

        public Color() {
            this(-1, -1, -1, -1);
        }

        public Color(Color pColor) {
            set(pColor);
        }

        public void set(float pR, float pG, float pB, float pA) {
            r = pR;
            g = pG;
            b = pB;
            a = pA;
        }

        @Override
        public String toString() {
            return "Color{" +
                   "r=" + r +
                   ", g=" + g +
                   ", b=" + b +
                   ", a=" + a +
                   '}';
        }

        public void set(Color c) {
            r = c.r;
            g = c.g;
            b = c.b;
            a = c.a;
        }

        public boolean isEqual(Color c) {
            return r == c.r
                   && g == c.g
                   && b == c.b
                   && a == c.a;
        }
    }

    protected static class ShaderTriangleBucket {

        final Color color;
        final ArrayList<Float> triangles = new ArrayList<>();

        ShaderTriangleBucket(Color pColor) {
            color = new Color(pColor);
        }

    }

}