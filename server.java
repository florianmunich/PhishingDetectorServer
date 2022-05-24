import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.util.stream.Collectors; 

public class server {
    public static final String folderForFiles = "ReceivedStats/";
    public static final int port = 8000;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/uploadFile", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server running! Listening on port " + Integer.toString(port));
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
          System.out.println("Request received");
            String filename = t.getRequestHeaders().get("filename").get(0);
            InputStream inputStream = t.getRequestBody();
            String statsToWrite = new BufferedReader(new InputStreamReader(inputStream))
            .lines().collect(Collectors.joining("\n"));

            //Create File
            try {
            File myFile = new File(folderForFiles + filename + ".txt");
            if (myFile.createNewFile()) {
                //doNothing
              } else {
                System.out.println("File already exists.");
              }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            //write text to file
            try {
                FileWriter myWriter = new FileWriter(folderForFiles + filename + ".txt");
                myWriter.write(statsToWrite);
                myWriter.close();
                System.out.println("Successfully wrote to the file: " + filename);
              } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
              }

            String response = "File sucessfully received";
            t.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

    }

}