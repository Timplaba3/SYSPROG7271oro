import java.io.IOException;
import java.net.InetAddress;

public class PingClass {
    public static void main(String[] args) throws IOException
    {
        if(args[0]!=""){
            InetAddress hostToCheck = InetAddress.getByName(args[0]);
            if(hostToCheck.isReachable(1000)){
                System.exit(0);
            }else {
                System.exit(1);
            }
        }
        System.exit(1);
    }
}
