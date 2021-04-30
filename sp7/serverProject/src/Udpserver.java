import com.google.gson.Gson;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Udpserver extends Thread {

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

    protected DatagramSocket socket = null;
    protected boolean isRunning;
    protected byte[] buffer = new byte[1024];
    private final ArrayList<String> results = new ArrayList<String>();
    private final ArrayList<Long> resultsBytes = new ArrayList<>();
    private String listOfFiles;
    private String dataPath = "./data";

    public Udpserver() throws IOException {
        socket = new DatagramSocket(25565);
        scanForFiles();
        System.out.println("Server running");
    }

    public void run() {
        isRunning = true;

        while (isRunning) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                String receivedData = new String(packet.getData(), 0, packet.getLength());
                analiseMessage(receivedData, address, port);
            } catch (IOException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }
        socket.close();
    }

    private void analiseMessage(String message, InetAddress address, int port){
        Message messageToAnalise = new Gson().fromJson(message, Message.class);
        int type = messageToAnalise.getType();
        String  stringMessage = messageToAnalise.getMessage();
        System.out.println("Received: " + message);
        if(type == 0){
            System.out.println("Received client hello message from " + address + ":" + port);
            System.out.println("Sending num of files: " + results.size());
            sendMessage("" + results.size(), address, port);
            System.out.println("Sending list of files: " + listOfFiles);
            sendMessage(listOfFiles, address, port);
        } else if(type == 1){
            int id = Integer.parseInt(stringMessage);
            System.out.println("Client chosen file № " + id);
            System.out.println("Sending file size: " + "{\"type\": \"2\", \"message\": " + resultsBytes.get(id) + "}");
            sendMessage("{\"type\": \"2\", \"message\": " + resultsBytes.get(id) + "}", address, port);
        }else if(type == 3){//Send data
            int id = Integer.parseInt(stringMessage);
            System.out.println("Sending file path to file №: " + id);
            sendMessage(dataPath + "/" + results.get(id), address, port);
        }
    }
    private void scanForFiles(){
        File[] files = new File(dataPath).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
                resultsBytes.add(file.length());
            }
        }
        listOfFiles = getFileList();
        System.out.println("Founded files: "+ listOfFiles);
    }

    private String getFileList(){
        String res = "";
        for(int i = 0; i < results.size() - 1; i++){
            res += (i + 1) + ") " + results.get(i) + "; ";
        }
        res += (results.size()) + ") " + results.get(results.size() - 1);
        //System.out.println(res);
        return res;
    }

    private void sendMessage(String message, InetAddress address, int port){
        System.out.println("Sending to " + address + ":" + port);
        byte[] byteArray = message.getBytes();
        DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}