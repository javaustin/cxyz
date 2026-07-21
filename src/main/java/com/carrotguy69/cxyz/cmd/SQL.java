package com.carrotguy69.cxyz.cmd;

import com.carrotguy69.cxyz.CXYZ;
import com.carrotguy69.cxyz.http.Request;
import com.carrotguy69.cxyz.http.RequestResult;
import com.carrotguy69.cxyz.messages.MessageKey;
import com.carrotguy69.cxyz.messages.MessageUtils;
import com.carrotguy69.cxyz.other.Logger;
import com.carrotguy69.cxyz.utils.CommandRestrictor;
import com.carrotguy69.cxyz.utils.JsonConverters;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.carrotguy69.cxyz.CXYZ.apiEndpoint;
import static com.carrotguy69.cxyz.CXYZ.gson;

public class SQL implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        String node = "cxyz.sql";

        if (!sender.hasPermission(node)) {
            MessageUtils.sendParsedMessage(sender, MessageKey.COMMAND_NO_ACCESS, Map.of("permission", node));
            return true;
        }

        if (CommandRestrictor.handleRestricted(command, sender)) {
            return true;
        }

        if (args.length == 0) {
            MessageUtils.sendParsedMessage(sender, MessageKey.MISSING_GENERAL, Map.of("missing-args", "query"));
            return true;
        }

        String query = String.join(" ", args);

        boolean confirmFlag = query.contains("-confirm");


        if (!confirmFlag) {
            MessageUtils.sendParsedMessage(sender, MessageKey.SQL_CONFIRM_PROMPT, Map.of("query", query));
            return true;
        }

        query = query.replace("-confirm", "").strip();

        MessageUtils.sendParsedMessage(sender, MessageKey.SQL_SENDING, Map.of());

        CompletableFuture<RequestResult> req = Request.postRequest(apiEndpoint + "/sql", gson.toJson(Map.of("query", query)));

        req.thenAccept(result -> {
            Logger.log("hellooooooo" + result.statusCode);

            if (result.statusCode >= 400) {
                Object error = result.responseBody;

                try {
                    Map<String, Object> body = JsonConverters.toMap(result.responseBody);
                    if (body != null) {
                        error = body.getOrDefault("error", body.getOrDefault("message", result.responseBody));
                    }
                }
                catch (RuntimeException ignored) {
                }

                if (error == null) {
                    error = "Unknown error";
                }

                Object errorMessage = error;
                Bukkit.getScheduler().runTask(CXYZ.plugin, () -> MessageUtils.sendParsedMessage(sender, MessageKey.SQL_ERROR, Map.of("error", errorMessage)));
                return;
            }

            String body = result.responseBody == null ? "" : result.responseBody;
            Bukkit.getScheduler().runTask(CXYZ.plugin, () -> MessageUtils.sendParsedMessage(sender, MessageKey.SQL_SUCCESS, Map.of("body", body)));

        }).exceptionally(ex -> {
            Logger.logStackTrace((Exception) ex);
            Bukkit.getScheduler().runTask(CXYZ.plugin, () -> MessageUtils.sendParsedMessage(sender, MessageKey.API_ERROR, Map.of()));
            return null;
        });


        return true;
    }
}
