package de.hfkbremen.mesh;

import processing.core.PApplet;
import processing.core.PVector;
import processing.data.XML;
import processing.opengl.PGraphics3D;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static de.hfkbremen.mesh.Location.exists;

public class RendererCycles extends PGraphics3D {

    /* [cycles](https://www.cycles-renderer.org/development/) */
    /* [Blender -- Command Line](https://docs.blender.org/manual/en/latest/advanced/command_line/index.html) */

    private static final String XML_NODE_CYCLES = "cycles";
    private static final String XML_NODE_CAMERA = "camera";
    private static final String XML_NODE_BACKGROUND = "background";
    private static final String XML_NODE_MESH = "mesh";
    private static final String XML_NODE_OBJECT = "state";
    private static final String XML_NODE_CONNECT = "connect";
    private static final String XML_NODE_DIFFUSE = "diffuse_bsdf";
    private static final String XML_NODE_TRANSFORM = "transform";
    private static final String XML_ATTR_WIDTH = "width";
    private static final String XML_ATTR_HEIGHT = "height";
    private static final String XML_ATTR_TYPE = "type";
    private static final String XML_ATTR_MATRIX = "matrix";
    private static final String XML_ATTR_NAME = "name";
    private static final String XML_ATTR_POINTS = "P";
    private static final String XML_ATTR_NVERTS = "nverts";
    private static final String XML_ATTR_VERTS = "verts";
    private static final String XML_ATTR_COLOR = "color";
    private static final String XML_ATTR_FROM = "from";
    private static final String XML_ATTR_TO = "to";
    private static final String XML_UNI_SHADER = "shader";
    private static final String VALUE_FROM_DIFFUSE = "diffuse bsdf";
    private static final String VALUE_OUTPUT_SURFACE = "output surface";
    private static final String SHADER_TYPE_DIFFUSE = "diffuse";
    private static final char DELIMITER = ' ';
    private static final String SHADER_NAME = "s_";
    private static final String STREAM_NAME_ERR = "ERR";
    private static final String STREAM_NAME_OUT = "OUT";
    private static final String OPTION_SAMPLES = "--samples";
    private static final String OPTION_BACKGROUND = "--background";
    private static final String mOptionOutput = "--output";
    public static String IMAGE_FILE_TYPE_PNG = ".png";
    public static String IMAGE_FILE_TYPE_JPG = ".jpg";
    public static String IMAGE_FILE_TYPE_TGA = ".tga";
    public static String OPTION_IMAGE_FILE_TYPE = IMAGE_FILE_TYPE_PNG;
    public static String CYCLES_BINARY_NAME = "cycles";
    public static String CYCLES_BINARY_PATH = null;
    public static boolean RENDERING_PROCESS_BLOCKING = false;
    public static int OPTION_NUMBER_OF_SAMPLES = 10;
    public static boolean DEBUG_PRINT_RENDER_PROGRESS = false;
    public static String CAMERA_TYPE_PERSPECTIVE = "perspective";
    public static String CAMERA_TYPE = CAMERA_TYPE_PERSPECTIVE;
    public static boolean DEBUG_PRINT_CAMERA_MATRIX = false;
    public static PVector BACKGROUND_COLOR = new PVector();
    private final ArrayList<Float> mTriangleList = new ArrayList<>();
    private final ArrayList<Float> mLineList = new ArrayList<>();
    private final Color mColorFill = new Color();
    private File mFile;
    private XML mXML;
    private String mExecPath;
    private int mShaderNameID = 0;

    public RendererCycles() {
        this((CYCLES_BINARY_PATH == null ? getLocation() : CYCLES_BINARY_PATH) + "/" + CYCLES_BINARY_NAME);
    }

    private RendererCycles(String pPathToCycles) {
        mExecPath = Location.get(pPathToCycles);
        if (!exists(mExecPath)) {
            /* try default location */
            error("couldn t find `cycles` at location `" + mExecPath + "` trying default location `" + Location.get(
                    CYCLES_BINARY_NAME) + "`");
            mExecPath = Location.get(CYCLES_BINARY_NAME);
            if (!exists(mExecPath)) {
                error("couldn t find `cycles` at default location.");
            }
        }
        if (exists(mExecPath)) {
            System.out.println("### found `cycles` at location " + mExecPath);
        }
    }

    public static RendererCycles create(PApplet pApplet, int pWidth, int pHeight, String pOutputFile) {
        return (RendererCycles) pApplet.createGraphics(pWidth, pHeight, name(), pOutputFile);
    }

