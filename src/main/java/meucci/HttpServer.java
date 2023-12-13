package meucci;

import java.io.*;
import java.net.*;
import java.util.*;

public class HttpServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader in;
    private DataOutputStream out;

    public void startServer() throws IOException {
        this.serverSocket = new ServerSocket(8000);
        System.out.println("Server in ascolto sulla porta 8000");

        while (true) {
            this.clientSocket = serverSocket.accept();
            System.out.println("Client connected");

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new DataOutputStream(clientSocket.getOutputStream());

            String request = in.readLine();
            System.out.println("Request: " + request);
            String uri = request.split(" ")[1];
            System.out.println("URI: " + uri);

            String content = "";

            if (uri.equals("/")) {
                content = this.readFile("src/main/resources/index.html");
                this.sendResponse(out, content, "GET");
            } else {
                String dir = uri.replaceFirst("/", "");
                content = this.readFile("src/main/resources/" + dir + "/index.html");
                if (content.equals("404")) {
                    this.sendResponseError(out);

                } else {
                    this.sendResponse(out, content, "GET");
                }
            }

            this.close();
            System.out.println("Client disconnected");
        }
    }

    public String readFile(String path) {
        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            String html = "";

            while (scanner.hasNextLine()) {
                html += scanner.nextLine();
            }
            scanner.close();
            return html;
        } catch (FileNotFoundException e) {
            System.out.println("File non trovato");
            return "404";
        }
    }

    public void sendResponse(DataOutputStream out, String content) throws IOException {
        try {
            byte[] body = content.getBytes();
            int contentLength = body.length;
            out.write("HTTP/1.1 200 OK\r\n".getBytes());
            out.write("Content-Type: text/html\r\n".getBytes());
            out.write(("Content-Length: " + contentLength + "\r\n").getBytes());
            out.write("\r\n".getBytes());
            out.write(body);
        } catch (IOException e) {
            System.err.println("Errore nell'invio della risposta");
        }
    }

    public void sendResponseError(DataOutputStream out) throws IOException {
        try {
            byte[] body = readFile("src/main/resources/404.html").getBytes();
            int contentLength = body.length;
            out.write("HTTP/1.1 404 Not Found\r\n".getBytes());
            out.write("Content-Type: text/html\r\n".getBytes());
            out.write(("Content-Length: " + contentLength + "\r\n").getBytes());
            out.write("\r\n".getBytes());
            out.write(body);

        } catch (IOException e) {
            System.err.println("Errore nell'invio della risposta");
        }
    }

    /***/
    public void sendResponse(DataOutputStream out, String content, String code) throws IOException {
        try {
            if (code.equals("GET")) {
                byte[] body = content.getBytes();
                int contentLength = body.length;
                out.write("HTTP/1.1 200 OK\r\n".getBytes());
                out.write("Content-Type: text/html\r\n".getBytes());
                out.write(("Content-Length: " + contentLength + "\r\n").getBytes());
                out.write("\r\n".getBytes());
                out.write(body);
            } else {
                out.write("HTTP/1.1 404 Not Found\r\n".getBytes());
                out.write("Content-Type: text/html\r\n".getBytes());
                out.write("\r\n".getBytes());
                out.write("<h1>404 Not Found</h1>".getBytes());
            }

        } catch (IOException e) {
            System.err.println("Errore nell'invio della risposta");
        }
    }

    public void close() {
        try {
            this.in.close();
            this.out.close();
            this.clientSocket.close();
        } catch (IOException e) {
            System.err.println("Errore nella chiusura della connessione");
        }

    }

}
