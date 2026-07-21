package com.carrotguy69.cxyz.models.db;


import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.http.RequestType;
import com.carrotguy69.cxyz.utils.TimeUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Message {
    private final String sender_uuid;
    private final String sender_name;
    private final String recipient_uuid;
    private final String recipient_name;
    private final String content;
    private final long timestamp;

    public static Message getLastReplyableMessage(UUID recipientUUID) {
        Message lastMessage = null;


        for (Message msg : messageMap.values()) { // Loops through all messages

            if (msg.getRecipientUUID().equals(recipientUUID)) { // Ignores if the message was not received by our given player.

                // If we don't have a good message yet, we will set it to whatever we get here for point of reference.
                // We will then check if our current message (in the loop), was sent more recently then the last, and use that.
                if (lastMessage == null || msg.getTimestamp() > lastMessage.getTimestamp() && !msg.getContent().isEmpty()) {
                    lastMessage = msg;
                }
            }
        }

        return lastMessage;
    }

    public static Message getLastSentMessage(UUID senderUUID) {
        Message lastMessage = null;


        for (Message msg : messageMap.values()) { // Loops through all messages

            if (msg.getSenderUUID().equals(senderUUID)) { // Ignores if the message was not sent by our player.
                // If we don't have a good message yet, we will set it to whatever we get here for point of reference.
                // We will then check if our current message (in the loop), was sent more recently then the last, and use that.
                if (lastMessage == null || msg.getTimestamp() > lastMessage.getTimestamp()) {
                    lastMessage = msg;
                }
            }
        }


        return lastMessage;
    }

    public static Collection<Message> getMessagesByRecipient(UUID recipientUUID) {
        return messageMap.get(recipientUUID);
    }

    public static Collection<Message> getMessages(UUID recipientUUID, UUID senderUUID) {
        Set<Message> results = new HashSet<>();

        Collection<Message> msgs = messageMap.get(recipientUUID);

        for (Message msg : msgs) {
            if (msg.getSenderUUID() == senderUUID)
                results.add(msg);
        }

        return results;
    }

    public static Collection<Message> getMessagesBySender(UUID senderUUID) {
        List<Message> results = new ArrayList<>();

        for (Map.Entry<UUID, Message> entry : messageMap.entries()) {
            if (entry.getValue().getSenderUUID() == senderUUID) {
                results.add(entry.getValue());
            }
        }

        return results;
    }

    public static void deleteAll(@Nullable NetworkPlayer matchingSender, @Nullable NetworkPlayer matchingRecipient, @Nullable String matchingContent, long afterTimestamp) {
        // Send a request to the API to delete any messages with the query. The API will push back the result back so we can cache it with our delivery system.

        Map<String, Object> body  = new HashMap<>();

        if (matchingSender != null)
            body.put("sender_uuid", matchingSender.getUUID());

        if (matchingRecipient != null)
            body.put("recipient_uuid", matchingRecipient.getUUID());

        if (matchingContent != null)
            body.put("content", matchingContent);

        if (afterTimestamp > 0)
            body.put("timestamp", afterTimestamp);

        Request req = new Request(
                RequestType.POST,
                apiEndpoint + "/message/delete",
                gson.toJson(body)
        );

        req.send();
    }

    public Message(NetworkPlayer sender, NetworkPlayer recipient, String content) {
        this.sender_uuid = sender.getUUID().toString();
        this.sender_name = sender.getUsername();
        this.recipient_uuid = recipient.getUUID().toString();
        this.recipient_name = recipient.getUsername();
        this.content = content;
        this.timestamp = TimeUtils.unixTimeNow();
    }

    public Message(String senderUUID, String senderName, String recipientUUID, String recipientName, String content, long timestamp) {
        this.sender_uuid = senderUUID;
        this.sender_name = senderName;
        this.recipient_uuid = recipientUUID;
        this.recipient_name = recipientName;
        this.content = content;
        this.timestamp = timestamp;
    }

    public void submit() {
        Request.postRequest(apiEndpoint + "/message/submit", gson.toJson(this));
    }

    public UUID getSenderUUID() {
        return UUID.fromString(sender_uuid);
    }

    public UUID getRecipientUUID() {
        return UUID.fromString(recipient_uuid);
    }

    public NetworkPlayer getSender() {
        return NetworkPlayer.resolvePlayer(getSenderUUID());
    }

    public NetworkPlayer getRecipient() {
        return NetworkPlayer.resolvePlayer(getRecipientUUID());
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender_uuid='" + sender_uuid + '\'' +
                ", sender_name='" + sender_name + '\'' +
                ", recipient_uuid='" + recipient_uuid + '\'' +
                ", recipient_name='" + recipient_name + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof Message))
            return false;

        Message msg = (Message) o;

        return Objects.equals(sender_uuid, msg.recipient_uuid)
                && Objects.equals(recipient_uuid, msg.recipient_uuid)
                && Objects.equals(content, msg.content)
                && timestamp == msg.timestamp;
    }
}
