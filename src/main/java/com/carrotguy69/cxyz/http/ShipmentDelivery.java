package com.carrotguy69.cxyz.http;

import com.carrotguy69.cxyz.models.db.*;
import com.carrotguy69.cxyz.other.Logger;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.annotations.SerializedName;
import org.bukkit.entity.Player;

import java.util.*;

import static com.carrotguy69.cxyz.CXYZ.*;

public class ShipmentDelivery {


    static class NetworkPlayerShipmentWrapper {
        @SerializedName("data")
        private List<NetworkPlayer> data;

        public List<NetworkPlayer> getData() {
            return data;
        }
    }

    public static void handleNetworkPlayerShipment(String json) {
        // This is the method we call when the delivery includes an entire table. So we don't need to update any values, we will just add them from our data.

        try {
            NetworkPlayerShipmentWrapper wrapper = gson.fromJson(json, NetworkPlayerShipmentWrapper.class);


            List<NetworkPlayer> npList = wrapper.getData();

            Map<UUID, NetworkPlayer> tempMap = new HashMap<>();

            for (NetworkPlayer np : npList) {
                // Shipments will not give us data formatted in (key, object/value), it will just give us a list of values.
                // There are ways to get the keys we want from inside the class, so we will just use those built-in attributes.
                tempMap.put(np.getUUID(), np);
            }

            // stop warning me
            users.clear();
            users.putAll(tempMap);

            Logger.info("✅ NetworkPlayer table shipment received!");
        }
        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("NetworkPlayer shipment failed!! Cannot continue.");
        }
    }


    static class NetworkPlayerDeliveryWrapper {
        @SerializedName("old_data")
        private List<NetworkPlayer> old_data;

        @SerializedName("new_data")
        private List<NetworkPlayer> new_data;

        public List<NetworkPlayer> getDeletedData() {
            return old_data;
        }

        public List<NetworkPlayer> getNewData() {
            return new_data;
        }
    }

    public static void handleNetworkPlayerDelivery(String json) {
        // This is the method we call when the delivery includes a few rows. We already have data, so we will update the data by adding, modifying, or deleting rows.
        try {
            NetworkPlayerDeliveryWrapper wrapper = gson.fromJson(json, NetworkPlayerDeliveryWrapper.class);


            for (NetworkPlayer np : wrapper.getDeletedData()) {
                Logger.debugUser("[-] Deleted an entry from users! " + np.getUUID());
                users.remove(np.getUUID());
            }

            for (NetworkPlayer np : wrapper.getNewData()) {
                Player p = np.getPlayer();

                // Modifies or adds new.
                Logger.debugUser("[+] Added/Modified an entry to users. " + np.getUUID());
                users.put(np.getUUID(), np);


                if (p != null && p.isOnline() && !np.isOnline()) {
                    Logger.warning(String.format("This NetworkPlayer delivery seems to have outdated data. The Bukkit player is online but the NetworkPlayer is listed as not. NetworkPlayer: %s. Player: %s", np, p));

                    Logger.log(p.toString());
                    Logger.log(np.toString());
                }

            }

        }

        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("NetworkPlayer delivery failed!! Cannot continue.");
        }

    }





    static class PartyShipmentWrapper {
        @SerializedName("data")
        private List<Party> data;

        public List<Party> getData() {
            return data;
        }
    }

    public static void handlePartyShipment(String json) {
        // This is the method we call when the delivery includes an entire table. So we don't need to update any values, we will just add them from our data.

        try {
            PartyShipmentWrapper wrapper = gson.fromJson(json, PartyShipmentWrapper.class);


            List<Party> partyList = wrapper.getData();

            Map<UUID, Party> tempMap = new HashMap<>();

            for (Party party : partyList) {
                // Shipments will not give us data formatted in (key, object/value), it will just give us a list of values.
                // There are ways to get the keys we want from inside the class, so we will just use those built-in attributes.
                tempMap.put(party.getOwnerUUID(), party);
            }

            parties.clear();
            parties.putAll(tempMap);


            Logger.info("✅ Party table shipment received!");
        }
        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("Party shipment failed!! Cannot continue.");
        }
    }


    static class PartyDeliveryWrapper {
        @SerializedName("old_data")
        private List<Party> old_data;

        @SerializedName("new_data")
        private List<Party> new_data;

        public List<Party> getDeletedData() {
            return old_data;
        }

        public List<Party> getNewData() {
            return new_data;
        }
    }

    public static void handlePartyDelivery(String json) {
        // This is the method we call when the delivery includes a few rows. We already have data, so we will update the data by adding, modifying, or deleting rows.
        try {
            PartyDeliveryWrapper wrapper = gson.fromJson(json, PartyDeliveryWrapper.class);

            for (Party party : wrapper.getDeletedData()) {
                Logger.debugParty("[-] Deleted an entry from parties! " + party.toString());
                parties.remove(party.getOwnerUUID());
            }

            for (Party party : wrapper.getNewData()) {
                Logger.debugParty("[+] Added/Modified an entry to parties. " + party.toString());
                // Modifies or adds new.
                parties.put(party.getOwnerUUID(), party);
            }


        }
        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("Party delivery failed!! Cannot continue.");
        }
    }





    static class PartyInviteShipmentWrapper {
        @SerializedName("data")
        private List<PartyInvite> data;

        public List<PartyInvite> getData() {
            return data;
        }
    }

    public static void handlePartyInviteShipment(String json) {
        // This is the method we call when the delivery includes an entire table. So we don't need to update any values, we will just add them from our data.

        try {
            PartyInviteShipmentWrapper wrapper = gson.fromJson(json, PartyInviteShipmentWrapper.class);


            List<PartyInvite> invites = wrapper.getData();

            Multimap<UUID, PartyInvite> tempMap = ArrayListMultimap.create();

            for (PartyInvite invite : invites) {
                // Shipments will not give us data formatted in (key, object/value), it will just give us a list of values.
                // There are ways to get the keys we want from inside the class, so we will just use those built-in attributes.
                tempMap.put(invite.getRecipientUUID(), invite);
            }

            partyInvites.clear();
            partyInvites.putAll(tempMap);


            Logger.info("✅ PartyInvite table shipment received!");
        }
        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("PartyInvite shipment failed!! Cannot continue.");
        }
    }


    static class PartyInviteDeliveryWrapper {
        @SerializedName("old_data")
        private List<PartyInvite> old_data;

        @SerializedName("new_data")
        private List<PartyInvite> new_data;

        public List<PartyInvite> getDeletedData() {
            return old_data;
        }

        public List<PartyInvite> getNewData() {
            return new_data;
        }
    }

    public static void handlePartyInviteDelivery(String json) {
        // This is the method we call when the delivery includes a few rows. We already have data, so we will update the data by adding, modifying, or deleting rows.

        try {
            PartyInviteDeliveryWrapper wrapper = gson.fromJson(json, PartyInviteDeliveryWrapper.class);

            for (PartyInvite invite : wrapper.getDeletedData()) {
                Logger.debugParty("[-] Deleted an entry from partyInvites! " + invite.toString());

                partyInvites.remove(invite.getInviterUUID(), invite);
            }

            for (PartyInvite invite : wrapper.getNewData()) {

                if (partyInvites.containsKey(invite.getInviterUUID())) { // This player has at least one invite. We must see if it is a duplicate or not.
                    boolean exit = false;
                    for (PartyInvite collectionInvite : partyInvites.get(invite.getInviterUUID())) {

                        if (collectionInvite.getRecipientUUID().equals(invite.getRecipientUUID())) {
                            // The exact same invite is already in our map. We want to check for and avoid duplicates because this MultiMap will actually allow them.
                            Logger.debugParty("[~] Ignored duplicate entry from partyInvites! " + invite.toString());
                            exit = true;
                            continue;
                        }
                    }

                    if (exit)
                        continue;

                }


                // Modifies or adds new.
                Logger.debugParty("[+] Added/Modified an entry to partyInvites. " + invite.toString());
                partyInvites.put(invite.getInviterUUID(), invite);
                continue;
            }


        }
        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("PartyInvite delivery failed!! Cannot continue.");
        }
    }






    static class PartyExpireShipmentWrapper {
        @SerializedName("data")
        private List<PartyExpire> data;

        public List<PartyExpire> getData() {
            return data;
        }
    }

    public static void handlePartyExpireShipment(String json) {
        // This is the method we call when the delivery includes an entire table. So we don't need to update any values, we will just add them from our data.

        try {
            PartyExpireShipmentWrapper wrapper = gson.fromJson(json, PartyExpireShipmentWrapper.class);


            List<PartyExpire> expires = wrapper.getData();

            Map<UUID, PartyExpire> tempMap = new HashMap<>();

            for (PartyExpire expire : expires) {
                // Shipments will not give us data formatted in (key, object/value), it will just give us a list of values.
                // There are ways to get the keys we want from inside the class, so we will just use those built-in attributes.
                tempMap.put(expire.getUUID(), expire);
            }

            partyExpires.clear();
            partyExpires.putAll(tempMap);

            Logger.info("✅ PartyExpire table shipment received!");
        }
        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("PartyExpire shipment failed!! Cannot continue.");
        }
    }


    static class PartyExpireDeliveryWrapper {
        @SerializedName("old_data")
        private List<PartyExpire> old_data;

        @SerializedName("new_data")
        private List<PartyExpire> new_data;

        public List<PartyExpire> getDeletedData() {
            return old_data;
        }

        public List<PartyExpire> getNewData() {
            return new_data;
        }
    }

    public static void handlePartyExpireDelivery(String json) {
        // This is the method we call when the delivery includes a few rows. We already have data, so we will update the data by adding, modifying, or deleting rows.

        try {
            PartyExpireDeliveryWrapper wrapper = gson.fromJson(json, PartyExpireDeliveryWrapper.class);

            // Remove the old data first, and then add the new data!

            for (PartyExpire expire : wrapper.getDeletedData()) {
                Logger.debugParty("[-] Deleted an entry from partyExpires! " + expire.toString());
                partyExpires.remove(expire.getUUID(), expire);
            }

            for (PartyExpire expire : wrapper.getNewData()) {
                Logger.debugParty("[+] Added/Modified an entry to partyExpires. " + expire.toString());

                partyExpires.put(expire.getUUID(), expire);
            }


        }
        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("PartyExpire delivery failed!! Cannot continue.");
        }
    }



    static class PunishmentShipmentWrapper {
        @SerializedName("data")
        private List<Punishment> data;

        public List<Punishment> getData() {
            return data;
        }
    }

    public static void handlePunishmentShipment(String json) {
        // This is the method we call when the delivery includes an entire table. So we don't need to update any values, we will just add them from our data.

        try {
            PunishmentShipmentWrapper wrapper = gson.fromJson(json, PunishmentShipmentWrapper.class);

            List<Punishment> punishments = wrapper.getData();

            Map<Long, Punishment> tempMap2 = new HashMap<>();

            for (Punishment punishment : punishments) {
                // Shipments will not give us data formatted in (key, object/value), it will just give us a list of values.
                // There are ways to get the keys we want from inside the class, so we will just use those built-in attributes.
                tempMap2.put(punishment.getID(), punishment);
            }

            punishmentIDMap.clear();
            punishmentIDMap.putAll(tempMap2);

            Logger.info("✅ Punishment table shipment received!");

        }

        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("Punishment shipment failed!! Cannot continue.");
        }
    }


    static class PunishmentDeliveryWrapper {
        @SerializedName("old_data")
        private List<Punishment> old_data;

        @SerializedName("new_data")
        private List<Punishment> new_data;

        public List<Punishment> getDeletedData() {
            return old_data;
        }

        public List<Punishment> getNewData() {
            return new_data;
        }
    }

    public static void handlePunishmentDelivery(String json) {
        // This is the method we call when the delivery includes a few rows. We already have data, so we will update the data by adding, modifying, or deleting rows.

        try {
            PunishmentDeliveryWrapper wrapper = gson.fromJson(json, PunishmentDeliveryWrapper.class);

            for (Punishment punishment : wrapper.getDeletedData()) {
                Logger.debugPunishment("[-] Deleted an entry from punishments! " + punishment.toString());
                punishmentIDMap.remove(punishment.getID(), punishment);
            }

            for (Punishment punishment : wrapper.getNewData()) {
                // Modifies or adds new.

                Logger.debugPunishment("[+] Added/Modified an entry to partyExpires. " + punishment);
                punishmentIDMap.put(punishment.getID(), punishment);
            }


        }
        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("Punishment delivery failed!! Cannot continue.");
        }
    }










    static class MessageShipmentWrapper {

        @SerializedName("data")
        private List<Message> data;

        public List<Message> getData() {
            return data;
        }
    }

    public static void handleMessageShipment(String json) {
        // This is the method we call when the delivery includes an entire table. So we don't need to update any values, we will just add them from our data.

        try {
            MessageShipmentWrapper wrapper = gson.fromJson(json, MessageShipmentWrapper.class);


            List<Message> messageList = wrapper.getData();

            Multimap<UUID, Message> tempMap = ArrayListMultimap.create();

            for (Message message : messageList) {
                // Shipments will not give us data formatted in (key, object/value), it will just give us a list of values.
                // There are ways to get the keys we want from inside the class, so we will just use those built-in attributes.
                tempMap.put(message.getRecipientUUID(), message);
            }

            messageMap.clear();
            messageMap.putAll(tempMap);

            Logger.info("✅ Message table shipment received!");

        }
        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("Message data shipment failed!! Cannot continue.");
        }

    }

    static class MessageDeliveryWrapper {
        @SerializedName("old_data")
        private List<Message> old_data;

        @SerializedName("new_data")
        private List<Message> new_data;

        public List<Message> getDeletedData() {
            return old_data;
        }

        public List<Message> getNewData() {
            return new_data;
        }
    }

    public static void handleMessageDelivery(String json) {
        // This is the method we call when the delivery includes a few rows. We already have data, so we will update the data by adding, modifying, or deleting rows.

        try {
            MessageDeliveryWrapper wrapper = gson.fromJson(json, MessageDeliveryWrapper.class);

            for (Message msg : wrapper.getDeletedData()) {
                Logger.debugPlayerMessage("[-] Deleted an entry from messages! " + msg.toString());
                messageMap.remove(msg.getRecipientUUID(), msg);
            }


            for (Message msg : wrapper.getNewData()) {

                if (messageMap.containsKey(msg.getRecipientUUID())) { // This player has at least one invite. We must see if it is a duplicate or not.
                    for (Message collectionMsg : messageMap.get(msg.getRecipientUUID())) {
                        if (collectionMsg.getSenderUUID().equals(msg.getSenderUUID())) {
                            // The exact same message is already in our map. We want to check for and avoid duplicates because this MultiMap will actually allow them.
                            Logger.debugPlayerMessage("[~] Ignored duplicate entry from messages! " + msg);
                            continue;
                        }
                    }

                }


                // Modifies or adds new.
                Logger.debugMessage("[+] Added/Modified an entry to messages. " + msg);
                messageMap.put(msg.getRecipientUUID(), msg);
                continue;
            }


        }
        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("Punishment delivery failed!! Cannot continue.");
        }
    }




    // FriendRequest

    static class FriendRequestShipmentWrapper {
        @SerializedName("data")
        private List<FriendRequest> data;

        public List<FriendRequest> getData() {
            return data;
        }
    }

    public static void handleFriendRequestShipment(String json) {
        // This is the method we call when the delivery includes an entire table. So we don't need to update any values, we will just add them from our data.

        try {
            FriendRequestShipmentWrapper wrapper = gson.fromJson(json, FriendRequestShipmentWrapper.class);


            List<FriendRequest> requests = wrapper.getData();

            Multimap<UUID, FriendRequest> tempMap = ArrayListMultimap.create();

            for (FriendRequest request : requests) {
                // Shipments will not give us data formatted in (key, object/value), it will just give us a list of values.
                // There are ways to get the keys we want from inside the class, so we will just use those built-in attributes.
                tempMap.put(request.getRecipientUUID(), request);
            }

            friendRequests.clear();
            friendRequests.putAll(tempMap);


            Logger.info("✅ FriendRequest table shipment received!");
        }
        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("FriendRequest shipment failed!! Cannot continue.");
        }
    }


    static class FriendRequestDeliveryWrapper {
        @SerializedName("old_data")
        private List<FriendRequest> old_data;

        @SerializedName("new_data")
        private List<FriendRequest> new_data;

        public List<FriendRequest> getDeletedData() {
            return old_data;
        }

        public List<FriendRequest> getNewData() {
            return new_data;
        }
    }

    public static void handleFriendRequestDelivery(String json) {
        // This is the method we call when the delivery includes a few rows. We already have data, so we will update the data by adding, modifying, or deleting rows.


        try {
            FriendRequestDeliveryWrapper wrapper = gson.fromJson(json, FriendRequestDeliveryWrapper.class);

            for (FriendRequest request : wrapper.getDeletedData()) {
                Logger.debugFriendRequest("[-] Deleted an entry from friendRequests! " + request.toString());

                friendRequests.remove(request.getSenderUUID(), request);
            }


            for (FriendRequest request : wrapper.getNewData()) {

                if (friendRequests.containsKey(request.getSenderUUID())) { // This player has at least one invite. We must see if it is a duplicate or not.
                    boolean exit = false;
                    for (FriendRequest collectionInvite : friendRequests.get(request.getSenderUUID())) {
                        if (collectionInvite.getRecipientUUID().equals(request.getRecipientUUID())) {
                            // The exact same friend request is already in our map. We want to check for and avoid duplicates because this MultiMap will actually allow them.
                            Logger.debugFriendRequest("[~] Ignored duplicate entry from friendRequests! " + request.toString());
                            exit = true;
                            break;
                        }
                    }

                    if (exit)
                        continue;

                }


                // Modifies or adds new.
                Logger.debugFriendRequest("[+] Added/Modified an entry to friendRequests. " + request.toString());
                friendRequests.put(request.getSenderUUID(), request);
                continue;
            }


        }
        catch (Exception e) {
            Logger.logStackTrace(e);
            throw new RuntimeException("FriendRequest delivery failed!! Cannot continue.");
        }
    }


}


















