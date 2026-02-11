package com.carrotguy69.cxyz.http;

import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.models.config.channel.channelTypes.BaseChannel;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.other.utils.JsonConverters;
import com.google.gson.reflect.TypeToken;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.carrotguy69.cxyz.CXYZ.gson;
import static com.carrotguy69.cxyz.CXYZ.initializedMap;
import static com.carrotguy69.cxyz.http.ShipmentDelivery.*;
import static com.carrotguy69.cxyz.models.config.channel.channelTypes.CustomChannel.sendChannelMessage;
import static java.lang.Boolean.parseBoolean;

public class Listener extends NanoHTTPD {

    public Listener(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        Logger.info("HTTP listener started on port: " + port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        try {
            Map<String, String> body = new HashMap<>();
            session.parseBody(body);

            // Clean up temp file(s) if any
            String tmpFilePath = body.get("file"); // only present for multipart/form-data
            if (tmpFilePath != null) {
                File tmpFile = new File(tmpFilePath);
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
            }


            String postData = body.get("postData");

            if (Method.POST.equals(session.getMethod())) {

                Map<String, String> data = JsonConverters.toMapString(postData);

                switch (session.getUri()) {

                    case "/sendMessage":
                        handleSendMessage(data);
                        break;

                    case "/sendChannelMessage":
                        handleSendChannelMessage(data);
                        break;

                    case "/sendPublicMessage":
                        handleSendPublicMessage(data);
                        break;

                    case "/command":
                        handleCommand(data);
                        break;

                    case "/kickPlayer":
                        handleKickPlayer(data);
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



                    case "/partyExpiresShipment":
                        initializedMap.put("partyExpires", true);
                        handlePartyExpireShipment(postData);
                        break;

                    case "/partyExpiresDelivery":
                        handlePartyExpireDelivery(postData);
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
                        String.format("{\"status\" : \"%s\"}", "received"));
            }
        }

        catch (Exception ex) {
            Logger.logStackTrace(ex);
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json", "{}");
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found");
    }

    public void handleSendMessage(Map<String, String> map) {
        // We don't check to see if the player is online. We assume the server that sent this request already checked, and is sending the message based off of the fact that that player is online.

        if (!map.containsKey("recipientUUID") || !map.containsKey("content") || !map.containsKey("parsed") || !map.containsKey("formatMap")) {
            throw new RuntimeException("One or more of the following required keys (recipientUUID, content, parsed, formatMap) is missing: " + map.get("recipientUUID") + ", " + map.get("content") + ", " + map.get("parsed") + ", " + map.get("formatMap"));
        }

        String recipient = map.get("recipientUUID");
        String content = map.get("content");
        boolean parsed = parseBoolean(map.get("parsed"));
        Map<String, Object> formatMap = gson.fromJson(map.get("formatMap"), new TypeToken<Map<String, Object>>() {}.getType());


        NetworkPlayer recipientNP = NetworkPlayer.getPlayerByUUID(UUID.fromString(recipient));

        Player p = recipientNP.getPlayer();

        if (p == null) {
            Logger.severe("In HttpListener.handleSendMessage(...), Player p was found to be null.");
            return;
        }

        if (parsed)
            MessageUtils.sendParsedMessage(p, content, formatMap);

        else
            MessageUtils.sendUnparsedMessage(p, content, formatMap);

    }

    public void handleSendPublicMessage(Map<String, String> map) {

        if (!map.containsKey("content") || !map.containsKey("parsed") || !map.containsKey("formatMap")) {
            throw new RuntimeException("One or more of the following required keys (content, parsed, formatMap) is missing: " + map.get("content") + ", " + map.get("parsed") + ", " + map.get("formatMap"));
        }

        String content = map.get("content");
        boolean parsed = Boolean.getBoolean(map.get("parsed"));
        Map<String, Object> formatMap = gson.fromJson(map.get("formatMap"), new TypeToken<Map<String, Object>>() {}.getType());

        MessageUtils.sendPublicMessage(content, parsed, formatMap);
    }

    public void handleSendChannelMessage(Map<String, String> map) {
        if (!map.containsKey("channel") || !map.containsKey("content") || !map.containsKey("parsed") || !map.containsKey("formatMap")) {
            throw new RuntimeException("One or more of the following required keys (channel, content, parsed, formatMap) is missing: " + map.get("channel") + ", " + map.get("content") + ", " + map.get("parsed") + ", " + map.get("formatMap"));
        }

        String channelName = map.get("channel"); //
        String chatFormat = map.get("chatFormat"); // We pre-defined the format already. So this content will not change
        Map<String, Object> formatMap = gson.fromJson(map.get("formatMap"), new TypeToken<Map<String, Object>>() {}.getType());

        BaseChannel channel = BaseChannel.getChannel(channelName);

        if (channel == null) {
            Logger.warning(map.toString());
            throw new NullPointerException("No channel found named " + channelName);
        }

        sendChannelMessage(channel, chatFormat, formatMap);
    }




    public void handleCommand(Map<String, String> map) {

        if (!map.containsKey("commandLine")) {
            throw new RuntimeException("One or more of the following required keys (commandLine) is missing: " + map.get("commandLine"));
        }

        String commandLine = map.get("commandLine");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine);
    }

    public void handleKickPlayer(Map<String, String> map) {
        String recipient = map.get("recipientUUID");
        String reason = map.get("reason");

        if (!map.containsKey("recipientUUID") || !map.containsKey("reason")) {
            throw new RuntimeException("One or more of the following required keys (recipientUUID, reason) is missing: " + map.get("recipientUUID") + ", " + map.get("reason"));
        }

        Player p = Bukkit.getPlayer(UUID.fromString(recipient));

        if (p == null) {
            throw new NullPointerException("Player with UUID of " + recipient + " is offline or does not exist.");
        }

        NetworkPlayer.kickPlayer(p, reason);
    }
}
