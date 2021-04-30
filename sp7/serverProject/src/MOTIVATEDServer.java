import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;

public class MOTIVATEDServer implements IpConnectionListener {

    private class Message{
            public Message(String type, String message){
                this.message=message;
                this.type=Integer.parseInt(type);
            }
            public int getType(){
                return type;
            }
            public String getMessage(){
                return message;
            }
            private int type;
            private String message;
    }

    private final ArrayList<IpConnection> connections =new ArrayList<>();
    private final ArrayList<String> results = new ArrayList<String>();
    private final ArrayList<Long> resultsBytes = new ArrayList<>();
    private String listOfFiles;
    private String dataPath = "./data";

    private void serverStart(){
        System.out.println("Server running");
        scanForFiles();

        try(ServerSocket serverSocket=new ServerSocket(25565)){
            while(true){
                try{
                    new IpConnection(this, serverSocket.accept());
                }
                catch(IOException e){
                    System.out.println("Server exception: "+e);
                }
            }

        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    public static void main(String args[]) {
        Thread rxThread=new Thread(new Runnable() {
            @Override
            public void run() {
                new MOTIVATEDServer().serverStart();
        }});
        rxThread.start();
        while(true) {
            Scanner in = new Scanner(System.in);
            System.out.print("Input exit to exit: ");
            String c = in.nextLine();
            if(c.equals("exit")){
                rxThread.interrupt();
                System.exit(0);
            }
        }

    }

    private void scanForFiles(){
        File[] files = new File(dataPath).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
                resultsBytes.add(file.length()/4096 + 1);
            }
        }
        listOfFiles = getFileList();
        System.out.println("\nFounded files: "+ listOfFiles);
    }

    private String getFileList(){
        String res = "";
        for(int i = 0; i < results.size() - 1; i++){
            res += (i + 1) + ") " + results.get(i) + "; ";
        }
        res += (results.size()) + ") " + results.get(results.size() - 1);
        return res;
    }

    private void analiseMessage(IpConnection IpConnection, String message){
        Message messageToAnalise = new Gson().fromJson(message, Message.class);
        int type = messageToAnalise.getType();
        String  stringMessage = messageToAnalise.getMessage();
        System.out.println("Received: " + message);
        if(type == 1){
            int id = Integer.parseInt(stringMessage);
            System.out.println("Client chosen file № " + id);
            System.out.println("Sending number of blocks: " + "{\"type\": \"2\", \"message\": " + resultsBytes.get(id) + "}");
            IpConnection.sendString("{\"type\": \"2\", \"message\": " + resultsBytes.get(id) + "}");
        }else if(type == 3){//Send data
            System.out.println("Sending data");
            int id = Integer.parseInt(stringMessage);
            IpConnection.sendData(dataPath + "/" + results.get(id));
        }
    }

    @Override
    public void onConnectionReady(IpConnection IpConnection) {
        connections.add(IpConnection);
        System.out.println("Client come to server");
        System.out.println("Sending num of files: " + results.size());
        IpConnection.sendString("" + results.size());
        IpConnection.sendString(listOfFiles);
    }

    @Override
    public void onRecieveString(IpConnection IpConnection, String str) {
        analiseMessage(IpConnection, str);
    }

    @Override
    public void onDisconnect(IpConnection IpConnection) {
        connections.remove(IpConnection);
    }

    @Override
    public void onException(IpConnection IpConnection, Exception e) {
        System.out.println("Server exception: "+e);
    }
}
