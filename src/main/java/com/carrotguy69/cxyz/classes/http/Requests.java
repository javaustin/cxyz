package com.carrotguy69.cxyz.classes.http;

import com.google.gson.reflect.TypeToken;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Requests {

    public static String apiKey = "TSmTS2us5INUfhw2";
    public static int failThreshold = 5;
    public static HttpClient client = HttpClient.newHttpClient();

    public static RequestResult normalizeResponse(RequestResult r) {
        // checks if the api gave an error, will raise an exception if we don't have a useful message

        Map<String, Object> json = gson.fromJson(r.body, new TypeToken<Map<String, Object>>() {}.getType());

        if ((r.statusCode < 200 || r.statusCode >= 300) && json.containsKey("message")) {
            return new RequestResult(r.method, r.url, r.body, r.statusCode); // we can return a request result when it has body for us
        }
        // ----- every thing below represents a successful request -----


        if (r.url.contains("sql") && r.body.contains("Operation successful!")) { // a null value is returned from the SQL script, we will return the null value with a 200 status code
            return new RequestResult(r.method, r.url, null, 200);
        }

        return r;
    }

    public static CompletableFuture<RequestResult> postRequest(String url, String data) {
        Request req = new Request(RequestType.POST, url, data);
        return req.send();
    }

    public static class RequestResult {
        public final RequestType method;
        public final String url;
        public final String body;
        public final int statusCode;


        public RequestResult(RequestType method, String url, String body, int statusCode) {
            this.method = method;
            this.url = url;
            this.body = body;
            this.statusCode = statusCode;
        }

        @Override
        public String toString() {
            return "RequestResult{" +
                    "method='" + method + '\'' +
                    ", url='" + url + '\'' +
                    ", body='" + body + '\'' +
                    ", statusCode='" + statusCode + '\'' +
                    '}';

        }

        public String toCompactString() {
            return "RequestResult{" +
                    "\nmethod='" + method + '\'' +
                    ", \nurl='" + url + '\'' +
                    ", \nbody='" + body + '\'' +
                    ", \nstatusCode='" + statusCode + '\'' +
                    '}';

        }
    }

}