    public static String name() {
        return RendererCycles.class.getName();
    }

    public static RendererCycles beginRaw(PApplet pParent, String pOutputPath) {
        return (RendererCycles) pParent.beginRaw(RendererCycles.class.getName(), pOutputPath);
    }

    public XML getXML() {
        return mXML;
    }

    public void setPath(String pPath) {
        path = pPath;
        if (pPath != null) {
            mFile = new File(pPath);
            if (!mFile.isAbsolute()) {
                mFile = null;
            }
        }
        if (mFile == null) {
            error("could not create output file `" + pPath + "`");
        }
    }

    public boolean displayable() {
        return false;
    }

    public void beginDraw() {
        if (mXML == null) {
            mXML = new XML(XML_NODE_CYCLES);
        }
    }

    public void endDraw() {
        buildCamera(mXML);
        buildBackground(mXML);

        if (mXML != null && mFile != null) {
            mXML.save(mFile);
            //            mXML.print();
            launchRenderer(mFile.getPath());
            mXML = null;
        }
    }

    /* --- PGraphics --- */

    public void beginShape(int kind) {
        shape = kind;

        if ((shape != LINES) && (shape != TRIANGLES) && (shape != POLYGON)) {
            String err = "renderer can only be used with beginRaw(), " + "because it only supports lines and " +
                         "triangles_continuous";
            throw new RuntimeException(err);
        }

        if ((shape == POLYGON) && fill) {
            throw new RuntimeException("renderer only supports non-filled shapes.");
        }

        vertexCount = 0;
    }

    public void endShape(int mode) {
        if (shape == POLYGON) {
            for (int i = 0; i < vertexCount - 1; i++) {
                writeLine(i, i + 1);
            }
            if (mode == CLOSE) {
                writeLine(vertexCount - 1, 0);
            }
        }

    /*
    if ((vertexCount != 0) &&
        ((shape != LINE_STRIP) && (vertexCount != 1))) {
      System.err.println("Extra vertex boogers found.");
    }
    */

        final String mCurrentShaderName = SHADER_NAME + mShaderNameID;
        mShaderNameID++;

        buildShader(mXML, mCurrentShaderName, fillR, fillG, fillB); // TODO fillA?
        buildObject(mXML, mCurrentShaderName, toArray(mTriangleList));
        mTriangleList.clear();

        /* TODO handle line list, build object */
        mLineList.clear();
    }

