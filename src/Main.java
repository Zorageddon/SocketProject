public class Main { //for some reason when run, sub1 only prints one of the Qd msgs, and then doesn't DISC_ACK but does disconnect
                    //I think it's because I'm only reading in one line when reconnecting, and that screws with things somehow
  public static void main(String[] args) {
    Publisher pub = new Publisher("pub1", "WEATHER");
    pub.connect();               // Connect publisher

    Subscriber sub = new Subscriber("sub1");
    sub.connect();               // Connect subscriber
    sub.subscribe("WEATHER");    // Subscribe to topic
    sub.disconnect();
    pub.publish("WEATHER", "It's sunny!"); // Publish message
    pub.publish("WEATHER", "It's rainy!"); // Publish message
    sub.reconnect();
    sub.getMsgs();               // Start listening for messages

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
