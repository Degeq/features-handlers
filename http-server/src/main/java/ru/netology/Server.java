package ru.netology;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private int portNum;
    private ServerSocket serverSocket = null;
    private ExecutorService threadPool = Executors.newFixedThreadPool(64);
    private List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();

    public void listen (int portNum) throws IOException {
        this.portNum = portNum;
        serverSocket = new ServerSocket(portNum);

        while (true) {
            try {
                var socket = serverSocket.accept();
                threadPool.submit(() -> requestProcessing(socket));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private void requestProcessing(Socket socket) {
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        )  {

            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                return;
            }

            var request = new Request(parts[0], parts[1], parts[2]);

            if (!handlers.containsKey(request.getRequestMethod())) {
                printNotFoundError(out);
                return;
            }

            var method = handlers.get(request.getRequestMethod());

            if (!method.containsKey(request.getRequestPath())) {
                printNotFoundError(out);
                return;
            }

            var handler = method.get(request.getRequestPath());

            handler.handle(request, out);
            return;

//            final var path = parts[1];
//            if (!validPaths.contains(path)) {
//                out.write((
//                        "HTTP/1.1 404 Not Found\r\n" +
//                                "Content-Length: 0\r\n" +
//                                "Connection: close\r\n" +
//                                "\r\n"
//                ).getBytes());
//                out.flush();
//                return;
//            }
//
//            final var filePath = Path.of(".", "/public", path);
//            final var mimeType = Files.probeContentType(filePath);
//
//            if (path.equals("/classic.html")) {
//                final var template = Files.readString(filePath);
//                final var content = template.replace(
//                        "{time}",
//                        LocalDateTime.now().toString()
//                ).getBytes();
//                out.write((
//                        "HTTP/1.1 200 OK\r\n" +
//                                "Content-Type: " + mimeType + "\r\n" +
//                                "Content-Length: " + content.length + "\r\n" +
//                                "Connection: close\r\n" +
//                                "\r\n"
//                ).getBytes());
//                out.write(content);
//                out.flush();
//                return;
//            }
//
//            final var length = Files.size(filePath);
//            out.write((
//                    "HTTP/1.1 200 OK\r\n" +
//                            "Content-Type: " + mimeType + "\r\n" +
//                            "Content-Length: " + length + "\r\n" +
//                            "Connection: close\r\n" +
//                            "\r\n"
//            ).getBytes());
//            Files.copy(filePath, out);
//            out.flush();
//            return;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void printNotFoundError(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    public void addHandler(String headline, String path, Handler handler) {
        if (handlers.containsKey(headline)) {
            handlers.get(headline).put(path, handler);
        } else {
            handlers.put(headline, new ConcurrentHashMap<>());
            handlers.get(headline).put(path, handler);
        }
    }


}
