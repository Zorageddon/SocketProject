public class Main {

  public static void main(String[] args) throws InterruptedException {
    Subscriber.printer();

    Publisher pub1 = new Publisher("pub1", "WEATHER");
    Publisher pub2 = new Publisher("pub2", "NEWS");
    pub1.connect();
    pub2.connect();

    Thread.sleep(500);

    pub1.publish("WEATHER", "It is sunny today!");
    pub2.publish("NEWS", "Breaking news!");
    pub1.publish("WEATHER", "It is going to rain tomorrow.");
    pub1.publish("SPORTS", "Football match update."); //ERROR HANDLING 2 (publishing to non-existent subject)

    Thread.sleep(500);

    Subscriber sub1 = new Subscriber("sub1");
    sub1.connect();
    sub1.subscribe("WEATHER");
    sub1.subscribe("SPORTS"); //ERROR HANDLING 1 (subscribing to non-existent subject)

    Thread.sleep(500);

    Subscriber sub2 = new Subscriber("sub2");
    sub2.connect();
    sub2.subscribe("NEWS");

    Thread.sleep(500);

    Subscriber sub3 = new Subscriber("sub3");
    sub3.connect();
    sub3.subscribe("NEWS");
    sub3.subscribe("WEATHER");

    Thread.sleep(500);

    pub1.publish("WEATHER", "Heavy rain is expected tomorrow.");

    Thread.sleep(500);

    pub2.publish("NEWS", "Election results are out!");

    Thread.sleep(500);

    sub1.disconnect();
    sub2.disconnect();
    sub3.disconnect();

    Thread.sleep(500);

    pub1.publish("WEATHER", "Temperature will be normal tomorrow.");
    pub2.publish("NEWS", "The job market in Minnesota is great!");

    Thread.sleep(500);

    sub1.reconnect();
    sub2.reconnect();

    Thread.sleep(500);

    pub1.publish("WEATHER", "Snow tomorrow!");

    sub1.disconnect();
    sub2.disconnect();
    pub1.disconnect();
    pub2.disconnect();

    Subscriber.killPrinter();
  }

}