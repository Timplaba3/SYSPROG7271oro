import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.Vector;

public class IpCheckerClass{
    private static int checkIP(String ip){
        ProcessBuilder pb=new ProcessBuilder("java","-jar","sp4.jar", ip);
        pb.directory(new File("."));
        Process p= null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
        try {
           return p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            p.destroy();
            return 1;
        }

    }

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

        Vector rangeVector = new Vector();
        Vector rangeVectorSuccess = new Vector();

        int rangeDown = 1;
        int rangeUp;
        int rangeDownSuccess = 1;
        int rangeUpSuccess;

        boolean check = false;
        boolean checkSuccess = false;

        int i;

        for(i = 1  ; i < 255; i++){
            String ipToCheck = ip_str + i;
            if(debug) {
                System.out.println("Checking: " + ipToCheck);
            }
            if((checkIP(ipToCheck)) == 0){
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
        if((checkIP(ipToCheck)) == 0){
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
