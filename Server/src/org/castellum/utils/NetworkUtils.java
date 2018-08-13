package org.castellum.utils;

import org.castellum.entity.Database;
import org.castellum.network.CastellumSession;

import java.io.IOException;

public class NetworkUtils {

    public static Database getDatabase(CastellumSession session) throws IOException {
        String database =
                session.getInputStream().readBoolean() ? session.getInputStream().readUTF() : session.getDatabase();

        return session.getRoot().loadDatabase(database, session);
    }

}
