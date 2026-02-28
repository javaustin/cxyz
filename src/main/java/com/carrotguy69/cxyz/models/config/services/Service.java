package com.carrotguy69.cxyz.models.config.services;

import static com.carrotguy69.cxyz.CXYZ.*;

public class Service {
    private final String ip;
    private final String identifier;
    private final String secret;

    public Service(String identifier, String ip, String secret) {
        this.identifier = identifier;
        this.ip = ip;
        this.secret = secret;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getIP() {
        return ip;
    }

    public String getSecret() {
        return secret;
    }

    public String toString() {
        return "Service{" +
                "identifier=" + identifier + "," +
                "ip=" + ip + "," +
                "secret=" + secret +
                "}";
    }

    public static Service getServerByIdentifier(String identifier) {
        GameServer server = GameServer.getServerFromName(identifier);

        if (server != null) {
            return server;
        }

        if (apiIdentifier.equals(identifier)) {
            return new Service(apiIdentifier, apiEndpoint, apiSecret);
        }

        return null;
    }
}
