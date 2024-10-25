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
    System.out.println(readMsg()); //Should be <RECONNECT_ACK>
  }

  public void getMsg() { //IS READ HERE BLOCKING?
    String msg;
    msg = readMsg();
    System.out.println(msg);
//    new Thread(() -> {
//      String msg;
//      while ((msg = readMsg()) != null) {
//        System.out.println(msg);
//      }
//    }).start();
  }

}
