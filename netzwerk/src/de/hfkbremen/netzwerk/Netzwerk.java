package de.hfkbremen.netzwerk;

public class Netzwerk {

    public static String SERVER_DEFAULT_BROADCAST_IP = "d3Book.local";
    public static int SERVER_DEFAULT_BROADCAST_PORT = 32000;
    public static String SERVER_CONNECT_PATTERN = "/server/connect";
    public static String SERVER_DISCONNECT_PATTERN = "/server/disconnect";

    public static boolean match(String s1, String equals) {
        return s1.equals(equals);
    }

}
