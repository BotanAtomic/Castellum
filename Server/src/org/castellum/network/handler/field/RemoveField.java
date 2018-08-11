package org.castellum.network.handler.field;

import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.json.JSONArray;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RemoveField implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            boolean valid;
            try {
                boolean databaseSpecified = session.getInputStream().readBoolean();

                String database = databaseSpecified ? session.getInputStream().readUTF() : session.getDatabase();


                String table = session.getInputStream().readUTF();

                String fieldName = session.getInputStream().readUTF();

                Path configuration = Paths.get("database/" + database + "/" + table + "/configuration");

                valid = !database.isEmpty() && !table.isEmpty() && Files.exists(configuration);

                if (valid) {
                    JSONArray fields = new JSONArray(new String(Files.readAllBytes(configuration)));

                    int fieldsLength = fields.length();

                    for (int i = 0; i < fieldsLength; i++) {
                        if (fields.getJSONObject(i).has(fieldName)) {
                            fields.remove(i);
                            Files.write(configuration, fields.toString().getBytes());
                            break;
                        }
                    }


                }


            } catch (IOException e) {
                Logger.writeError(e);
                valid = false;
            }

            session.writeReturnResponse(valid);

        }
    }

}
