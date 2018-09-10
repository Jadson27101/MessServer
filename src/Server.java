import sample.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server implements TCPConnectionListener{

    public static void main(String[] args) {
        new Server();
    }

    private final ArrayList<connectTCP> connections  = new ArrayList<>();
    private Server(){
        System.out.println("Server running...");
        try(ServerSocket serverSocket = new ServerSocket(8189)){
            while (true){
                try{
                    new connectTCP(this, serverSocket.accept());
                }catch (IOException e){
                    System.out.println("TCPConnection exception " + e);
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(connectTCP connectTCP) {
        connections.add(connectTCP);
    }

    @Override
    public synchronized void onReceiveString(connectTCP connectTCP, Message value) {
        sendObject(value);
    }

    @Override
    public synchronized void onDisconnect(connectTCP connectTCP) {
        connections.remove(connectTCP);
        System.out.println("Client disconnected: " + connectTCP);
    }

    @Override
    public synchronized void onException(connectTCP connectTCP, Exception e) {
        System.out.println("TCPConnections exception" + e);
    }
private void sendObject(Message message){
    System.out.println(message.getName() + ": "+message.getMessage());
    final int size = connections.size();
    for (int i = 0; i < size; i++){
        String s = String.valueOf(connections.get(i));
            if(s.equals(String.valueOf(message.getClientIP()))) {
                //System.out.println("flag");
                connections.get(i).sendMessage(message);
            }
        }
}
}
