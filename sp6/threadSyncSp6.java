import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.Semaphore;

class PingCheckerClass implements Runnable {

    private int[] array;
    private int start;
    private int end;
    private String ip;
    private boolean debug;
    private Semaphore mutex;

    public PingCheckerClass(int[] array, int start, int end, String ip, Semaphore mutex, boolean debug) {
        this.array = array;
        this.start = start;
        this.end = end;
        this.ip = ip;
        this.mutex = mutex;
        this.debug = debug;
    }

    public static int ping(String ip) throws IOException
    {
        InetAddress hostToCheck = InetAddress.getByName(ip);
        if(hostToCheck.isReachable(1000)){
            return 0;
        }else {
            return 1;
        }
    }

    public void run() {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = start; i <= end; i++) {
            String ipToCheck = ip + i;
            try {
                array[i] = ping(ipToCheck);
                if(debug) {
                    String temp;
                    if(array[i] == 0){
                        temp = "ping successfully";
                    }else{
                        temp = "ping unsuccessfully";
                    }
                    System.out.println("Checking ip: " + ipToCheck + " - " + temp);
                }
            } catch (IOException e) {
                System.out.println("Unexpected error");
                mutex.release();
                return;
            }
        }
        mutex.release();
    }
}

public class threadSyncSp6 {

    private static String writeVec(int rangeDown, int rangeUp){
        if(!(rangeDown == rangeUp)){
            return rangeDown + " - " + (rangeUp);
        }else{
            return "" + rangeDown;
        }
    }

    public static void main(String[] args)
    {
        boolean debug = false;
        if(args.length != 0 && args[0] == "-debug"){
            debug = true;
        }
        //debug = true;
        InetAddress ip = null;
        Scanner in = new Scanner(System.in);
        System.out.print("Input an ip: ");
        String ipIn = in.nextLine();
        try {
            ip = InetAddress.getByName(ipIn);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        byte[] clearIp = ip.getAddress();
        clearIp[3] = 0;
        try {
            ip = InetAddress.getByAddress(clearIp);
        } catch (UnknownHostException e) { }

        String ip_str = ip.getHostAddress();
        ip_str = ip_str.substring(0, ip_str.length() - 1);

        int[] pingRes = new int[256];

        Semaphore mutex = new Semaphore(1);

        Thread thread1 = new Thread(new PingCheckerClass(pingRes, 0, 64, ip_str, mutex, debug));
        Thread thread2 = new Thread(new PingCheckerClass(pingRes, 65, 129, ip_str, mutex, debug));
        Thread thread3 = new Thread(new PingCheckerClass(pingRes, 130, 194, ip_str, mutex, debug));
        Thread thread4 = new Thread(new PingCheckerClass(pingRes, 195, 255, ip_str, mutex, debug));

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Vector rangeVector = new Vector();
        Vector rangeVectorSuccess = new Vector();

        int rangeDown = 1;
        int rangeUp;
        int rangeDownSuccess = 1;
        int rangeUpSuccess;

        boolean check = false;
        boolean checkSuccess = false;

        int i;
        debug = false;
        for(i = 1  ; i < 255; i++){
            String ipToCheck = ip_str + i;
            if(debug) {
                System.out.println("Checking: " + ipToCheck);
            }
            if(pingRes[i] == 0){
                if(debug) {
                    System.out.println("Ping successfully\n");
                }
                if(check){
                    rangeUp = i - 1;
                    rangeVector.add(writeVec(rangeDown, rangeUp));
                    check = false;
                }
                if(!checkSuccess){
                    rangeDownSuccess = i;
                    checkSuccess = true;
                }
            }else{
                if(debug) {
                    System.out.println("Ping unsuccessfully\n");
                }
                if(checkSuccess) {
                    rangeUpSuccess = i - 1;
                    rangeVectorSuccess.add(writeVec(rangeDownSuccess, rangeUpSuccess));
                    checkSuccess = false;
                }
                if(!check){
                    rangeDown = i;
                    check = true;
                }
            }
        }
        String ipToCheck = ip_str + i;
        if(pingRes[i] == 0){
            rangeUpSuccess = i - 1;
            rangeVectorSuccess.add(writeVec(rangeDownSuccess, rangeUpSuccess));
        }else{
            rangeUp = i - 1;
            rangeVector.add(writeVec(rangeDown, rangeUp));
        }

        if(!rangeVector.isEmpty()) {
            System.out.println("Unavailable ip: " + rangeVector);
        }
        if(!rangeVectorSuccess.isEmpty()) {
            System.out.println("Available ip: " + rangeVectorSuccess);
        }
    }
}
