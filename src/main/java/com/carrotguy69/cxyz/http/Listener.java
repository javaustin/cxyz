package com.carrotguy69.cxyz.http;

import com.carrotguy69.cxyz.exceptions.AuthenticationFailException;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.utils.JsonConverters;
import com.carrotguy69.cxyz.utils.TimeUtils;
import com.google.gson.Gson;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.carrotguy69.cxyz.CXYZ.*;
import static com.carrotguy69.cxyz.http.Request.generateSignature;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handleFriendRequestDelivery;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handleFriendRequestShipment;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handleGameStatDelivery;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handleGameStatShipment;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handleMessageDelivery;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handleMessageShipment;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handleNetworkPlayerDelivery;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handleNetworkPlayerShipment;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handlePartyDelivery;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handlePartyInviteDelivery;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handlePartyInviteShipment;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handlePartyShipment;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handlePunishmentDelivery;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.handlePunishmentShipment;

public final class Listener {

    private final Undertow server;

    public Listener(int port) {
        this.server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0")
                .setHandler(this::handleRequest)
                .build();

        server.start();
        Logger.info("HTTP listener started on: http://0.0.0.0:" + port);
    }

    public void stop() {
        server.stop();
    }

    private void handleRequest(HttpServerExchange exchange) {
        // Reading the body uses blocking I/O, so dispatch off Undertow's I/O thread.
        if (exchange.isInIoThread()) {
            exchange.dispatch(this::handleRequest);
            return;
        }

        try {
            if (!"POST".equals(exchange.getRequestMethod().toString())) {
                send(exchange, StatusCodes.NOT_FOUND, "text/plain", "Not found");
                return;
            }

            exchange.startBlocking();

            String postData = new String(
                    exchange.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );

            authenticateRequest(exchange, postData);

            Map<String, String> data = JsonConverters.toMapString(postData);

            switch (exchange.getRequestPath()) {
                case "/send_message":
                    handleSendMessage(data);
                    break;

                case "/usersShipment":
                    initializedMap.put("users", true);
                    handleNetworkPlayerShipment(postData);
                    break;
                case "/usersDelivery":
                    handleNetworkPlayerDelivery(postData);
                    break;

                case "/partiesShipment":
                    initializedMap.put("parties", true);
                    handlePartyShipment(postData);
                    break;
                case "/partiesDelivery":
                    handlePartyDelivery(postData);
                    break;

                case "/partyInvitesShipment":
                    initializedMap.put("partyInvites", true);
                    handlePartyInviteShipment(postData);
                    break;
                case "/partyInvitesDelivery":
                    handlePartyInviteDelivery(postData);
                    break;

                case "/punishmentsShipment":
                    initializedMap.put("punishments", true);
                    handlePunishmentShipment(postData);
                    break;
                case "/punishmentsDelivery":
                    handlePunishmentDelivery(postData);
                    break;

                case "/messagesShipment":
                    initializedMap.put("messages", true);
                    handleMessageShipment(postData);
                    break;
                case "/messagesDelivery":
                    handleMessageDelivery(postData);
                    break;

                case "/gameStatsShipment":
                    initializedMap.put("gameStats", true);
                    handleGameStatShipment(postData);
                    break;
                case "/gameStatsDelivery":
                    handleGameStatDelivery(postData);
                    break;

                case "/friendRequestsShipment":
                    initializedMap.put("friendRequests", true);
                    handleFriendRequestShipment(postData);
                    break;
                case "/friendRequestsDelivery":
                    handleFriendRequestDelivery(postData);
                    break;

                default:
                    send(exchange, StatusCodes.NOT_FOUND, "application/json",
                            "{\"status\":\"not found\"}");
                    return;
            }

            send(exchange, StatusCodes.OK, "application/json",
                    "{\"status\":\"received\"}");

        } catch (AuthenticationFailException ex) {
            send(exchange, StatusCodes.UNAUTHORIZED, "application/json",
                    gson.toJson(Map.of("error", ex.getMessage())));
        } catch (Exception ex) {
            Logger.logStackTrace(ex);
            send(exchange, StatusCodes.BAD_REQUEST, "application/json",
                    gson.toJson(Map.of("error",
                            ex.getMessage() == null ? "Bad request" : ex.getMessage())));
        }
    }

