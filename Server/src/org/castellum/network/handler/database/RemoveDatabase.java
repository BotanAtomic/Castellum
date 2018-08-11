package org.castellum.network.handler.database;

import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class RemoveDatabase implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            try {
                String database = session.getInputStream().readUTF();

                Path path = Paths.get("database/" + database);

                boolean valid = Files.exists(path) && !database.isEmpty();

                if (valid)
                    Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);

                valid = !Files.exists(path);

                session.writeReturnResponse(valid);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
