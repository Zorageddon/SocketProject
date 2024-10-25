import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TCPServer {
  private static final int port = 6789;
  private static final String[] topics = {"WEATHER", "NEWS"};
  private static Map<String, List<String>> msgQ = new ConcurrentHashMap<>();
  //merge two client lists into one <Name, Set of Topics>?
  private static Map<String, Map<String, Mediator>> clientsByTopic = new ConcurrentHashMap<>();
  private static Map<String, Mediator> activeClients = new ConcurrentHashMap<>();


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
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;
    private boolean isActive;


    public Mediator(Socket clientSocket) {
      this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
      try {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String message;

        while ((message = in.readLine()) != null) {
          String[] parts = message.split(",", 4);
          String one = parts[0].trim().substring(1);
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
            switch (parts[1].trim()) {
              case "CONN>":
                connect(parts[0].trim());
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
      this.isActive = true;
      this.clientName = name;
      activeClients.putIfAbsent(this.clientName, this);
      out.println("<CONN_ACK>");
    }

    private void pub(String topic, String message) {
      if (clientsByTopic.containsKey(topic) && clientsByTopic.get(topic).containsKey(this.clientName)) {
        clientsByTopic.get(topic).forEach((k, v) -> {
          if (v.isAlive()) {
            v.out.println(message);
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
        clientsByTopic.get(topic).putIfAbsent(this.clientName, this);
        out.println("<SUB_ACK>");
      } else {
        out.println("<ERROR: Subscription Failed - Subject Not Found>");
      }
    }

    private void reconnect(String name) {
      this.clientName = name;
      if (activeClients.containsKey(this.clientName) && activeClients.get(this.clientName).isActive) {
        System.out.println("Client already connected");
      } else if (!activeClients.containsKey(this.clientName)) {
        connect(this.clientName);
      } else {
        activeClients.replace(this.clientName, this);
        if (clientsByTopic.get("WEATHER").containsKey(this.clientName)) {
          clientsByTopic.get("WEATHER").replace(this.clientName, this);
        }
        if (clientsByTopic.get("NEWS").containsKey(this.clientName)) {
          clientsByTopic.get("NEWS").replace(this.clientName, this);
        }
        out.println("<RECONNECT_ACK>");
      }
      if (msgQ.containsKey(this.clientName) && !msgQ.get(this.clientName).isEmpty()) {
        for (String msg : msgQ.get(clientName)) {
          out.println(msg);
        }
        msgQ.get(this.clientName).clear();
      }
    }
  }
}