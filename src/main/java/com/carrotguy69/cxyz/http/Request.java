package com.carrotguy69.cxyz.http;

import com.carrotguy69.cxyz.other.utils.JsonConverters;
import com.carrotguy69.cxyz.other.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.http.RequestResult.normalizeResponse;

public class Request {


    public final RequestType type;
    public final String url;
    public final String requestBody;
    private int attempt = 0;

    public static String apiKey = "TSmTS2us5INUfhw2";
    public static HttpClient client = HttpClient.newHttpClient();
    private static final int maxAttempts = 3;


    public Request(RequestType type, String url, String requestBody) {
        this.type = type;
        this.url = url;
        this.requestBody = requestBody;
    }

    public CompletableFuture<RequestResult> send() {
        CompletableFuture<RequestResult> resultFuture = new CompletableFuture<>();
        sendAttempt(resultFuture);
        return resultFuture;
    }

    private void sendAttempt(CompletableFuture<RequestResult> resultFuture) {
        // Completes the resultFuture provided.

        if (attempt >= maxAttempts) {
            Logger.debugFailedRequest("Request failed too many times (" + attempt + "): " + url);
            resultFuture.completeExceptionally(
                    new RuntimeException("Exceeded retry attempts (" + attempt + ") for " + url)
            );
            return;
        }

        attempt++;

        long timeout = api_timeout * (long) Math.max(1, attempt); // Increases timeout time for failed attempts

        HttpRequest request;
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("X-API-KEY", apiKey)
                    .header("X-Request-Expires", String.valueOf(System.currentTimeMillis() + (timeout - 1000)))
                    .timeout(Duration.ofMillis(timeout));

            if (type == RequestType.POST) {

                Map<String, Object> bodyMap = JsonConverters.toMap(requestBody);
//                bodyMap.put("fromGameServer", this_server.getName());

                String body2 = gson.toJson(bodyMap);

                builder.POST(HttpRequest.BodyPublishers.ofString(body2)); // if this fails hilariously, replace the body2 argument with body, and delete the previous code
            }
            else {
                builder.GET();
            }

            request = builder.build();

        }
        catch (IllegalArgumentException e) {
            resultFuture.completeExceptionally(
                    new RuntimeException("Invalid URL: " + url)
            );
            return;
        }

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((response, throwable) -> {

                    // hard timeout or network failure
                    if (throwable != null) {
                        Logger.debugFailedRequest("Request timed out (" + attempt + "/" + maxAttempts + "): " + url);
                        sendAttempt(resultFuture);
                        return;
                    }

                    RequestResult normalized = normalizeResponse(new RequestResult(type, url, requestBody, response.body(), response.statusCode()));


                    // logical timeout via status code
                    if (normalized.statusCode == 408) {
                        Logger.debugFailedRequest("Endpoint returned (408) (" + attempt + "/" + maxAttempts + "): " + normalized.toCompactString());
                        sendAttempt(resultFuture);
                        return;
                    }

                    if (normalized.statusCode < 200 && response.statusCode() > 299)
                        Logger.debugFailedRequest("Received result: " + normalized.toCompactString());

                    else
                        Logger.debugAllRequest("Received result: " + normalized.toCompactString());

                    // normal result (may still be error)
                    resultFuture.complete(normalized);
                });
    }

    public static CompletableFuture<RequestResult> postRequest(String url, String data) {
        Request req = new Request(RequestType.POST, url, data);
        return req.send();
    }
}

