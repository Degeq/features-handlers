package ru.netology;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                final var in = new BufferedInputStream(socket.getInputStream());
                final var out = new BufferedOutputStream(socket.getOutputStream());
        )  {
            final var limit = 4096;

            in.mark(limit);
            final var buffer = new byte[limit];
            final var read = in.read(buffer);

            // ищем request line
            final var requestLineDelimiter = new byte[]{'\r', '\n'};
            final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
            if (requestLineEnd == -1) {
                printNotFoundError(out);
                return;
            }

            // читаем request line
            final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
            if (requestLine.length != 3) {
                printNotFoundError(out);
                return;
            }

            var position = requestLine[1].indexOf('?');

            var request = new Request(requestLine[0], requestLine[1].substring(0, position),
                    requestLine[1].substring(position+1), requestLine[2]);

            System.out.println(request.getRequestBody());

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

            final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
            final var headersStart = requestLineEnd + requestLineDelimiter.length;
            final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);

            if (headersEnd == -1) {
                printNotFoundError(out);
            }

            in.reset();
            in.skip(headersStart);

            final var headersBytes = in.readNBytes(headersEnd - headersStart);
            final var headers = Arrays.asList(new String(headersBytes).split("\r\n"));
            request.setHeadersList(headers);
            System.out.println(headers);

            in.skip(headersDelimiter.length);

            handler.handle(request, out, in);
            return;

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

    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

}
