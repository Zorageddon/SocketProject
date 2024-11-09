public class Main { //for some reason when run, sub1 only prints one of the Qd msgs, and then doesn't DISC_ACK but does disconnect
                    //I think it's because I'm only reading in one line when reconnecting, and that screws with things somehow
                    //Now for some reason it only works on the second run of main???
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

    Thread.sleep(500);

    Subscriber sub1 = new Subscriber("sub1");
    sub1.connect();
    sub1.subscribe("WEATHER");

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

    sub1.disconnect(); //hanging after this first <DISC_ACK> might be something to do with threads not shutting down right
    sub2.disconnect();
    sub3.disconnect();

    Thread.sleep(500);

    pub1.publish("WEATHER", "Temperature will be normal tomorrow.");
    pub2.publish("NEWS", "The job market in Minnesota is great!");

    Thread.sleep(500);

    Subscriber.killPrinter();
  }
}
