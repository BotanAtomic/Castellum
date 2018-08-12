package org.castellum.network.handler.field;

import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.castellum.utils.NetworkUtils;
import org.castellum.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class RemoveField implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            boolean valid;
            try {
                String database = NetworkUtils.getDatabase(session);

                String table = session.getInputStream().readUTF();

                String fieldName = session.getInputStream().readUTF();

                File configuration = Utils.getConfiguration(database, table);

                valid = !database.isEmpty() && !table.isEmpty() && configuration.exists();

                if (valid) {
                    JSONArray fields = new JSONArray(Utils.getStringConfiguration(database, table));

                    int fieldsLength = fields.length();

                    for (int i = 0; i < fieldsLength; i++) {
                        if (fields.getJSONObject(i).has(fieldName)) {
                            fields.remove(i);
                            Files.write(configuration.toPath(), fields.toString().getBytes());
                            break;
                        }
                    }

                    File[] values = Utils.getValues(database, table);

                    if (values != null && values.length > 0) {
                        for (File value : values) {
                            JSONObject jsonValue = new JSONObject(Utils.toString(value));
                            jsonValue.remove(fieldName);

                            if (jsonValue.length() == 0) {
                                Files.deleteIfExists(value.toPath());
                            } else
                                Files.write(value.toPath(), jsonValue.toString().getBytes());
                        }
                    }


                }


            } catch (IOException | JSONException e) {
                Logger.writeError(e);
                valid = false;
            }

            session.writeReturnResponse(valid);

        }
    }

}
