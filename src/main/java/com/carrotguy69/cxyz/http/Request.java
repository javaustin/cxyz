package com.carrotguy69.cxyz.http;

import com.carrotguy69.cxyz.models.config.services.Service;
import com.carrotguy69.cxyz.utils.JsonConverters;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.utils.TimeUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.http.RequestResult.normalizeResponse;

public class Request {

    public final RequestType type;
    public final String url;
    public final String requestBody;
    private int attempt = 0;

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
        Logger.debugRequest(String.format("[🔃] POST in progress: %s", url));
        return resultFuture;
    }

    private void sendAttempt(CompletableFuture<RequestResult> resultFuture) {
        // Completes the resultFuture provided.

        if (attempt >= maxAttempts) {
            Logger.log(String.format("[❌] %s failed too many times (%d): %s", type.name().toUpperCase(), attempt, url));
            resultFuture.completeExceptionally(
                    new RuntimeException("Exceeded retry attempts (" + attempt + ") for " + url)
            );
            return;
        }

        attempt++;

        long timeout = apiTimeoutMillis * (long) Math.max(1, attempt); // Increases timeout time for failed attempts

        HttpRequest request;
        try {
            long timestamp = TimeUtils.unixTimeNow();

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("X-Identifier", thisServer.getIdentifier())
                    .header("X-Timestamp", String.valueOf(timestamp))
                    .timeout(Duration.ofMillis(timeout));

            if (type == RequestType.POST) {

                Map<String, Object> bodyMap = JsonConverters.toMap(requestBody);
                String bodyGSON = gson.toJson(bodyMap);

                builder.header("X-Signature", generateSignature((Service) thisServer, timestamp, "POST", URI.create(url).getPath(), bodyGSON));

                builder.POST(HttpRequest.BodyPublishers.ofString(bodyGSON));
            }
            else {
                builder.header("X-Signature", generateSignature((Service) thisServer, timestamp, "GET", URI.create(url).getPath(), ""));
                builder.GET();
            }

            request = builder.build();

        }
        catch (Exception e) {
            resultFuture.completeExceptionally(
                    new Throwable(e)
            );
            return;
        }

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((response, throwable) -> {

                    String method = request.method();

                    // hard timeout or network failure
                    if (throwable != null) {
                        Logger.log(throwable.getMessage());
                        Logger.log(String.format("[❌] %s FAILED (%d/%d): %s", method.toUpperCase(), attempt, maxAttempts, url));
                        sendAttempt(resultFuture);
                        return;
                    }

                    RequestResult normalized = normalizeResponse(new RequestResult(type, url, requestBody, response.body(), response.statusCode()));

                    if (normalized.statusCode != 200)
                        Logger.log(String.format("[⚠️] %s COMPLETED (%d) %s.\n\tBody: %s", method.toUpperCase(), normalized.statusCode, normalized.url, normalized.responseBody));

                    if (normalized.statusCode == 200)
                        Logger.debugRequest(String.format("[✅] %s COMPLETED (%d) %s.\n\tBody: %s", method.toUpperCase(), normalized.statusCode, normalized.url, normalized.responseBody));

                    // normal result (may still be error)
                    resultFuture.complete(normalized);
                });
    }

    public static CompletableFuture<RequestResult> postRequest(String url, String data) {
        Request req = new Request(RequestType.POST, url, data);
        return req.send();
    }

    public static CompletableFuture<RequestResult> getRequest(String url) {
        Request req = new Request(RequestType.GET, url, "");
        return req.send();
    }

    public static String generateSignature(String identifier, String secret, long timestamp, String method, String path, String payloadJSON) throws Exception {
        String message = identifier + " | " + timestamp + " | " + method + " | " + path + " | " + payloadJSON;


        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        mac.init(key);

        byte[] raw = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

        String signature = Base64.getEncoder().encodeToString(raw);

//        Logger.log("Generating signature with: " + message + "\n\tSignature: " + signature);

        return signature;
    }

    public static String generateSignature(Service service, long timestamp, String method, String path, String payloadJSON) throws Exception {
        return generateSignature(service.getIdentifier(), service.getSecret(), timestamp, method, path, payloadJSON);
    }
}

