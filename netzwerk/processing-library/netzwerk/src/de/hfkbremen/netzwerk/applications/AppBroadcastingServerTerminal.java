package de.hfkbremen.netzwerk.applications;

import de.hfkbremen.netzwerk.NetzwerkServer;

/**
 * run this from the terminal. make sure to point it to the required JARs. like this e.g:
 * <p>
 * <pre><code>
 *     java -cp /Users/dennisppaul/Documents/Processing/libraries/netzwerk/library/netzwerk
 *     .jar:/Users/dennisppaul/Documents/Processing/libraries/oscP5/library/oscP5.jar:/Applications/Pressing
 *     .app/Contents/Java/core/library/core.jar
 * de.hfkbremen.netzwerk.applications.AppBroadcastingServerTerminal
 * </code></pre>
 */
public class AppBroadcastingServerTerminal {

    public static void main(String[] args) {
        NetzwerkServer.PRINT_MESSAGES_TO_CONSOLE = true;
        if (args.length == 0) {
            new NetzwerkServer();
        } else {
            int mListeningPort = Integer.parseInt(args[0]);
            System.out.println("### listening on port " + mListeningPort);
            new NetzwerkServer(mListeningPort);
        }
    }
}
