package ru.netology;

public class Request {
    private String requestMethod;
    private String requestPath;
    private String requestVersion;
//    private String requestBody;

    public Request(String requestMethod, String requestPath, String requestVersion) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.requestVersion = requestVersion;
//        this.requestBody = requestBody;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public String getRequestVersion() {
        return requestVersion;
    }


}