    // ..............................................................

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
//            System.out.println("stroke: " + strokeR + "," + strokeG + "," + strokeB);
        }

        if (textureImage != null) {  // for the future?
            vertex[U] = textureU;
            vertex[V] = textureV;
        }
        vertexCount++;

        if ((shape == LINES) && (vertexCount == 2)) {
            writeLine(0, 1);
            // @TODO look into `LINE_STRIP`
            //        } else if ((shape == LINE_STRIP) && (vertexCount == 2)) {
            //            writeLineStrip();
        } else if ((shape == TRIANGLES) && (vertexCount == 3)) {
            writeTriangle();
        } else if ((shape != TRIANGLES) && (shape != LINES)) {
            System.out.println("shape ( see `PConstants` ) not recognized: " + shape);
        }
    }

    // ..............................................................

    public boolean is2D() {
        return false;
    }

    public boolean is3D() {
        return true;
    }

    protected void writeLine(int index1, int index2) {
        // @TODO expand line into triangles
        addTriangleVertex(vertices[index1][X], vertices[index1][Y], vertices[index1][Z]);
        addTriangleVertex(vertices[index2][X], vertices[index2][Y], vertices[index2][Z]);
        addTriangleVertex(vertices[index2][X] + 10.0f, vertices[index2][Y], vertices[index2][Z] + 10.0f);
        vertexCount = 0;
    }

    protected void writeTriangle() {
        // @TODO vertex coloring is ignored. last vertex color is used to color triangle

        float[] vertex = vertices[2];
        detectMaterialChange(new Color(vertex[R], vertex[G], vertex[B], vertex[A]));

        //        vertex[R] = fillR;
        //        vertex[G] = fillG;
        //        vertex[B] = fillB;
        //        vertex[A] = fillA;
        //        vertex[SR] = strokeR;
        //        vertex[SG] = strokeG;
        //        vertex[SB] = strokeB;
        //        vertex[SA] = strokeA;

        for (int i = 0; i < 3; i++) {
            addTriangleVertex(vertices[i][X], vertices[i][Y], vertices[i][Z]);
        }
        vertexCount = 0;
    }

    private void addTriangleVertex(float x, float y, float z) {
        mTriangleList.add(x);
        mTriangleList.add(y);
        mTriangleList.add(z);
    }

    private void detectMaterialChange(Color c) {
        if (
                mColorFill.r != c.r ||
                mColorFill.g != c.g ||
                mColorFill.b != c.b ||
                mColorFill.a != c.a
        ) {
            mColorFill.set(c);
            System.out.println("material change " + mColorFill);
        }
    }

    private static String getLocation() {
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

    private void error(String pErrorMessage) {
        System.err.println("### @" + getClass().getSimpleName() + " / " + pErrorMessage);
    }

    private void console(String pErrorMessage) {
        System.out.println("+++ @" + getClass().getSimpleName() + " / " + pErrorMessage);
    }

    private void buildCamera(XML pXML) {
        //        if (!(g instanceof PGraphics3D)) {
        //            error("camera requires a `PGraphics3D` context to function");
        //        }
        int mWidth = width;
        int mHeight = height;
        float[] mCameraMatrix = new float[16];
        getMatrix().get(mCameraMatrix);
        mCameraMatrix[14] = -height * 2;// TODO find out why this is necessary / still off 105%
        // TODO fix y-axis flip
        mCameraMatrix[0] *= 1;
        mCameraMatrix[5] *= 1;
        mCameraMatrix[10] *= 1;

        float[] mParentCameraMatrix = new float[16];
        parent.g.getMatrix().get(mParentCameraMatrix);

        // @DEBUG
        if (DEBUG_PRINT_CAMERA_MATRIX) {
            for (int i = 0; i < mParentCameraMatrix.length; i++) {
                System.out.print(mParentCameraMatrix[i] + ", ");
                if (i % 4 == 3) {
                    System.out.println();
                }
            }
        }

        {
            XML mCamera = new XML(XML_NODE_CAMERA);
            mCamera.setInt(XML_ATTR_WIDTH, mWidth);
            mCamera.setInt(XML_ATTR_HEIGHT, mHeight);
            pXML.addChild(mCamera);
        }
        {
            XML mCameraTransform = new XML(XML_NODE_TRANSFORM);
            setMatrix4x4(mCameraTransform, mCameraMatrix);
            XML mCamera = new XML(XML_NODE_CAMERA);
            mCamera.setString(XML_ATTR_TYPE, CAMERA_TYPE);
            mCameraTransform.addChild(mCamera);
            pXML.addChild(mCameraTransform);
        }
        //          <camera width="800" height="500" />
        //          <transform matrix="1 0 0 0
        //                             0 1 0 0
        //                             0 0 1 0
        //                             0 0 -4 1">
        //              <camera type="perspective" />
        //          </transform>
    }

    private void buildBackground(XML pXML) {
        final String BACKGROUND_NAME = "bg";
        XML mBackgroundNode = new XML(XML_NODE_BACKGROUND);
        {
            XML mBackgroundPropertyNode = new XML(XML_NODE_BACKGROUND);
            mBackgroundPropertyNode.setString(XML_ATTR_NAME, BACKGROUND_NAME);
            mBackgroundPropertyNode.setFloat("strength", 2.0f);
            mBackgroundPropertyNode.setString(XML_ATTR_COLOR,
                                              getColorAttr(BACKGROUND_COLOR.x, BACKGROUND_COLOR.y, BACKGROUND_COLOR.z));
            mBackgroundPropertyNode.setFloat("SurfaceMixWeight", 1.0f);
            mBackgroundNode.addChild(mBackgroundPropertyNode);

            XML mConnectNode = new XML(XML_NODE_CONNECT);
            mConnectNode.setString(XML_ATTR_FROM, BACKGROUND_NAME + DELIMITER + "background");
            mConnectNode.setString(XML_ATTR_TO, VALUE_OUTPUT_SURFACE);
            mBackgroundNode.addChild(mConnectNode);
        }
        pXML.addChild(mBackgroundNode);
        //      <background>
        //	        <background name="bg" strength="2.0" color="0.5, 0.5, 0.5" SurfaceMixWeight="1.0"/>
        //	        <connect from="bg background" to="output surface" />
        //      </background>
    }

    private void buildShader(XML pXML, String pShaderName, float r, float g, float b) {
        final float mRoughness = 0.0f;
        XML mShaderNode = new XML(XML_UNI_SHADER);
        mShaderNode.setString(XML_ATTR_NAME, pShaderName);
        {
            XML mDiffuseNode = new XML(XML_NODE_DIFFUSE);
            mDiffuseNode.setString(XML_ATTR_NAME, SHADER_TYPE_DIFFUSE);
            mDiffuseNode.setFloat("roughness", mRoughness);
            mDiffuseNode.setString(XML_ATTR_COLOR, getColorAttr(r, g, b));
            mShaderNode.addChild(mDiffuseNode);
        }
        {
            XML mConnectNode = new XML(XML_NODE_CONNECT);
            mConnectNode.setString(XML_ATTR_FROM, VALUE_FROM_DIFFUSE);
            mConnectNode.setString(XML_ATTR_TO, VALUE_OUTPUT_SURFACE);
            mShaderNode.addChild(mConnectNode);
        }
        pXML.addChild(mShaderNode);
        //      <shader name="floor">
        //	        <diffuse_bsdf name="diffuse" roughness="0.0" color="1.0, 0.5, 0.0" />
        //	        <connect from="diffuse bsdf" to="output surface" />
        //      </shader>
    }

    private void buildObject(XML pXML, String pShaderName, float[] pTriangleList) {
        XML mObjNode = new XML(XML_NODE_OBJECT);
        mObjNode.setString(XML_UNI_SHADER, pShaderName);
        XML mMeshNode = buildMesh(pTriangleList);
        mObjNode.addChild(mMeshNode);
        pXML.addChild(mObjNode);
        //      <state shader="floor">
        //	        <mesh P="-3 3 0  3 3 0  3 -3 0  -3 -3 0" nverts="4" verts="0 1 2 3" />
        //      </state>
    }

    private XML buildMesh(float[] pTriangleList) {
        StringBuilder mPoints = new StringBuilder();
        StringBuilder mNumberOfVertices = new StringBuilder();
        StringBuilder mVertexList = new StringBuilder();

        for (int i = 0; i < pTriangleList.length; i += 9) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    mPoints.append(pTriangleList[i + j * 3 + k]).append(DELIMITER);
                }
                mPoints.append(DELIMITER);
                mVertexList.append(i / 3 + j).append(DELIMITER);
            }
            mVertexList.append(DELIMITER);
            mNumberOfVertices.append(3).append(DELIMITER);
        }

        XML mMeshNode = new XML(XML_NODE_MESH);
        mMeshNode.setString(XML_ATTR_POINTS, mPoints.toString());
        mMeshNode.setString(XML_ATTR_NVERTS, mNumberOfVertices.toString());
        mMeshNode.setString(XML_ATTR_VERTS, mVertexList.toString());
        return mMeshNode;
        //	        <mesh P="-3 3 0  3 3 0  3 -3 0  -3 -3 0" nverts="4" verts="0 1 2 3" />
        //	        <mesh P="-3 3 0  3 3 0  3 -3 0" nverts="3" verts="0 1 2 " />
    }

    private String getColorAttr(float r, float g, float b) {
        final String D = ", ";
        return r + D + g + D + b;
    }

    private void setMatrix4x4(XML pNode, float[] pMatrix4x4) {
        StringBuilder s = new StringBuilder();
        for (float aPMatrix4x4 : pMatrix4x4) {
            s.append(aPMatrix4x4);
            s.append(' ');
        }
        pNode.setString(XML_ATTR_MATRIX, s.toString());
    }

    private void launchRenderer(String pXMLPath) {
        try {
            final String mOptionSamplesValue = String.valueOf(OPTION_NUMBER_OF_SAMPLES);
            final String mOptionOutputValue = path + OPTION_IMAGE_FILE_TYPE;
            final String[] mCommandString = new String[]{mExecPath,
                                                         OPTION_SAMPLES,
                                                         mOptionSamplesValue,
                                                         OPTION_BACKGROUND,
                                                         mOptionOutput,
                                                         mOptionOutputValue,
                                                         pXMLPath};
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

    private static float[] toArray(List<Float> pFloatList) {
        float[] mArray = new float[pFloatList.size()];
        for (int i = 0; i < mArray.length; i++) {
            mArray[i] = pFloatList.get(i);
        }
        return mArray;
    }

    /* --- console output--- */

    private static class StreamConsumer extends Thread {

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

    private static class Color {

        float r, g, b, a;

        public Color(float pR, float pG, float pB, float pA) {
            r = pR;
            g = pG;
            b = pB;
            a = pA;
        }

        public Color() {
            this(-1, -1, -1, -1);
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
    }
}
