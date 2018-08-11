package org.castellum.network;

import org.castellum.logger.Logger;
import org.castellum.network.handler.MessageParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class CastellumSession extends Thread {

    private int id;

    private Socket channel;

    private CastellumServer server;

    private final MessageParser messageParser = new MessageParser(this);
    private final AtomicBoolean connected = new AtomicBoolean(false), active = new AtomicBoolean(true);

    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    private String database = "";

    CastellumSession(Socket channel, CastellumServer server) throws IOException {
        this.channel = channel;
        this.server = server;
        this.id = server.nextId();

        this.inputStream = new DataInputStream(channel.getInputStream());
        this.outputStream = new DataOutputStream(channel.getOutputStream());

        server.register(this);
        Logger.println("Session $ is connected", id);
    }

    Runnable listen() {
        return () -> {
            while (active.get() && channel.isConnected()) {
                try {
                    byte message = inputStream.readByte();
                    messageParser.parse(message);
                } catch (Exception e) {
                    disconnect();
                    //Logger.writeError(e);
                }
            }
        };
    }


    public void disconnect() {
        active.set(false);
        server.unregister(this);

        try {
            if (!channel.isClosed())
                channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Logger.println("Session $ is disconnected", id);
    }

    public int getSessionId() {
        return id;
    }

    public CastellumServer getRoot() {
        return server;
    }

    void validateConnection() {
        connected.set(true);
    }

    public boolean isConnected() {
        return connected.get();
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public void writeReturnResponse(boolean valid) {
        try {
            outputStream.writeBoolean(valid);
        } catch (Exception e) {
            Logger.writeError(e);
        }
    }

    public void writeError(short error) {
        try {
            outputStream.writeShort(error);
        } catch (Exception e) {
            Logger.writeError(e);
        }
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
