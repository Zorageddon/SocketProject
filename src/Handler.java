import java.io.*;
import java.net.*;

public class Handler extends Thread {
  private Socket clientSocket;

  public Handler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    try {
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      String input;

      while ((input = in.readLine()) != null) {
        //DO THINGS HERE
      }
    } catch (IOException e) {
      System.err.println("Error handling client: " + e.getMessage());
    }

    try {
      clientSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}