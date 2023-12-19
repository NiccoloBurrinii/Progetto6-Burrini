package meucci;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;

public class ServerThread extends Thread {

    private DataOutputStream out;
    private BufferedReader in;
    private InputStreamReader inStream;

    public ServerThread(final Socket client) throws IOException {
        out = new DataOutputStream(client.getOutputStream());
        inStream = new InputStreamReader(client.getInputStream());
        in = new BufferedReader(inStream);
    }

    @Override
    public void run() {
        String request = "";

        try {
            request = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Request:" + request);

        if (request.split(" ")[0].contains("GET")) {
            String path = request.split(" ")[1].split(" ")[0];
            System.out.println(path);

            if (isFile(path)) {
                String fileName = getFileName(path).trim();
                String fileType = fileName.split("\\.")[1].trim();

                switch (fileType) {
                    case "html":
                    case "scss":
                    case "css":
                    case "js":
                        try {
                            sendResponse(fileType, "text", readFile(path));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "png":
                    case "jpeg":
                    case "jpg":
                    case "gif":
                    case "webp":
                        try {
                            sendResponse(fileType, "image", readFile(path));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        try {
                            sendErrorPage();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            } else {
                try {
                    byte[] body = readFile(path.trim() + "/index.html");
                    if (body == null)
                        sendErrorPage();
                    else
                        sendResponse("html", "text", body);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendResponse(final String extension, final String contentType, final byte[] body) throws IOException {

        out.write("HTTP/1.1 200 OK\r\n".getBytes());
        out.write(("Content-Type: " + contentType + "/" + extension + "\r\n").getBytes());
        out.write(("Content-Length:" + body.length + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(body);
        out.write("\r\n".getBytes());

        out.close();
        in.close();

    }

    private boolean isFile(final String path) {
        final String[] strings = path.split("/");
        try {
            return strings[strings.length - 1].split("\\.")[1] != null;
        } catch (Exception e) {
            return false;
        }
    }

    private String getFileName(final String path) {
        String[] strings = path.split("/");
        return strings[strings.length - 1];
    }

    private void sendErrorPage() throws IOException {
        final byte[] contentBody = readFile("404.html");
        out.write("HTTP/1.0 404 Page not found\r\n".getBytes());
        out.write("Content-Type: text/html\r\n".getBytes());
        out.write(("Content-Length:" + contentBody.length + "\r\n").getBytes());
        out.write("\r\n".getBytes());
        out.write(contentBody);
        out.write("\r\n".getBytes());

    }

    private byte[] readFile(String filePath) throws IOException {
        byte[] body;
        try {
            body = Files.readAllBytes(new File("src/main/resources/" + filePath.trim()).toPath());
        } catch (Exception e) {
            return null;
        }
        return body;

    }
}