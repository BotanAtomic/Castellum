package org.castellum.network;

import org.castellum.network.handler.LoginHandler;
import org.castellum.network.handler.NetworkHandler;

import java.nio.channels.AsynchronousSocketChannel;

class MessageParser {

    private NetworkHandler[] handlers = {new LoginHandler()};

    void parse(byte id, CastellumSession session, AsynchronousSocketChannel channel) {
        if (id <= handlers.length)
            handlers[id].handle(session, channel);
    }

}
