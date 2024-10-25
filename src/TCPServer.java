import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TCPServer {
  private static final int port = 6789;
  private static final Map<String, List<String>> msgQ = new ConcurrentHashMap<>();
  //merge two client lists into one <Name, Set of Topics>?
  private static final Map<String, Map<String, Mediator>> clientsByTopic = new ConcurrentHashMap<>();
  private static final Map<String, Mediator> activeClients = new ConcurrentHashMap<>();


  public static void main(String[] args) {
    clientsByTopic.put("WEATHER", new ConcurrentHashMap<>());
    clientsByTopic.put("NEWS", new ConcurrentHashMap<>());
    try (ServerSocket welcomeSocket = new ServerSocket(port)) {
      while (true) {
        new Mediator(welcomeSocket.accept()).start();
      }
    } catch (IOException e) {
      System.out.println("IOException creating welcomeSocket");
    }
  }


//--------------------------------------------------------------------------------------------------------------------\\


  private static class Mediator extends Thread {
    private final Socket clientSocket;
    private PrintWriter out;
    private String clientName;
    private boolean isActive;
    private boolean isPublisher;


    public Mediator(Socket clientSocket) {
      this.clientSocket = clientSocket;
      this.isPublisher = false;
    }

    @Override
    public void run() {
      try {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String message;

        while ((message = in.readLine()) != null) {
          String[] parts = message.split(",", 4);
          String one = parts[0].trim().substring(1);
          //messages where format places request in position 0
          if (one.equals("PUB") || one.equals("DISC>") || one.equals("RECONNECT")) {
            switch (one) {
              case "PUB":
                pub(parts[1].trim(), parts[2].trim().substring(0, (parts[2].trim().length() - 1)));
                break;
              case "RECONNECT":
                reconnect(parts[1].trim().substring(0, (parts[1].trim().length() - 1)));
                break;
              case "DISC>":
                isActive = false;
                out.println("<DISC_ACK>");
                break;
            }
          } else {
            //messages where format places request in position 1
            switch (parts[1].trim()) {
              case "CONN>":
                connect(parts[0].trim().substring(1));
                break;
              case "SUB":
                sub(parts[2].trim().substring(0, (parts[2].trim().length() - 1)));
                break;
              default:
                out.println("<ERROR: Invalid Request>");
            }
          }
        }
      } catch (IOException e) {
        System.err.println("Error handling client: " + e.getMessage());
      }
    }

    private void connect(String name) {
      isActive = true;
      clientName = name;
      activeClients.putIfAbsent(clientName, this);
      out.println("<CONN_ACK>");
    }

    private void pub(String topic, String message) {
      isPublisher = true;  //Don't love this fix for below problem as publishers will get pushed messages if they don't try to publish right away
      if (clientsByTopic.containsKey(topic) && clientsByTopic.get(topic).containsKey(clientName)) {
        clientsByTopic.get(topic).forEach((k, v) -> {
          if (v.isActive && !v.isPublisher) {
            v.out.println(message); //YOU PRINT TO PUBLISHERS TOO
          } else {
            msgQ.putIfAbsent(k, new ArrayList<>());
            msgQ.get(k).add(message);
          }
        });
      } else {
        out.println("<ERROR: Not Subscribed>");
      }
    }

    private void sub(String topic) {
      if (clientsByTopic.containsKey(topic)) {
        clientsByTopic.get(topic).putIfAbsent(clientName, this);
        out.println("<SUB_ACK>");
      } else {
        out.println("<ERROR: Subscription Failed - Subject Not Found>");
      }
    }

    private void reconnect(String name) {
      this.clientName = name;
      clientsByTopic.forEach((topic, nameMap) -> {
        if (nameMap.containsKey(clientName) && !nameMap.get(clientName).isActive) {
          nameMap.replace(clientName, this);
          out.println("<RECONNECT_ACK>");
        }
      });
      if (msgQ.containsKey(this.clientName)) {
        for (String msg : msgQ.get(clientName)) {
          out.println(msg);
        }
        msgQ.get(this.clientName).clear();
      }
    }
  }
}