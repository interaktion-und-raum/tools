package de.hfkbremen.netzwerk;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class NetzwerkClient {

    private static final String mReceiveMethod = "receive";
    private static final String mPingMethod = "ping";
    private static final int MIN_PORT_NUMBER = 12000;
    private static final String DELIMITER = "/";
    private static final String ANONYM = "anonymous";
    private static final String LOCALHOST = "127.0.0.1";
    public static boolean SHOW_LOG = true;
    private final OscP5 mOSC;
    private final NetAddress mBroadcastLocation;
    private final String mSenderName;
    private final Object mClientParent;
    private final String mIP;
    private final int mPort;
    private Method mMethodReceive3f;
    private Method mMethodReceive2f;
    private Method mMethodReceive1f;
    private Method mMethodReceiveStr;
    private Method mMethodReceiveRaw;
    private Method mMethodPing;

    public NetzwerkClient(Object pClientParent, String pServer, String pSenderName) {
        this(pClientParent, pServer, pSenderName, Netzwerk.SERVER_DEFAULT_BROADCAST_PORT, findAvailablePort());
    }

    public NetzwerkClient(Object pClientParent,
                          String pServer,
                          String pSenderName,
                          int pServerListeningPort,
                          int pClientListeningPort) {
        mClientParent = pClientParent;

        mPort = pClientListeningPort;

        mOSC = new OscP5(this, mPort);
        mIP = mOSC.ip();
        log("+++", "client is @ " + mIP + " + sending on port " + mPort);

        mBroadcastLocation = new NetAddress(pServer, pServerListeningPort);
        mSenderName = pSenderName;
        log("+++", "server is @ " + pServer + " + listening on port " + pServerListeningPort);

        try {
            mMethodReceive1f = mClientParent.getClass().getDeclaredMethod(mReceiveMethod,
                                                                          String.class,
                                                                          String.class,
                                                                          Float.TYPE);
        } catch (NoSuchMethodException ignored) {
        }
        try {
            mMethodReceive2f = mClientParent.getClass().getDeclaredMethod(mReceiveMethod,
                                                                          String.class,
                                                                          String.class,
                                                                          Float.TYPE,
                                                                          Float.TYPE);
        } catch (NoSuchMethodException ignored) {
        }
        try {
            mMethodReceive3f = mClientParent.getClass().getDeclaredMethod(mReceiveMethod,
                                                                          String.class,
                                                                          String.class,
                                                                          Float.TYPE,
                                                                          Float.TYPE,
                                                                          Float.TYPE);
        } catch (NoSuchMethodException ignored) {
        }
        try {
            mMethodReceiveStr = mClientParent.getClass().getDeclaredMethod(mReceiveMethod,
                                                                           String.class,
                                                                           String.class,
                                                                           String.class);
        } catch (NoSuchMethodException ignored) {
        }
        try {
            mMethodReceiveRaw = mClientParent.getClass().getDeclaredMethod(mReceiveMethod, OscMessage.class);
        } catch (NoSuchMethodException ignored) {
        }
        try {
            mMethodPing = mClientParent.getClass().getDeclaredMethod(mPingMethod);
        } catch (NoSuchMethodException ignored) {
        }

        prepareExitHandler();
        if (pClientParent instanceof PApplet) {
            PApplet p = (PApplet) pClientParent;
            p.registerMethod("dispose", this);
        }
    }

    private static int findAvailablePort() {
        int mPortTemp = MIN_PORT_NUMBER;
        while (!available(mPortTemp)) {
            mPortTemp++;
        }
        return mPortTemp;
    }

    public String ip() {
        return mIP;
    }

    public int port() {
        return mPort;
    }

    private static boolean available(int port) {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException ignored) {
                }
            }
        }
        return false;
    }

    public void connect() {
        OscMessage m = new OscMessage(Netzwerk.SERVER_CONNECT_PATTERN);
        m.add(mPort);
        // todo       // System.out.println("### also connect with a name `m.add(mSenderName`); so that IPs can be mapped to names.");
        mOSC.send(m, mBroadcastLocation);
    }

    public void disconnect() {
        OscMessage m = new OscMessage(Netzwerk.SERVER_DISCONNECT_PATTERN);
        m.add(mPort);
        mOSC.send(m, mBroadcastLocation);
    }

    public void ping() {
        OscMessage m = new OscMessage(Netzwerk.SERVER_PING_PATTERN);
        m.add(mPort);
        mOSC.send(m, mBroadcastLocation);
    }

    public void send_raw(OscMessage pMessage, NetAddress pNetAddress) {
        mOSC.send(pMessage, pNetAddress);
    }

    /* send message to server with 1, 2, or 3 floats as data or a string */

    public void send(String tag, float x) {
        OscMessage m = new OscMessage(getAddressPattern(tag));
        m.add(x);
        mOSC.send(m, mBroadcastLocation);
    }

    public void send(String tag, float x, float y) {
        OscMessage m = new OscMessage(getAddressPattern(tag));
        m.add(x);
        m.add(y);
        mOSC.send(m, mBroadcastLocation);
    }

    public void send(String tag, float x, float y, float z) {
        OscMessage m = new OscMessage(getAddressPattern(tag));
        m.add(x);
        m.add(y);
        m.add(z);
        mOSC.send(m, mBroadcastLocation);
    }

    public void send(String tag, String message) {
        OscMessage m = new OscMessage(getAddressPattern(tag));
        m.add(message);
        mOSC.send(m, mBroadcastLocation);
    }

    /* `send_direct` works like `send` except that it sends a message directly to the specified IP */

    public void send_direct(String IP, String tag, float x) {
        OscMessage m = new OscMessage(getAddressPattern(tag));
        m.add(x);
        NetAddress mLocal = new NetAddress(IP, mPort);
        OscP5.flush(m, mLocal);
    }

    public void send_direct(String IP, String tag, float x, float y) {
        OscMessage m = new OscMessage(getAddressPattern(tag));
        m.add(x);
        m.add(y);
        NetAddress mLocal = new NetAddress(IP, mPort);
        OscP5.flush(m, mLocal);
    }

    public void send_direct(String IP, String tag, float x, float y, float z) {
        OscMessage m = new OscMessage(getAddressPattern(tag));
        m.add(x);
        m.add(y);
        m.add(z);
        NetAddress mLocal = new NetAddress(IP, mPort);
        OscP5.flush(m, mLocal);
    }

    public void send_direct(String IP, String tag, String message) {
        OscMessage m = new OscMessage(getAddressPattern(tag));
        m.add(message);
        NetAddress mLocal = new NetAddress(IP, mPort);
        OscP5.flush(m, mLocal);
    }

    /* `spoof` works like `send` except that it use an arbitray sender */

    public void spoof(String sender, String tag, float x) {
        OscMessage m = new OscMessage(getAddressPattern(sender, tag));
        m.add(x);
        mOSC.send(m, mBroadcastLocation);
    }

    public void spoof(String sender, String tag, float x, float y) {
        OscMessage m = new OscMessage(getAddressPattern(sender, tag));
        m.add(x);
        m.add(y);
        mOSC.send(m, mBroadcastLocation);
    }

    public void spoof(String sender, String tag, float x, float y, float z) {
        OscMessage m = new OscMessage(getAddressPattern(sender, tag));
        m.add(x);
        m.add(y);
        m.add(z);
        mOSC.send(m, mBroadcastLocation);
    }

    /* `sneak` works like `send_direct` except that it send a message to `localhost` */

    public void sneak(String tag, float x) {
        send_direct(LOCALHOST, tag, x);
    }

    public void sneak(String tag, float x, float y) {
        send_direct(LOCALHOST, tag, x, y);
    }

    public void sneak(String tag, float x, float y, float z) {
        send_direct(LOCALHOST, tag, x, y, z);
    }

    public void sneak(String tag, String message) {
        send_direct(LOCALHOST, tag, message);
    }

    private String getAddressPattern(String pSenderName, String pTag) {
        return DELIMITER + pSenderName + DELIMITER + pTag;
    }

    private String getAddressPattern(String pTag) {
        return getAddressPattern(mSenderName, pTag);
    }

    private void receive(String name, String tag, float x) {
        try {
            mMethodReceive1f.invoke(mClientParent, name, tag, x);
        } catch (Exception ignored) {
        }
    }

    private void receive(String name, String tag, float x, float y) {
        try {
            mMethodReceive2f.invoke(mClientParent, name, tag, x, y);
        } catch (Exception ignored) {
        }
    }

    private void receive(String name, String tag, float x, float y, float z) {
        try {
            mMethodReceive3f.invoke(mClientParent, name, tag, x, y, z);
        } catch (Exception ignored) {
        }
    }

    private void receive(String name, String tag, String message) {
        try {
            mMethodReceiveStr.invoke(mClientParent, name, tag, message);
        } catch (Exception ignored) {
        }
    }

    private void receive_raw(OscMessage m) {
        try {
            mMethodReceiveRaw.invoke(mClientParent, m);
        } catch (Exception ignored) {
        }
    }

    private void receive_ping() {
        try {
            mMethodPing.invoke(mClientParent);
        } catch (Exception ignored) {
        }
    }

    public void oscEvent(OscMessage m) {
        if (m.checkAddrPattern(Netzwerk.SERVER_PING_PATTERN)) {
            if (mMethodPing != null) {
                receive_ping();
            }
        } else if (m.typetag().equalsIgnoreCase("f")) {
            if (mMethodReceive1f != null) {
                receive(getName(m.addrPattern()), getTag(m.addrPattern()), m.get(0).floatValue());
            }
        } else if (m.typetag().equalsIgnoreCase("ff")) {
            if (mMethodReceive2f != null) {
                receive(getName(m.addrPattern()),
                        getTag(m.addrPattern()),
                        m.get(0).floatValue(),
                        m.get(1).floatValue());
            }
        } else if (m.typetag().equalsIgnoreCase("fff")) {
            if (mMethodReceive3f != null) {
                receive(getName(m.addrPattern()),
                        getTag(m.addrPattern()),
                        m.get(0).floatValue(),
                        m.get(1).floatValue(),
                        m.get(2).floatValue());
            }
        } else if (m.typetag().equalsIgnoreCase("s")) {
            if (mMethodReceiveStr != null) {
                receive(getName(m.addrPattern()), getTag(m.addrPattern()), m.get(0).stringValue());
            }
        } else {
            if (mMethodReceiveRaw != null) {
                receive_raw(m);
            }
//            log("### ", "client couldn t parse message:");
//            log("### ", theOscMessage.toString());
        }
    }

    private String getTag(String pAddrPattern) {
        String[] mStrings = PApplet.split(pAddrPattern, DELIMITER);
        if (mStrings.length == 3) {
            return mStrings[2];
        } else if (mStrings.length == 2) {
            return mStrings[1];
        } else {
            log("### ", "ERROR-MALFORMED-NAME-TAG: " + pAddrPattern);
            return "ERROR-MALFORMED-NAME-TAG";
        }
    }

    private String getName(String pAddrPattern) {
        String[] mStrings = PApplet.split(pAddrPattern, DELIMITER);
        if (mStrings.length == 3) {
            return mStrings[1];
        } else if (mStrings.length == 2) {
            return ANONYM;
        } else {
            log("### ", "ERROR-MALFORMED-NAME-TAG: " + pAddrPattern);
            return "ERROR-MALFORMED-NAME-TAG";
        }
    }

    private void log(String prefix, String message) {
        if (SHOW_LOG) {
            System.out.println(prefix + "\t" + message);
        }
    }

    public void dispose() {
        log("###", "disconnecting client*");
        disconnect();
    }

    private void prepareExitHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                log("###", "disconnecting client*");
                disconnect();
            }
        }));
    }
}
