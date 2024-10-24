import javax.print.attribute.standard.Media;
import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer {
  private static final int port = 6789;
  private static final String[] topics = {"WEATHER", "NEWS"};
  private static Map<String, List<Mediator>> clientList = new HashMap<>();

  public static void main(String[] args) throws Exception {
    for (String topic : topics) {
      clientList.put(topic, new ArrayList<>());
    }
    try (ServerSocket welcomeSocket = new ServerSocket(port)) {
      while (true) {
        new Mediator(welcomeSocket.accept()).start();
      }
    } catch (IOException e) {
      System.out.println("IOException creating welcomeSocket");
    }
  }

  private static class Mediator extends Thread {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String name;
    private Set<String> subbed = new HashSet<>();



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
          String request = parts[1].trim();

          switch (request) {
            case "CONN>":
              name = parts[0].trim();
              out.println("<CONN_ACK>");
              break;
            case "SUB":
              handleSub(parts[2].trim().substring(0, (parts[2].trim().length() - 1)));
              break;
            case "PUB":
              handlePub(parts[2].trim(), parts[3].trim().substring(0, (parts[3].trim().length() - 1)));
              break;
            case "DISC>":
              out.println("<DISC_ACK>");
              break;
            default:
              out.println("<ERROR: Invalid Request>");
          }
        }
      } catch (IOException e) {
        System.err.println("Error handling client: " + e.getMessage());
      }
    }

    private void handlePub(String topic, String message) {
      if (subbed.contains(topic)) {
        synchronized (clientList) {
          for (Mediator client : clientList.get(topic)) {
            client.out.println("Message from " + name + " on " + topic + ": " + message);
          }
        }
      } else {
        out.println("<ERROR: Not Subscribed>");
      }
    }

    private void handleSub(String topic) {
      if(Arrays.asList(topics).contains(topic)) {
        subbed.add(topic);
        synchronized (clientList) {
          clientList.get(topic).add(this);
        }
        out.println("<SUB_ACK>");
      }  else {
        out.println("<ERROR: Subscription Failed - Subject Not Found>");
      }
    }

  }

}