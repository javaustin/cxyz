package com.carrotguy69.cxyz.http;

import com.google.gson.reflect.TypeToken;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.carrotguy69.cxyz.CXYZ.*;

public class RequestResult {

    public final RequestType method;
    public final String url;
    public final String responseBody;
    public final String requestBody;
    public final int statusCode;

    public RequestResult(RequestType method, String url, String requestBody, String responseBody, int statusCode) {
        this.method = method;
        this.url = url;
        this.responseBody = responseBody;
        this.requestBody = requestBody;
        this.statusCode = statusCode;
    }

    public String toCompactString() {
        return "RequestResult{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", requestBody='" + requestBody + '\'' +
                ", responseBody='" + responseBody + '\'' +
                ", statusCode='" + statusCode + '\'' +
                '}';

    }

    @Override
    public String toString() {
        return "RequestResult{" +
                "\nmethod='" + method + '\'' +
                ", \nurl='" + url + '\'' +
                ", \nrequestBody='" + requestBody + '\'' +
                ", \nresponseBody='" + responseBody + '\'' +
                ", \nstatusCode='" + statusCode + '\'' +
                '}';
    }

    public static RequestResult normalizeResponse(RequestResult r) {
        // checks if the api gave an error, will raise an exception if we don't have a useful message

        Map<String, Object> json = gson.fromJson(r.responseBody, new TypeToken<Map<String, Object>>() {}.getType());

        if ((r.statusCode < 200 || r.statusCode >= 300) && json.containsKey("message")) {
            return new RequestResult(r.method, r.url, r.requestBody, r.responseBody, r.statusCode); // we can return a request result when it has body for us
        }
        // ----- every thing below represents a successful request -----


        if (r.url.contains("sql") && r.responseBody.contains("Operation successful!")) { // a null value is returned from the SQL script, we will return the null value with a 200 status code
            return new RequestResult(r.method, r.url, r.requestBody, null, 200); // we can return a request result when it has body for us

        }

        return r;
    }

}
