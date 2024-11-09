public class Publisher extends TCPClient {
  private final String name;
  private final String topic;

  public Publisher(String name, String topic) {
    this.name = name;
    this.topic = topic;
  }
//THIS BLOCKS WHEN NO ERROR RESPONSE IS PUSHED
  public void publish(String topic, String message) {
    sendMessage("<PUB, " + topic + ", " + message + ">");
    String response = readMessage();
    if (response != null && response.startsWith("<E")) {
      System.out.println(response);
    }
  }

  @Override
  public void connect() {
    super.connect();
    sendMessage("<" + name + ", CONN>");
    System.out.println(readMessage()); //Should be <CONN_ACK>
    sendMessage("<" + name + ", SUB, " + topic + ">");
//    System.out.println(readMsg()); //should be <SUB_ACK>
  }

}
