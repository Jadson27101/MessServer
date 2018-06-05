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

    public connectTCP(TCPConnectionListener eventListener, String IP, int port) throws IOException{
        this(eventListener, new Socket(IP, port));
    }

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
                        String message = in.readLine();
                        eventListener.onReceiveString(connectTCP.this, message);
                    }
                } catch (IOException e) {
                    eventListener.onException(connectTCP.this, e);
                } finally {
                    eventListener.onDisconnect(connectTCP.this);
                }
            }
        });
        rxThread.start();
    }
    public synchronized void sendMessage(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(connectTCP.this, e);
            disconnect();
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
       return "TCPConnection: " + socket.getInetAddress() + " " + socket.getPort();
    }
}
