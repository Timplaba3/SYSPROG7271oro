public interface IpConnectionListener {
    void onConnectionReady(IpConnection IpConnection/*, String msg*/);
    void onRecieveString(IpConnection IpConnection, String str);
    void onDisconnect(IpConnection IpConnection);
    void onException(IpConnection IpConnection, Exception e);
}
