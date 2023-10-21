package de.schlueter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class App {
  public static void main(String[] args) {

    try (ServerSocket serverSocket = new ServerSocket(8080)) {
      System.out.println("Server is listening on port 8080");
      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("Client connected");
        InputStream input = socket.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        OutputStream output = socket.getOutputStream();

        String line;
        int contentLength = -1;

        System.out.println("Request: ");
        while (!(line = in.readLine())
                    .isEmpty()) { // Headers end with an empty line
          System.out.println(line);
          if (line.startsWith("Content-Length: ")) {
            contentLength = Integer.parseInt(line.split(":")[1].trim());
          }
        }
        String body = "";

        // Read the body if Content-Length is present
        if (contentLength > 0) {
          char[] bodyChars = new char[contentLength];
          in.read(bodyChars, 0, contentLength);
          body = new String(bodyChars);
          System.out.println("Body: " + body);
        }

        String responseBody = "You sent: " + body;
        String responseHeaders = "HTTP/1.1 200 OK\r\n"
                                 + "Content-Type: text/plain\r\n"
                                 + "Content-Length: " + responseBody.length() +
                                 "\r\n\r\n";

        output.write(responseHeaders.getBytes());
        output.write(responseBody.getBytes());

        System.out.println("Request end");
        socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
