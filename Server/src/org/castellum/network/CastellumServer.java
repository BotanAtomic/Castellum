package org.castellum.network;

import org.castellum.generator.Configuration;
import org.castellum.entity.Database;
import org.castellum.logger.Logger;
import org.castellum.security.EncryptionUtil;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    private final Map<String, Database> databases = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> databasesIndex = new ConcurrentHashMap<>();

    @Override
    public void run() {
        try {
            EncryptionUtil.loadPrivateKey(new File("private.key"));
            Logger.println("Private RSA key successfully loaded");

            ServerSocket server = new ServerSocket();
            server.bind(new InetSocketAddress(port));

            Logger.println("Server successfully started on port $", port);

            while (server.isBound()) {
                Socket socket = server.accept();

                if (sessions.size() < maxConnection)
                    executor.execute(new CastellumSession(socket, this).listen());
                else
                    socket.close();
            }

        } catch (Exception e) {
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

    public synchronized Database loadDatabase(String databaseName, CastellumSession session) {
        Database database = null;
        if (!databases.containsKey(databaseName)) {
            database = databases.put(databaseName, new Database(databaseName));
            AtomicInteger atomicInteger;
            databasesIndex.put(databaseName, atomicInteger = databasesIndex.getOrDefault(databaseName, new AtomicInteger(0)));
            atomicInteger.incrementAndGet();
            session.addDatabase(databaseName);
            Logger.println("Database [$] successfully loaded", databaseName);
        }

        return database == null ? databases.get(databaseName) : database;
    }

    void unloadDatabase(String database) {
        AtomicInteger atomicInteger = databasesIndex.get(database);

        if (atomicInteger.decrementAndGet() < 1) {
            databases.remove(database);
            System.gc();
            Logger.println("Database [$] successfully unloaded", database);
        }
    }

}
