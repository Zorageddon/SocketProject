import java.util.concurrent.ConcurrentLinkedQueue;

public class Subscriber extends TCPClient {

  private final String name;
  private static ConcurrentLinkedQueue<String> msgs = new ConcurrentLinkedQueue<>();

  public static void printer() {
    new Thread(() -> {
      while (true) {
        if (!msgs.isEmpty()) {
          String msg = msgs.poll();
          if (msg != null) {
            System.out.println(msg);
          }
        }
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }).start();
  }

  public Subscriber(String name) {
    this.name = name;
  }

  public void subscribe(String subject) {
    sendMessage("<" + name + ", SUB, " + subject + ">");
    System.out.println(readMessage()); //Should be <SUB_ACK>
    getMsgs();
  }

  @Override
  public void connect() {
    super.connect();
    sendMessage("<" + name + ", CONN>");
    System.out.println(readMessage()); //Should be <CONN_ACK>
  }

  public void reconnect() {
    super.connect();
    sendMessage("<RECONNECT, " + name + ">");
    String response;
    if ((response = readMessage()).startsWith("<R")) {
      System.out.println(response);
      getMsgs();
    }
  }

  public void getMsgs() {
    new Thread(() -> {
      while (true) {
        String msg = readMessage();
        if (msg != null) {
          msgs.add(msg);
        }
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }).start();
  }

}
