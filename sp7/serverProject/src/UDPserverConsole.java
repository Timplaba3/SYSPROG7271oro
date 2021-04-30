import java.io.IOException;
import java.util.Scanner;

public class UDPserverConsole {
    public static void main(String args[]){
        Thread t = null;
        try {
            t = new Udpserver();
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true) {
            Scanner in = new Scanner(System.in);
            System.out.print("Input exit to exit: ");
            String c = in.nextLine();
            if(c.equals("exit")){
                t.interrupt();
                System.exit(0);
            }
        }
    }
}
