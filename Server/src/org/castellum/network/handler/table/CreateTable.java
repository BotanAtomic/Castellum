package org.castellum.network.handler.table;

import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.castellum.utils.NetworkUtils;
import org.castellum.utils.Utils;

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
                String database = NetworkUtils.getDatabase(session);

                String table = session.getInputStream().readUTF();

                File configurationPath = Utils.getConfiguration(database, table);
                Path path = Paths.get("database/" + database + "/" + table);

                valid = !Files.exists(path) && !table.isEmpty();

                if (valid && new File(path.toString()).mkdirs())
                    Files.write(configurationPath.toPath(), "[]".getBytes());


            } catch (IOException e) {
                e.printStackTrace();
                valid = false;
            }

            session.writeReturnResponse(valid);

        }
    }

}
