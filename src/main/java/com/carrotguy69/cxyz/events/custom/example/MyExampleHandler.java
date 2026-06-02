package com.carrotguy69.cxyz.events.custom.example;

import com.carrotguy69.cxyz.events.custom.PublicChatEvent;
import com.carrotguy69.cxyz.events.custom.base.EventHandler;
import com.carrotguy69.cxyz.events.custom.base.Priority;
import com.carrotguy69.cxyz.events.custom.service.EventService;
import com.carrotguy69.cxyz.models.db.NetworkPlayer;

public class MyExampleHandler implements EventHandler<PublicChatEvent> {

    @Override
    public boolean handle(PublicChatEvent event) {
        NetworkPlayer sender = event.getSender();
        String content = event.getContent();

        // return true if handled
        return true;
    }

    public static void testAdd() {
        EventService.addEventHandler(PublicChatEvent.class, new MyExampleHandler(), Priority.LOWEST);
    }

    public static void testCall() {
        NetworkPlayer dummy = NetworkPlayer.getPlayerByUsername("dummy");
        String content = "Hello world!";

        PublicChatEvent e = new PublicChatEvent(dummy, content);
        EventService.dispatch(e);
    }
}
