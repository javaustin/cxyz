package com.carrotguy69.cxyz.http;

import com.carrotguy69.cxyz.exceptions.AuthenticationFailException;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.messages.utils.MessageGrabber;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.models.config.services.Service;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.utils.JsonConverters;
import com.carrotguy69.cxyz.utils.TimeUtils;
import com.google.gson.reflect.TypeToken;
import fi.iki.elonen.NanoHTTPD;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.carrotguy69.cxyz.CXYZ.gson;
import static com.carrotguy69.cxyz.CXYZ.initializedMap;
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

public class Listener extends NanoHTTPD {

    public Listener(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        Logger.info("HTTP listener started on: http://localhost:" + port);
    }

    private static void authenticateRequest(IHTTPSession session, Map<String, String> body) throws Exception {
        // throws RuntimeException (or child) if we cannot authenticate the response

        String payload = session.getMethod() == Method.POST ? body.getOrDefault("postData", "") : "";

        Map<String, String> headers = session.getHeaders();

        // * NanoHTTPD headers are lowercased
        String identifier = headers.get("x-identifier");
        String timestampString = headers.get("x-timestamp");
        String providedSignature = headers.get("x-signature");

        if (identifier == null) {
            throw new AuthenticationFailException("\"X-Identifier\" is required for interacting with this service.");
        }

        if (timestampString == null) {
            throw new AuthenticationFailException("\"X-Timestamp\" is required for interacting with this service.");
        }

        if (providedSignature == null) {
            throw new AuthenticationFailException("\"X-Signature\" is required for interacting with this service.");
        }

        long timestamp = 0;

        try {
            timestamp = Long.parseLong(timestampString);
        }
        catch (NumberFormatException e) {
            throw new AuthenticationFailException("\"X-Timestamp\" is invalid");
        }

        if (Math.abs(TimeUtils.unixTimeNow() - timestamp) > 30) {
            throw new AuthenticationFailException("Request timestamp expired.");
        }

        Service sender = Service.getServerByIdentifier(identifier);

        if (sender == null) {
            throw new AuthenticationFailException(String.format("No service with identifier=%s is registered.", identifier));
        }

        String method = session.getMethod().name();
        String path = session.getUri();

        String correctSignature = generateSignature(sender, timestamp, method, path, payload);


        if (!MessageDigest.isEqual(correctSignature.getBytes(StandardCharsets.UTF_8), providedSignature.getBytes(StandardCharsets.UTF_8))) {
            throw new AuthenticationFailException("Signature is invalid.");
        }

    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            Map<String, String> body = new HashMap<>();
            session.parseBody(body);

            Logger.debugRequest("Received request: " + session + "\n" + body);

            authenticateRequest(session, body);

            String postData = body.get("postData");

            if (Method.POST.equals(session.getMethod())) {

                Map<String, String> data = JsonConverters.toMapString(postData);

                switch (session.getUri()) {

                    case "/send_message":
                        handleSendMessage(data);
                        break;

                    // Deliveries and Shipments
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
                        // Handle unknown URIs if necessary
                        return newFixedLengthResponse(Response.Status.NOT_FOUND, "application/json",
                                "{\"status\" : \"not found\"}");
                }
                return newFixedLengthResponse(Response.Status.OK, "application/json",
                        "{\"status\" : \"received\"}");
            }
        }

        catch (AuthenticationFailException ex) {
            return newFixedLengthResponse(Response.Status.UNAUTHORIZED, "application/json", gson.toJson(Map.of("error", ex.getMessage())));
        }

        catch (Exception ex) {
            Logger.logStackTrace(ex);
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json", gson.toJson(Map.of("error", ex.getMessage())));
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found");
    }

    public void handleSendMessage(Map<?, ?> map) {

        if (!map.containsKey("message_key")) {
            throw new RuntimeException("Missing required argument: `message_key`");
        }

        String messageKeyString = null;

        try {
            messageKeyString = (String) map.get("message_key");
            MessageKey key = MessageKey.valueOf(messageKeyString.toUpperCase());

            Map<String, Object> commonMap = new HashMap<>();
            List<String> toChannels = new ArrayList<>();
            List<String> toPlayers = new ArrayList<>();

            if (map.containsKey("format_map")) {
                commonMap = (Map<String, Object>) map.get("format_map");
            }

            if (map.containsKey("to_channels")) {
                toChannels = (List<String>) map.get("to_channels");
            }

            if (map.containsKey("to_players")) {
                toPlayers = (List<String>) map.get("to_players");
            }

            boolean toAll = toPlayers.size() == 1 && toPlayers.get(0).equalsIgnoreCase("*");

            if (toAll) {
                TextComponent component = MessageUtils.createMessage(MessageGrabber.grab(key), commonMap);

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

                channel.sendChannelMessage(MessageGrabber.grab(key), commonMap);
            }

            for (String playerUUID : toPlayers) {
                if (!NetworkPlayer.exists(UUID.fromString(playerUUID))) {
                    throw new RuntimeException(String.format("Invalid player UUID: '%s'", playerUUID));
                }

                Player p = Bukkit.getPlayer(UUID.fromString(playerUUID));

                if (p != null) {
                    MessageUtils.sendParsedMessage(p, MessageGrabber.grab(key), commonMap);
                }
            }

        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException(String.format("Invalid message key: '%s'", messageKeyString));
        }
    }
}
