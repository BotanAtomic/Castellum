package org.castellum.network.handler.table;

import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.castellum.utils.Utils;

import java.io.File;

public class GetTable implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            try {

                String database = session.getInputStream().readUTF();

                File[] tables = new File("database/" + database).listFiles();

                if (tables != null) {
                    session.getOutputStream().writeShort(tables.length);
                    for (File table : tables) {
                        session.getOutputStream().writeUTF(table.getName());
                        session.getOutputStream().writeUTF(Utils.toString(new File("database/" +
                                database + "/" + table.getName() + "/configuration")));
                    }
                }

            } catch (Exception e) {
                Logger.printError(e);
            }
        }
    }

}