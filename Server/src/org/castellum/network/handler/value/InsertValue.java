package org.castellum.network.handler.value;

import org.castellum.fields.Field;
import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.castellum.utils.NetworkUtils;
import org.castellum.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InsertValue implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            boolean valid = false;
            try {

                String database = NetworkUtils.getDatabase(session);

                String table = session.getInputStream().readUTF();

                short size = session.getInputStream().readShort();

                for (short j = 0; j < size; j++) {
                    byte fieldsSize = session.getInputStream().readByte();

                    JSONArray fields = new JSONArray(Utils.getStringConfiguration(database, table));

                    JSONObject values = new JSONObject();

                    for (int i = 0; i < fieldsSize; i++) {
                        String field = session.getInputStream().readUTF();
                        Field fieldType = Utils.getFieldType(fields, field);

                        if (fieldType == null) {
                            throw new IOException();
                        } else {
                            values.put(field, Utils.getObjectFromField(session, fieldType));
                        }
                    }

                    Files.write(Paths.get("database/" + database + "/" + table + "/" +
                                    System.nanoTime()),
                            values.toString().getBytes());
                }


                valid = true;

            } catch (Exception e) {
                e.printStackTrace();
                Logger.printError(e);
            }

            session.writeReturnResponse(valid);
        }
    }

}
