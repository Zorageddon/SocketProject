import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TCPServer {
  private ArrayList<TCPClient> clients;

  public static void main(String[] args) throws Exception {
    TCPServer server = new TCPServer();

    try (ServerSocket welcomeSocket = new ServerSocket(6789)) {
      while (true) {
        Socket connectionSocket = welcomeSocket.accept();

        new Handler(connectionSocket).start();
      }
    }
  }
}