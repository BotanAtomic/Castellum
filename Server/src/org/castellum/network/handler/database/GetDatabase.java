package org.castellum.network.handler.database;

import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;

import java.io.File;

public class GetDatabase implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            try {

                File[] databases = new File("database/").listFiles();

                if (databases != null) {
                    session.getOutputStream().writeShort(databases.length);
                    for (File database : databases)
                        session.getOutputStream().writeUTF(database.getName());
                }

            } catch (Exception e) {
                Logger.printError(e);
            }
        }
    }

}
