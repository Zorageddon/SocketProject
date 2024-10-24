public class Publisher extends TCPClient {
  private String name;

  public Publisher(String name) {
    this.name = name;
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
    System.out.println(readMessage()); //Should be <SUB_ACK>
  }

}
