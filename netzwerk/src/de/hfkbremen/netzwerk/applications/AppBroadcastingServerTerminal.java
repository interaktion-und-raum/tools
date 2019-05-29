package de.hfkbremen.netzwerk.applications;

import de.hfkbremen.netzwerk.NetzwerkServer;

/*
 * run this command from the terminal. adjust the paths and to point it to the required JARs. like this e.g:
 *
java -cp  /Users/dennisppaul/Documents/Processing/libraries/netzwerk/library/netzwerk.jar:\
/Users/dennisppaul/Documents/Processing/libraries/oscP5/library/oscP5.jar:\
/Applications/Processing.app/Contents/Java/core/library/core.jar \
de.hfkbremen.netzwerk.applications.AppBroadcastingServerTerminal
 *
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
