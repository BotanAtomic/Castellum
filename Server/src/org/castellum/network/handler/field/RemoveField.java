package org.castellum.network.handler.field;

import org.castellum.entity.Database;
import org.castellum.entity.Table;
import org.castellum.entity.Value;
import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.castellum.utils.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.nio.file.Files;

public class RemoveField implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            boolean valid;
            try {
                Database database = NetworkUtils.getDatabase(session);
                Table table = database.get(session.getInputStream().readUTF());

                String fieldName = session.getInputStream().readUTF();


                valid = !database.isEmpty() && !table.isEmpty();

                if (valid) {
                    JSONArray fields = table.getFields();

                    int fieldsLength = fields.length();

                    for (int i = 0; i < fieldsLength; i++) {
                        if (fields.getJSONObject(i).has(fieldName)) {
                            fields.remove(i);
                            Files.write(table.getConfigurationPath(), fields.toString().getBytes());
                            table.reload();
                            break;
                        }
                    }


                    for (Value value : table) {
                        value.remove(fieldName);

                        if (value.length() == 0) {
                            Files.deleteIfExists(value.getFile().toPath());
                        } else
                            Files.write(value.getFile().toPath(), value.toString().getBytes());
                    }

                }


            } catch (IOException | JSONException e) {
                Logger.printError(e);
                valid = false;
            }

            session.writeReturnResponse(valid);

        }
    }

}
