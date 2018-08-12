package org.castellum.network;

import org.castellum.api.Configuration;
import org.castellum.logger.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CastellumServer extends Thread {

    private Executor executor = Executors.newCachedThreadPool();

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

    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    @Override
    public void run() {
        try {

            ServerSocket server = new ServerSocket();
            server.bind(new InetSocketAddress(port));


            Logger.println("Castellum server successfully started on port $", port);

            while (server.isBound()) {
                Socket socket = server.accept();

                if (sessions.size() < maxConnection)
                    executor.execute(new CastellumSession(socket, this).listen());
                else
                    socket.close();
            }

        } catch (IOException e) {
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
    }

    public ScriptEngine getEngine() {
        return engine;
    }
}
