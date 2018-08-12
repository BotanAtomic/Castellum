package org.castellum.network.handler.database;

import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SelectDatabase implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            try {
                String database = session.getInputStream().readUTF();

                boolean valid = Files.exists(Paths.get("database/" + database));

                session.writeReturnResponse(valid);

                if (valid)
                    session.setDatabase(database);

            } catch (IOException e) {
                session.disconnect();
                Logger.printError(e);
            }
        }
    }
}
