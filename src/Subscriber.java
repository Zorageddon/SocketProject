import java.io.IOException;

public class Subscriber extends TCPClient {
  private String name;

  public Subscriber(String name) {
    this.name = name;
  }

  public void subscribe(String subject) {
    sendMessage("<" + name + ", SUB, " + subject + ">");
    System.out.println(readMsg()); //Should be <SUB_ACK>
  }

  @Override
  public void connect() {
    super.connect();
    sendMessage("<" + name + ", CONN>");
    System.out.println(readMsg()); //Should be <CONN_ACK>
  }

  public void reconnect() {
    super.connect();
    sendMessage("<RECONNECT, " + name + ">");
    getMsgs();
  }

  public void getMsgs() {
//    try {
//      while (in.ready()) {
//        String msg = readMsg();
//        System.out.println("For: " + name + "\n" + msg);
//      }
//    } catch (IOException e) {
//      System.out.println("IOException getting messages " + name);
//    }

    String msg;
    msg = readMsg();
    System.out.println("For: " + name + "\n" + msg);
  }

  @Override
  public void disconnect() {
    super.disconnect();
    System.out.println(name);
  }

}
