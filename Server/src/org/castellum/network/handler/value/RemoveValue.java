package org.castellum.network.handler.value;

import org.castellum.entity.Database;
import org.castellum.entity.Table;
import org.castellum.entity.Value;
import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.castellum.utils.NetworkUtils;
import org.castellum.utils.filter.Filter;

import java.nio.file.Files;

public class RemoveValue implements NetworkHandler {
    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            boolean valid = false;
            try {
                Database database = NetworkUtils.getDatabase(session);
                Table table = database.get(session.getInputStream().readUTF());

                byte fieldSize = session.getInputStream().readByte();

                String[] fields = new String[fieldSize];

                if (fieldSize == 0) {
                    for (Value value : table)
                        Files.deleteIfExists(value.getFile().toPath());

                } else {
                    for (int i = 0; i < fieldSize; i++) {
                        fields[i] = session.getInputStream().readUTF();
                    }

                    Filter filter = new Filter(String.join(",", fields), session.getInputStream().readUTF());

                    filter.apply(table, fieldSize, fields, (file) -> Files.deleteIfExists(file.getFile().toPath()));

                }


                valid = true;
            } catch (Exception e) {
                Logger.printError(e);
            }

            session.writeReturnResponse(valid);
        }
    }

}
