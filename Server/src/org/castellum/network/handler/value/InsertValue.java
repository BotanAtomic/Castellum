package org.castellum.network.handler.value;

import org.castellum.entity.Database;
import org.castellum.entity.Table;
import org.castellum.entity.Value;
import org.castellum.fields.Field;
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
import java.nio.file.Path;

public class InsertValue implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            boolean valid = false;
            try {

                Database database = NetworkUtils.getDatabase(session);
                Table table = database.get(session.getInputStream().readUTF());

                short size = session.getInputStream().readShort();

                for (short j = 0; j < size; j++) {
                    byte fieldsSize = session.getInputStream().readByte();

                    JSONArray fields = table.getFields();

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

                    Path newValuePath = table.newFile();

                    Files.write(newValuePath, values.toString().getBytes());

                    table.add(new Value(new File(newValuePath.toString())));
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
