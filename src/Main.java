public class Main {
  public static void main(String[] args) {
    Publisher pub = new Publisher("pub1", "WEATHER");
    pub.connect();               // Connect publisher

    Subscriber sub = new Subscriber("sub1");
    sub.connect();               // Connect subscriber
    sub.subscribe("WEATHER");    // Subscribe to topic

    pub.publish("WEATHER", "It's sunny!"); // Publish message

    sub.getMsg();               // Start listening for messages

    // Wait a moment to ensure the message is received
    try {
      Thread.sleep(1000); // Adjust as necessary
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    sub.disconnect();           // Disconnect subscriber
    pub.disconnect();           // Disconnect publisher
  }
}
