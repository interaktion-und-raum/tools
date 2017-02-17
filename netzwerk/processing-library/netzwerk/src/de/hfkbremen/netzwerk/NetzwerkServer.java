package de.hfkbremen.netzwerk;

import netP5.NetAddress;
import netP5.NetAddressList;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

import java.util.HashMap;

import static de.hfkbremen.netzwerk.Netzwerk.SERVER_CONNECT_PATTERN;
import static de.hfkbremen.netzwerk.Netzwerk.SERVER_DEFAULT_BROADCAST_PORT;
import static de.hfkbremen.netzwerk.Netzwerk.SERVER_DISCONNECT_PATTERN;

public class NetzwerkServer {

    public static boolean SHOW_LOG = true;
    private final NetAddressList mNetAddressList = new NetAddressList();
    private final String mConnectPattern;
    private final String mDisconnectPattern;
    private final OscMessage[] mMessages;
    private int mMessagePtr;
    private final HashMap<String, String> mAddressMap = new HashMap<>();
    private final String mIP;
    private OscP5 mOSC;

    public NetzwerkServer() {
        this(SERVER_DEFAULT_BROADCAST_PORT, SERVER_CONNECT_PATTERN, SERVER_DISCONNECT_PATTERN);
    }

    public NetzwerkServer(int pListeningPort, String pConnectPattern, String pDisconnectPattern) {
        mConnectPattern = pConnectPattern;
        mDisconnectPattern = pDisconnectPattern;

        final int mMaxMessages = 32;
        mMessagePtr = 0;
        mMessages = new OscMessage[mMaxMessages];
        for (int i = 0; i < mMessages.length; i++) {
            mMessages[i] = new OscMessage("");
        }

        mOSC = new OscP5(this, pListeningPort);

        mIP = mOSC.ip();
        log("+++", "server is @ " + mIP + " + listening on port " + pListeningPort);
        // todo write logfile of number if messages to HD / once a second
    }

    public String ip() {
        return mIP;
    }

    public synchronized void purge_clients() {
        mAddressMap.clear();
        for (int i = 0; i < mNetAddressList.size(); i++) {
            disconnect(mNetAddressList.get(i).address(), mNetAddressList.get(i).port());
        }
    }

    public synchronized OscMessage[] messages() {
        OscMessage[] mMessages = new OscMessage[this.mMessages.length];
        for (int i = 0; i < this.mMessages.length; i++) {
            int j = (i + mMessagePtr) % this.mMessages.length;
            mMessages[i] = this.mMessages[j];
        }
        return mMessages;
    }

    public NetAddressList clients() {
        return mNetAddressList;
    }

    public synchronized void oscEvent(OscMessage pOscMessage) {
        /* check if the address pattern fits any of our patterns, accepting `int` and `float` as typetag */
        if (pOscMessage.addrPattern().equals(mConnectPattern)) {
            if (pOscMessage.checkTypetag("i")) {
                connect(pOscMessage.netAddress().address(), pOscMessage.get(0).intValue());
            } else if (pOscMessage.checkTypetag("f")) {
                connect(pOscMessage.netAddress().address(), (int) pOscMessage.get(0).floatValue());
            }
        } else if (pOscMessage.addrPattern().equals(mDisconnectPattern)) {
            if (pOscMessage.checkTypetag("i")) {
                disconnect(pOscMessage.netAddress().address(), pOscMessage.get(0).intValue());
            } else if (pOscMessage.checkTypetag("f")) {
                disconnect(pOscMessage.netAddress().address(), (int) pOscMessage.get(0).floatValue());
                //            } else {
                //                disconnect(pOscMessage.netAddress().address(), 12000);
            }
        } else {
            /*
             * if pattern matching was not successful, then broadcast the incoming
             * message to all addresses in the netAddresList.
             */
            mOSC.send(pOscMessage, mNetAddressList);

            // @todo try to connect with first message
            /* try to connect name and IP:port */
            //            System.out.println("### trying to connect name and IP:port.");
            //            /* check if the message has a name+tag. if yes check the the 'name' matches with an IP in the address map */
            //            final String mAddress = pOscMessage.netAddress().address() + ":" + pOscMessage.netAddress().port();
            //            String mValue = mAddressMap.get(mAddress);
            //            if (mValue == null) {
            //                System.out.println("### address not yet in address map");
            //                mAddressMap.put(mAddress, "foobar");
            //            } else {
            //                System.out.println("### found " + mValue + "in address map.");
            //            }
        }

        /* store messages */
        mMessages[mMessagePtr] = pOscMessage;
        mMessagePtr++;
        mMessagePtr %= mMessages.length;
    }

    private void connect(String theIPaddress, int pBroadcastPort) {
        if (!mNetAddressList.contains(theIPaddress, pBroadcastPort)) {
            mNetAddressList.add(new NetAddress(theIPaddress, pBroadcastPort));
            log("###", "adding " + theIPaddress + " to the list.");
        } else {
            log("---", theIPaddress + " is already connected.");
        }
        log("###", "currently there are " + mNetAddressList.list().size() + " remote locations connected.");
        log("###", mNetAddressList.list() + "");
    }

    private void disconnect(String theIPaddress, int pBroadcastPort) {
        if (mNetAddressList.contains(theIPaddress, pBroadcastPort)) {
            mNetAddressList.remove(theIPaddress, pBroadcastPort);
            log("###", "removing " + theIPaddress + " from the list.");
        } else {
            log("---", theIPaddress + " is not connected.");
        }
        log("###", "currently there are " + mNetAddressList.list().size());
    }

    private void log(String p, String m) {
        if (SHOW_LOG) {
            System.out.println(p + "\t" + m);
        }
    }

    public static String getAsString(Object[] theObject) {
        StringBuilder s = new StringBuilder();
        for (Object o : theObject) {
            if (o instanceof Float) {
                String str = PApplet.nfc((Float) o, 2);
                s.append(str).append(" | ");
            } else if (o instanceof Integer) {
                String str = o.toString();
                s.append(str).append(" | ");
            }
        }
        return s.toString();
    }
}
