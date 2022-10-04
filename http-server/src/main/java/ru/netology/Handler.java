package ru.netology;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

public interface Handler {
    void handle(Request request, BufferedOutputStream responseStream, BufferedInputStream in) throws IOException;
}