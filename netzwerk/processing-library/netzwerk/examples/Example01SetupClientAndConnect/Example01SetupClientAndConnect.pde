import de.hfkbremen.netzwerk.*;
import netP5.*;
import oscP5.*;

NetzwerkClient mClient;
void settings() {
    size(640, 480);
}
void setup() {
    /*
     * create a client that connects to the specified `server`. the `server` is specified through
     * an IP address ( e.g "192.168.1.100" ) or a resolvable network name ( e.g "dennisppaul.local" ).
     * the `sender` is the name under which the client sends messages. */
    mClient = new NetzwerkClient(this, "localhost", "client");
    /*
     * network party knowledge: if the server is running on the same machine as the sketch it can
     * also bee specified as `localhost` or with the reserved IP address `127.0.0.1` ( i.e local machine ).
     *
     */
}
void draw() {
    background(255);
}
void keyPressed() {
    /* connect to or disconnect from server by pressing `.` or `,` */
    if (key == ',') {
        mClient.disconnect();
    }
    if (key == '.') {
        mClient.connect();
    }
}