    private static void send(
            HttpServerExchange exchange, int status, String contentType, String body
    ) {
        exchange.setStatusCode(status);
        exchange.getResponseHeaders().put(
                Headers.CONTENT_TYPE, contentType + "; charset=utf-8"
        );
        exchange.getResponseSender().send(body);
    }

    public void handleSendMessage(Map<?, ?> map) {

        if (!map.containsKey("content")) {
            throw new RuntimeException("Missing required argument: `content`");
        }

        String content = (String) map.get("content");

        Map<String, Object> formatMap = new HashMap<>();
        List<String> toChannels = new ArrayList<>();
        List<String> toPlayers = new ArrayList<>();

        if (map.containsKey("format_map")) {
            formatMap = (Map<String, Object>) map.get("format_map");
        }

        if (map.containsKey("to_channels")) {
            toChannels = (List<String>) map.get("to_channels");
        }

        if (map.containsKey("to_players")) {
            toPlayers = (List<String>) map.get("to_players");
        }

        boolean toAllPlayers = (toPlayers.size() == 1 && toPlayers.get(0).equalsIgnoreCase("*")) || (toChannels.isEmpty() && toPlayers.isEmpty());

        if (toAllPlayers) {
            TextComponent component = MessageUtils.createMessage(content, formatMap);

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.spigot().sendMessage(component);
            }

            return;
        }

        for (String channelName : toChannels) {
            BaseChannel channel = BaseChannel.getChannel(channelName);

            if (channel == null) {
                throw new RuntimeException(String.format("Invalid channel: '%s'", channelName));
            }

            channel.sendChannelMessage(content, formatMap, false);
        }

        for (String playerUUID : toPlayers) {
            if (!NetworkPlayer.exists(UUID.fromString(playerUUID))) {
                throw new RuntimeException(String.format("Invalid player UUID: '%s'", playerUUID));
            }

            Player p = Bukkit.getPlayer(UUID.fromString(playerUUID));

            if (p != null) {
                MessageUtils.sendParsedMessage(p, content, formatMap);
            }
        }
    }

    private static void authenticateRequest(HttpServerExchange exchange, String payload) throws Exception {

        String identifier = exchange.getRequestHeaders().getFirst("X-Identifier");
        String timestampString = exchange.getRequestHeaders().getFirst("X-Timestamp");
        String providedSignature = exchange.getRequestHeaders().getFirst("X-Signature");

        if (identifier == null) {
            throw new AuthenticationFailException("\"X-Identifier\" is required for interacting with this service.");
        }

        if (timestampString == null) {
            throw new AuthenticationFailException("\"X-Timestamp\" is required for interacting with this service.");
        }

        if (providedSignature == null) {
            throw new AuthenticationFailException("\"X-Signature\" is required for interacting with this service.");
        }

        long timestamp;
        try {
            timestamp = Long.parseLong(timestampString);
        }
        catch (NumberFormatException e) {
            throw new AuthenticationFailException("\"X-Timestamp\" is invalid");
        }

        long now = TimeUtils.unixTimeNow();
        if (timestamp < now - 30 || timestamp > now + 30) {
            throw new AuthenticationFailException("Request timestamp expired.");
        }

        if (!identifier.equals(apiIdentifier)) {
            throw new AuthenticationFailException("This server does not accept requests from that identifier!");
        }

        String method = exchange.getRequestMethod().toString();
        String path = exchange.getRequestPath();

        String correctSignature = generateSignature(
                apiIdentifier,
                apiSecret,
                timestamp,
                method,
                path,
                payload == null ? "" : payload
        );

        if (!MessageDigest.isEqual(correctSignature.getBytes(StandardCharsets.UTF_8), providedSignature.getBytes(StandardCharsets.UTF_8))) {
            throw new AuthenticationFailException("Signature is invalid.");
        }
    }
}
