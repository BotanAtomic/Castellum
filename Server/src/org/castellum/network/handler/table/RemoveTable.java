package org.castellum.network.handler.table;

import org.castellum.entity.Database;
import org.castellum.entity.Table;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.castellum.utils.NetworkUtils;

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
                Database database = NetworkUtils.getDatabase(session);
                Table table = database.get(session.getInputStream().readUTF());

                Path path = Paths.get("database/" + database + "/" + table);

                valid = Files.exists(path) && !table.isEmpty();

                if (valid) {
                    Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                    database.remove(table.getName());
                }
                valid = !Files.exists(path);

            } catch (IOException e) {
                e.printStackTrace();
                valid = false;
            }

            session.writeReturnResponse(valid);

        }
    }

}
