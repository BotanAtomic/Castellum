package org.castellum.network.handler.table;

import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class RemoveTable implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            boolean valid;
            try {
                String database = session.getInputStream().readUTF();
                String table = session.getInputStream().readUTF();

                Path path = Paths.get("database/" + database + "/" + table);

                valid = Files.exists(path) && !table.isEmpty();

                if (valid)
                    Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);

                valid = !Files.exists(path);

            } catch (IOException e) {
                e.printStackTrace();
                valid = false;
            }

            session.writeReturnResponse(valid);

        }
    }

}
