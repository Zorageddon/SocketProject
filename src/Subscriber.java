public class Subscriber extends TCPClient {
  private String name;

  public Subscriber(String name) {
    this.name = name;
  }

  public void subscribe(String subject) {
    sendMessage("<" + name + ", SUB, " + subject + ">");
    System.out.println(readMessage()); //Should be <SUB_ACK>
  }

  @Override
  public void connect() {
    super.connect();
    sendMessage("<" + name + ", CONN>");
    System.out.println(readMessage()); //Should be <CONN_ACK>
  }

}
