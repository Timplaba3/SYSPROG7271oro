import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;


public class IpConnection {

    private final Socket socket;
    private final Thread rxThread;
    private final IpConnectionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public IpConnection(IpConnectionListener eventListener, String host, int port) throws IOException {
        this(eventListener, new Socket(host,port));
    }
    public IpConnection(IpConnectionListener eventListener, Socket socket) throws IOException {
        this.socket=socket;
        this.eventListener=eventListener;

        in=new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        rxThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //System.out.println();
                    eventListener.onConnectionReady(IpConnection.this/*, in.readLine()*/);
                    while(!rxThread.isInterrupted()){
                        eventListener.onRecieveString(IpConnection.this, in.readLine());//POLUCHENIE STROKI
                    }

                }catch (IOException e){
                    eventListener.onException(IpConnection.this, e);
                }finally{
                    eventListener.onDisconnect(IpConnection.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized void sendString(String value){
        try {
            //System.out.println(value);
            out.write(value+"\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(IpConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(IpConnection.this, e);
        }
    }

    public synchronized void sendData(String path){//добавить какой размер будет приниматься и тд
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
        int t;

        while ((t = bis.read()) != -1){
            out.write(t);
        }
            out.flush();
            disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    public synchronized void setSettings(){

    }

}
