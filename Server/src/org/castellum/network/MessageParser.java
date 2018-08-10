package org.castellum.network;

import org.castellum.logger.Logger;
import org.castellum.network.handler.*;

class MessageParser {

    private final CastellumSession session;

    MessageParser(CastellumSession session) {
        this.session = session;
    }

    private NetworkHandler[] handlers = {
            new LoginHandler(),
            new CreateDatabase(),
            new CreateTable(),
            new CreateField()};

    void parse(byte id) {
        if (id <= handlers.length) {
            NetworkHandler handler = handlers[id];
            Logger.println("Session $ receive message [$]", session.getSessionId(), handler.getClass().getSimpleName());
            handler.handle(session);
        } else {
            Logger.println("Session $ receive invalid message", session.getSessionId());
        }
    }

}
