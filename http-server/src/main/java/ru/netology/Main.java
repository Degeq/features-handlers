package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Main {
  public static void main(String[] args) throws IOException {
    Server server = new Server();

    // добавление handler'ов (обработчиков)
    server.addHandler("GET", "/api/forms", new Handler() {
      public void handle(Request request, BufferedOutputStream out, BufferedInputStream in) throws IOException {
       parseParams(request, out);
      }
    });

    server.addHandler("POST", "/api/forms", new Handler() {
      public void handle(Request request, BufferedOutputStream out, BufferedInputStream in) {
       readPostRequestBody(request, in);
       parseParams(request, out);
    }
    });

    server.listen(9999);

  }

  public static void readPostRequestBody(Request request, BufferedInputStream in) {
    try {
      final var length = Integer.parseInt(request.getHeadersList().get("Content-Length"));
      final byte[] bodyBytes;

      bodyBytes = in.readNBytes(length);
      final var body = new String(bodyBytes);
      request.setRequestBody(body);
  } catch (IOException ioe) {
    ioe.printStackTrace();
    }
  }

  public static void parseParams(Request request, BufferedOutputStream out) {
  try {
    var queries = URLEncodedUtils.parse(request.getRequestBody(), StandardCharsets.UTF_8);
    for (NameValuePair i : queries) {
      request.putParamsPair(URLEncodedUtils.parse(String.valueOf(i), StandardCharsets.UTF_8).get(0));
    }
    request.printAllQueryParams();
    out.write((
            "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: 0\r\n" +
                    "Connection: close\r\n" +
                    "\r\n"
    ).getBytes());
    out.flush();
  } catch (IOException e) {
        e.printStackTrace();
        }

  }
}


