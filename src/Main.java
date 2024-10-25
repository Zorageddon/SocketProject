public class Main { //for some reason when run, sub1 only prints one of the Qd msgs, and then doesn't DISC_ACK but does disconnect
                    //I think it's because I'm only reading in one line when reconnecting, and that screws with things somehow
                    //Now for some reason it only works on the second run of main???
  public static void main(String[] args) {
    Publisher pub = new Publisher("pub1", "WEATHER");
    pub.connect();

    Subscriber sub1 = new Subscriber("sub1");
    sub1.connect();
    sub1.subscribe("WEATHER");
    sub1.disconnect();
    Subscriber sub2 = new Subscriber("sub2");
    sub2.connect();
    sub2.subscribe("WEATHER");
    sub2.getMsgs();
    pub.publish("WEATHER", "It's sunny!");
    pub.publish("WEATHER", "It's rainy!");
    sub2.disconnect();
    sub1.reconnect();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    sub1.disconnect();
    pub.disconnect();
  }
}
