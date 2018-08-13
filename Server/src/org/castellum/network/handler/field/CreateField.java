package org.castellum.network.handler.field;

import org.castellum.entity.Database;
import org.castellum.entity.Table;
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
import java.nio.file.Paths;

public class CreateField implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            boolean valid;
            try {
                Database database = NetworkUtils.getDatabase(session);
                Table table = database.get(session.getInputStream().readUTF());

                String fieldName = session.getInputStream().readUTF();

                byte type = session.getInputStream().readByte();


                valid = !database.isEmpty() && !table.isEmpty();

                if (valid) {
                    JSONArray fields = table.getFields();

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


                        Files.write(table.getConfigurationPath(), fields.toString().getBytes());

                        table.reload();
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
