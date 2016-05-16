package de.hfkbremen.mesh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LuxRender {

    private static String LUX_RENDERER_BINARY_PATH = "/usr/local/bin/luxconsole";
    private static String OUTPUT_PATH = "/Users/dennisppaul/Desktop/foo1.png";

    public static void _main(String[] args) {
        try {
            /* assemble shell command */
            String myParameter = LUX_RENDERER_BINARY_PATH;
            String[] myExecString = new String[]{myParameter,
                                                 // this might make noise in animations more stable
                                                 "--fixedseed",
                                                 // output path
                                                 "--output",
                                                 OUTPUT_PATH,
                                                 "/Applications/LuxRender/examples/custom_render_scene/custom_render_scene.lxs"};
            Process myProcess = Runtime.getRuntime().exec(myExecString);
            BufferedReader br = new BufferedReader(new InputStreamReader(myProcess.getInputStream()));

            //            /* assemble query */
            //            OutputStream myOutputStream = myProcess.getOutputStream();
            //            myOutputStream.write(String.valueOf(pDimensions).getBytes());
            //            myOutputStream.write('\n');
            //            myOutputStream.write(String.valueOf(pPoints.length).getBytes());
            //            myOutputStream.write('\n');
            //            for (Vector3f pPoint : pPoints) {
            //                if (pDimensions == 3) {
            //                    String myVectorString = pPoint.x + " " + pPoint.y + " " + pPoint.z;
            //                    myOutputStream.write(myVectorString.getBytes());
            //                } else if (pDimensions == 2) {
            //                    String myVectorString = pPoint.x + " " + pPoint.y;
            //                    myOutputStream.write(myVectorString.getBytes());
            //                }
            //                myOutputStream.write('\n');
            //            }
            //            myOutputStream.close();

            //            BufferedWriter writer = new BufferedWriter(new FileWriter("/Applications/LuxRender/examples/custom_render_scene/out.raw"));
            //
            //            byte[] buffer = new byte[1000];
            //            int nRead;
            //            while((nRead = br.read()) != -1) {
            //                // Convert to String so we can display it.
            //                // Of course you wouldn't want to do this with
            //                // a 'real' binary file.
            ////                System.out.println(new String(buffer));
            //                writer.write(nRead);
            //            }
            //            writer.close();
            //            br.close();

            //            /* collect result */
            //            //            BufferedWriter writer = new BufferedWriter(new FileWriter("/Applications/LuxRender/examples/custom_render_scene/out.raw"));
            //            final StringBuilder myResult = new StringBuilder();
            //            String myLine;
            //            while ((myLine = br.readLine()) != null) {
            //                //                System.out.println("----------------------");
            //                myResult.append(myLine);
            //                myResult.append('\n');
            //                System.out.println(myLine);
            //                //                writer.write(myLine);
            //            }

            /* open  */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class StreamGobbler extends Thread {

        InputStream is;
        String type;

        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(type + ">" + line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        try {
            String osName = System.getProperty("os.name");
            System.out.println("+++ current OS: " + osName);
            String[] cmd = new String[5];
            if (osName.equals("Mac OS X")) {
                cmd[0] = LUX_RENDERER_BINARY_PATH;
                cmd[1] = "--output";
                cmd[2] = OUTPUT_PATH;
                cmd[3] = "--fixedseed";
                cmd[4] = "/Applications/LuxRender/examples/custom_render_scene/custom_render_scene.lxs";
            } else if (osName.startsWith("Win")) {
                cmd[0] = "cmd.exe";
                cmd[1] = "/C";
                cmd[2] = "dir *.java";
            }

            Runtime rt = Runtime.getRuntime();
            System.out.println("Execing " + cmd[0] + " " + cmd[1] + " " + cmd[2]);
            Process proc = rt.exec(cmd);

            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
            errorGobbler.start();
            outputGobbler.start();

            int exitVal = proc.waitFor();
            System.out.println("+++ ExitValue: " + exitVal);

            // open results
            if (osName.equals("Mac OS X")) {
                Runtime.getRuntime().exec(new String[]{"open", OUTPUT_PATH});
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
