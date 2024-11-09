import java.util.concurrent.ConcurrentLinkedQueue;

public class Subscriber extends TCPClient {
  //STATIC ELEMENTS
  private static boolean printerActive = true;
  private static final ConcurrentLinkedQueue<String> msgs = new ConcurrentLinkedQueue<>();

  public static void printer() {
    new Thread(() -> {
      while (printerActive) {
        if (!msgs.isEmpty()) {
          String msg = msgs.poll();
          if (msg != null) {
            System.out.println(msg);
          }
        }
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }).start();
  }

  public static void killPrinter() {
    printerActive = false;
  }

//--------------------------------------------------------------------------------------------------------------------\\
  //INSTANCE ELEMENTS
  private final String name;
  private boolean listening;
  private Thread msgThread;

  public Subscriber(String name) {
    this.name = name;
    this.listening = false;
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
    if (!listening) {
      listening = true;
      msgThread = new Thread(() -> {
        while (listening) {
          String msg = readMessage();
          if (msg != null) {
            msgs.add(msg);
          }
        }
      });
      msgThread.start();
    }
  }

  @Override
  public void disconnect() {
    listening = false;
    try {
      msgThread.interrupt();
      msgThread.join();
    } catch (InterruptedException e) {
      System.out.println(e.getMessage());
    }
    super.disconnect();
  }

}
