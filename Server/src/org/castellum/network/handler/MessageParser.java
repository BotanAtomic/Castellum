package org.castellum.network.handler;

import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.castellum.network.handler.database.CreateDatabase;
import org.castellum.network.handler.database.RemoveDatabase;
import org.castellum.network.handler.database.SelectDatabase;
import org.castellum.network.handler.field.CreateField;
import org.castellum.network.handler.field.RemoveField;
import org.castellum.network.handler.login.LoginHandler;
import org.castellum.network.handler.table.CreateTable;
import org.castellum.network.handler.table.RemoveTable;
import org.castellum.network.handler.value.InsertValue;
import org.castellum.network.handler.value.RemoveValue;
import org.castellum.network.handler.value.SelectValue;

public class MessageParser {

    private final CastellumSession session;

    public MessageParser(CastellumSession session) {
        this.session = session;
    }

    private NetworkHandler[] handlers = {
            new LoginHandler(),
            new CreateDatabase(),
            new CreateTable(),
            new CreateField(),
            new SelectDatabase(),
            new RemoveDatabase(),
            new RemoveTable(),
            new RemoveField(),
            new InsertValue(),
            new RemoveValue(),
            new SelectValue()};

    public void parse(byte id) {
        if (id <= handlers.length) {
            NetworkHandler handler = handlers[id];
            Logger.println("Session $ receive message [$]", session.getSessionId(), handler.getClass().getSimpleName());
            handler.handle(session);
        } else {
            Logger.println("Session $ receive invalid message", session.getSessionId());
        }
    }

    public static void main(String[] args) {
        int i = 0;
        for (NetworkHandler handler : new MessageParser(null).handlers) {
            System.out.println(handler.getClass().getSimpleName() + " -> " + i++);
        }
    }

}
