package org.castellum.network;

import org.castellum.logger.Logger;
import org.castellum.network.api.BufferReader;
import org.castellum.network.protocol.Protocol;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicBoolean;

public class CastellumSession {

    private int id;

    private AsynchronousSocketChannel channel;

    private CastellumServer server;

    private final MessageParser messageParser = new MessageParser();
    private final AtomicBoolean connected = new AtomicBoolean(false);

    public CastellumSession(AsynchronousSocketChannel channel, CastellumServer server) {
        this.channel = channel;
        this.server = server;
        this.id = server.nextId();

        server.register(this);
        Logger.writeLn("Session $ is connected", id);

        read();
    }

    private void read(ByteBuffer byteBuffer) {
        channel.read(byteBuffer, byteBuffer, new CompletionHandler<>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1)
                    disconnect();
                else {
                    try {
                        byte message = byteBuffer.get(0);
                        Logger.writeLn("Session $ parse message id $", id, message);
                        messageParser.parse(message, CastellumSession.this, channel);
                    } catch (Exception e) {
                        Logger.writeError(e);
                    } finally {
                        read();
                    }
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
                read();
            }
        });
    }

    private void read() {
        read(ByteBuffer.allocate(Protocol.MESSAGE_SIZE));
    }

    public void nativeRead(ByteBuffer byteBuffer, BufferReader bufferReader) {
        channel.read(byteBuffer, byteBuffer, new CompletionHandler<>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    disconnect();
                } else {
                    bufferReader.read(attachment);
                    attachment.clear();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                attachment.clear().reset();
            }
        });
    }


    private void disconnect() {
        server.unregister(this);
        Logger.writeLn("Session $ is disconnected", id);
    }

    int getId() {
        return id;
    }

    public CastellumServer getRoot() {
        return server;
    }

    void validateConnection() {
        connected.set(true);
    }

    boolean isConnected() {
        return connected.get();
    }
}
