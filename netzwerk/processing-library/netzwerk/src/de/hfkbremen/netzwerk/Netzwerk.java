package de.hfkbremen.netzwerk;

public class Netzwerk {

    public static String SERVER_DEFAULT_BROADCAST_IP = "localhost";
    public static int SERVER_DEFAULT_BROADCAST_PORT = 32000;
    public static String SERVER_PATTERN_CONNECT = "/server/connect";
    public static String SERVER_PATTERN_DISCONNECT = "/server/disconnect";
    public static String SERVER_PATTERN_PING = "/server/ping";
    public static String SERVER_PATTERN_CONNECT_SERVER = "/server/connect-server";

    public static boolean match(String s1, String equals) {
        return s1.equals(equals);
    }
}
