package ru.netology;

import org.apache.http.NameValuePair;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private String requestMethod;
    private String requestPath;
    private String requestVersion;
    private Map<String, String> headers = new HashMap<>();
    private String requestBody;
    private List<NameValuePair> queryParams = new ArrayList<>();

    public Request(String requestMethod, String requestPath, String requestBody, String requestVersion) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
        this.requestVersion = requestVersion;
        this.requestBody = requestBody;
    }

    public void setHeadersList(List<String> headers) {

        for (String i : headers) {
            this.headers.put(i.split(": ")[0], i.split(": ")[1]);
        }

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

    public Map<String,String> getHeadersList() {
        return headers;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void putParamsPair(NameValuePair param) {
        queryParams.add(param);
    }

    public void setRequestBody(String name) {
        this.requestBody = name;
    }

    public void printAllQueryParams() {
        //Поскольку в рамках задания мы в дальнейшем не используем данные из параметров, то вместо возврата значения
        //Выводим на экран, для возврата значений будет использоваться обычный гетер
        for (NameValuePair i : queryParams) {
            System.out.println(i);
        }
    }

    public void printQueryParamsByName(String name) {
                //Поскольку в рамках задания мы в дальнейшем не используем данные из параметров, то вместо возврата значения
                //Выводим на экран, для возврата значений будет использоваться обычный гетер
        for (NameValuePair i : queryParams) {
            if (i.getName().equals(name)) {
                System.out.println(i);
            }
        }
    }
}

