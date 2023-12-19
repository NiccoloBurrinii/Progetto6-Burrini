package meucci;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    ServerSocket serverSocket;
    Socket client;
    DataOutputStream out;
    BufferedReader in;

    public HttpServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void start() throws IOException {
        while (true) {
            client = serverSocket.accept();
            (new ServerThread(client)).start();
        }
    }
}