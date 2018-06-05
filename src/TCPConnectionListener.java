public interface TCPConnectionListener {
    void onConnectionReady(connectTCP connectTCP);
    void onReceiveString(connectTCP connectTCP, String value);
    void onDisconnect(connectTCP connectTCP);
    void onException(connectTCP connectTCP, Exception e);
}
