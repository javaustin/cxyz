package com.carrotguy69.cxyz.models.db;


import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.other.utils.TimeUtils;


import java.util.Objects;
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

    public static Message getLastSent(UUID senderUUID) {
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

    public Message(NetworkPlayer sender, NetworkPlayer recipient, String content) {
        this.sender_uuid = sender.getUUID().toString();
        this.sender_name = sender.getUsername();
        this.recipient_uuid = recipient.getUUID().toString();
        this.recipient_name = recipient.getUsername();
        this.content = content;
        this.timestamp = TimeUtils.unixTimeNow();
    }

    public Message(String sender_uuid, String sender_name, String recipient_uuid, String recipient_name, String content, long timestamp) {
        this.sender_uuid = sender_uuid;
        this.sender_name = sender_name;
        this.recipient_uuid = recipient_uuid;
        this.recipient_name = recipient_name;
        this.content = content;
        this.timestamp = timestamp;
    }

    public void submit() {
        Request.postRequest(api_endpoint + "/message/submit", gson.toJson(this));
    }

    public UUID getSenderUUID() {
        return UUID.fromString(sender_uuid);
    }

    public UUID getRecipientUUID() {
        return UUID.fromString(recipient_uuid);
    }

    public NetworkPlayer getSender() {
        return NetworkPlayer.getPlayerByUUID(getSenderUUID());
    }

    public NetworkPlayer getRecipient() {
        return NetworkPlayer.getPlayerByUUID(getRecipientUUID());
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
