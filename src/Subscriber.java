public class Subscriber extends TCPClient {
  private String name;
//  private String[] subscriptions;

  public Subscriber(String name) {
//    this.subscriptions = new String[]{"", ""};
    this.name = name;
  }

  public void subscribe(String subject) {
    sendMessage("<" + name + ", SUB, " + subject + ">");
    System.out.println(readMessage()); //Should be <SUB_ACK>

//    subject = subject.toUpperCase();
//    try {
//      if (subject.equals("WEATHER")) {
//        if (this.subscriptions[0].equals("WEATHER") || this.subscriptions[1].equals("WEATHER")) {
//          System.out.println("Already subscribed to Weather");
//        } else if (this.subscriptions[0].isEmpty() && this.subscriptions[1].equals("NEWS")) {
//          this.subscriptions[0] = "WEATHER";
//        } else if (this.subscriptions[0].equals("NEWS") && this.subscriptions[1].isEmpty()) {
//          this.subscriptions[1] = "WEATHER";
//        } else {
//          this.subscriptions[0] = "WEATHER";
//        }
//      } else if (subject.equals("NEWS")) {
//        if (this.subscriptions[0].equals("NEWS") || this.subscriptions[1].equals("NEWS")) {
//          System.out.println("Already subscribed to News");
//        } else if (this.subscriptions[0].isEmpty() && this.subscriptions[1].equals("WEATHER")) {
//          this.subscriptions[0] = "NEWS";
//        } else if (this.subscriptions[0].equals("WEATHER") && this.subscriptions[1].isEmpty()) {
//          this.subscriptions[1] = "NEWS";
//        } else {
//          this.subscriptions[0] = "NEWS";
//        }
//      } else {
//        throw new IllegalArgumentException();
//      }
//    } catch (IllegalArgumentException e) {
//      System.out.println("Please subscribe to either Weather or News");
//    }
  }

  @Override
  public void connect() {
    super.connect();
    sendMessage("<" + name + ", CONN>");
    System.out.println(readMessage()); //Should be <CONN_ACK>
  }

}
