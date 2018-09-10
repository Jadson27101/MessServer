import sample.Message;

public interface TCPConnectionListener {
    void onConnectionReady(connectTCP connectTCP);
    void onReceiveString(connectTCP connectTCP, Message value);
    void onDisconnect(connectTCP connectTCP);
    void onException(connectTCP connectTCP, Exception e);
}
