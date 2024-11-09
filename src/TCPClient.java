import java.io.*;
import java.net.*;

public class TCPClient {
  private Socket clientSocket;
  private int port = 6789;
  private InetAddress serverAddress;
  private PrintWriter out;
  private BufferedReader in;


  public TCPClient() {
    try {
      this.serverAddress = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      System.out.println("Unknown Host");
    }
  }

  public void sendMessage(String message) {
    out.println(message);
  }

  public String readMessage() {
    String output = null;
    try {
      clientSocket.setSoTimeout(1000);
      output = in.readLine();
    } catch (SocketTimeoutException ignored) {

    } catch (IOException e) {
      System.out.println("IOException reading message");
    }
    return output;
  }

  public void connect() {
    try {
      clientSocket = new Socket(serverAddress, port);
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    } catch (IOException e) {
      System.out.println("IOException creating clientSocket");
    }

  }

  public void disconnect() {
    sendMessage("<DISC>");
    String response = readMessage();
    if (response.startsWith("<D")) {
      try {
        System.out.println(response);
        clientSocket.close();
      } catch (IOException e) {
        System.out.println("IOException closing clientSocket");
      }
    }
  }
}