public class Publisher extends TCPClient {
  private String name;
  private String topic;

  public Publisher(String name, String topic) {
    this.name = name;
    this.topic = topic;
  }

  public void publish(String topic, String message) {
    sendMessage("<" + name + ", PUB, " + topic + ", " + message + ">");
    String response = readMessage();
    if (response.startsWith("<E")) {
      System.out.println(response);
    }
  }

  @Override
  public void connect() {
    super.connect();
    sendMessage("<" + name + ", CONN>");
    System.out.println(readMessage()); //Should be <CONN_ACK>
    sendMessage("<" + name + ", SUB, " + topic + ">");
    System.out.println(readMessage()); //should be <SUB_ACK>
  }

}
