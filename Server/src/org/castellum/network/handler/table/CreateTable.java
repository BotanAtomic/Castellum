package org.castellum.network.handler.table;

import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateTable implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            boolean valid;
            try {
                String database = session.getInputStream().readBoolean() ? session.getInputStream().readUTF()
                        : session.getDatabase();

                String table = session.getInputStream().readUTF();

                Path configurationPath = Paths.get("database/" + database + "/" + table + "/" + "configuration");
                Path path = Paths.get("database/" + database + "/" + table);

                valid = !Files.exists(path) && !table.isEmpty();

                if (valid && new File(path.toString()).mkdirs())
                    Files.write(configurationPath, "[]".getBytes());


            } catch (IOException e) {
                e.printStackTrace();
                valid = false;
            }

            session.writeReturnResponse(valid);

        }
    }

}
