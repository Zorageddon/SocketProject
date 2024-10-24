import java.io.*;
import java.net.*;

public class TCPClient {
  protected Socket clientSocket;
  protected int port = 6789;
  protected InetAddress serverAddress;
  protected PrintWriter out;
  protected BufferedReader in;

  public TCPClient() {
    try {
      this.serverAddress = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      System.out.println("Unknown Host");
    }
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

  public void disconnect(Socket socket) {
    out.println("<DISC>");
    try {
      socket.close();
    } catch (IOException e) {
      System.out.println("IOException closing clientSocket");
    }
  }

  public String readMessage() {
    String output = null;
    try {
      output = in.readLine();
    } catch (IOException e) {
      System.out.println("IOException reading message");
    }
    return output;
  }

  public void sendMessage(String message) {
    out.println(message);
  }

}