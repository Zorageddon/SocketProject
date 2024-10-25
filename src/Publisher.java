public class Publisher extends TCPClient {
  private String name;
  private String topic;

  public Publisher(String name, String topic) {
    this.name = name;
    this.topic = topic;
  }
//THIS BLOCKS WHEN NO ERROR RESPONSE IS PUSHED
  public void publish(String topic, String message) {
    sendMessage("<PUB, " + topic + ", " + message + ">");
//    String response = readMsg();  //BLOCKS WHICH IS ANNOYING
//    if (response.startsWith("<E")) {
//      System.out.println(response);
//    }
  }

  @Override
  public void connect() {
    super.connect();
    sendMessage("<" + name + ", CONN>");
    System.out.println(readMsg()); //Should be <CONN_ACK>
    sendMessage("<" + name + ", SUB, " + topic + ">");
    System.out.println(readMsg()); //should be <SUB_ACK>
  }

  @Override
  public void disconnect() {
    super.disconnect();
    System.out.println(name);
  }

}
