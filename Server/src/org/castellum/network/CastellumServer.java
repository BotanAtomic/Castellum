package org.castellum.network;

import org.castellum.api.Configuration;
import org.castellum.logger.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CastellumServer extends Thread {

    @Configuration("connection-limit")
    private int maxConnection;

    @Configuration("port")
    private int port;

    @Configuration("login")
    private String login;

    @Configuration("password")
    private String password;

    private final AtomicInteger sessionIdentity = new AtomicInteger(0);

    private List<CastellumSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void run() {
        try {
            AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));

            Logger.writeLn("Castellum server successfully started on port $", port);


            server.accept(null, new CompletionHandler<>() {
                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    if (sessions.size() < maxConnection) {
                        new CastellumSession(result, CastellumServer.this);
                        server.accept(null, this);
                    }
                }

                @Override
                public void failed(Throwable exc, Object attachment) {

                }
            });

            Thread.currentThread().join();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    void register(CastellumSession session) {
        this.sessions.add(session);
    }

    void unregister(CastellumSession session) {
        this.sessions.remove(session);
    }


    int nextId() {
        return sessionIdentity.incrementAndGet();
    }

    public void login(String login, String password, CastellumSession session) {
        if (login.equals(this.login) && password.equals(this.password))
            session.validateConnection();

        Logger.writeLn("Session $ connection : $", session.getId(), session.isConnected());
    }
}
