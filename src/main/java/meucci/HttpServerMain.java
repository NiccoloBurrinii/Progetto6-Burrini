package meucci;

public class HttpServerMain {
    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        try {
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}