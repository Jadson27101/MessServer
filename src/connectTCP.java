import sample.Message;

import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.Charset;

public class connectTCP {
    private final Socket socket;
    private final Thread rxThread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final  TCPConnectionListener eventListener;
    
    public connectTCP(TCPConnectionListener eventListener,Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(connectTCP.this);
                    while (!rxThread.isInterrupted()){
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        Message m = (Message) ois.readObject();
                        //String message = in.readLine();
                        eventListener.onReceiveString(connectTCP.this, m);
                    }
                } catch (IOException e) {
                    eventListener.onException(connectTCP.this, e);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    eventListener.onDisconnect(connectTCP.this);
                }
            }
        });
        rxThread.start();
    }
    public synchronized void sendSysMessage(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(connectTCP.this, e);
            disconnect();
        }
    }
    public synchronized void sendMessage(Message message){
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            //objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(connectTCP.this, e);
        }
    }
    @Override
    public String toString(){
       return String.valueOf(socket.getInetAddress());
    }
}
