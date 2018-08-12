package org.castellum.utils;

import org.castellum.network.CastellumSession;

public class NetworkUtils {

    public static String getDatabase(CastellumSession session) {
        try {
            return session.getInputStream().readBoolean() ? session.getInputStream().readUTF() : session.getDatabase();
        } catch (Exception e) {
            return "";
        }
    }

}
