package meucci;

import java.io.*;
import java.net.*;

public class HttpServerMain {
    public static void main(String[] args) throws IOException {

        HttpServer server = new HttpServer(new ServerSocket(8080));
        server.start();
    }
}