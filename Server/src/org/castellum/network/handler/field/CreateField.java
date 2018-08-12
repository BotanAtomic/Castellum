package org.castellum.network.handler.field;

import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.castellum.utils.NetworkUtils;
import org.castellum.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CreateField implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            boolean valid;
            try {
                String database = NetworkUtils.getDatabase(session);

                String table = session.getInputStream().readUTF();
                String fieldName = session.getInputStream().readUTF();

                byte type = session.getInputStream().readByte();

                File configuration = Utils.getConfiguration(database, table);

                valid = !database.isEmpty() && !table.isEmpty() && configuration.exists();

                if (valid) {
                    JSONArray fields = new JSONArray(Utils.getStringConfiguration(database, table));

                    int fieldsLength = fields.length();

                    for (int i = 0; i < fieldsLength; i++) {
                        if (fields.getJSONObject(i).has(fieldName)) {
                            valid = false;
                            break;
                        }
                    }

                    if (valid) {
                        fields.put(new JSONObject() {{
                            put(fieldName, String.valueOf(type));
                        }});


                        Files.write(configuration.toPath(), fields.toString().getBytes());
                    }
                }


            } catch (IOException e) {
                Logger.printError(e);
                valid = false;
            }

            session.writeReturnResponse(valid);

        }
    }

}
