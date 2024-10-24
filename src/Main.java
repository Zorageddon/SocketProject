public class Main {
  public static void main(String[] args) {



    Publisher pub = new Publisher("pub1", "WEATHER");
    pub.connect();
    pub.publish("WEATHER", "It's sunny!");
    pub.disconnect();

    Subscriber sub = new Subscriber("sub1");
    sub.connect();
    sub.subscribe("WEATHER");
    sub.disconnect();
  }
}
