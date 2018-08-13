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

public class CreateTable implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            boolean valid;
            try {
                Database database = NetworkUtils.getDatabase(session);
                String tableName = session.getInputStream().readUTF();

                Path path = Paths.get("database/" + database.getName() + "/" + tableName);

                valid = !Files.exists(path) && !tableName.isEmpty();

                if (valid && new File(path.toString()).mkdirs()) {
                    Table table = new Table(tableName, database);
                    database.put(tableName, table);
                    Files.write(table.getConfigurationPath(), "[]".getBytes());
                }

            } catch (IOException e) {
                e.printStackTrace();
                valid = false;
            }

            session.writeReturnResponse(valid);

        }
    }

}
