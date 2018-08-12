package org.castellum.network.handler.value;

import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.castellum.utils.NetworkUtils;
import org.castellum.utils.Utils;
import org.castellum.utils.filter.Filter;

import java.io.File;
import java.nio.file.Files;

public class RemoveValue implements NetworkHandler {
    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            boolean valid = false;
            try {
                String database = NetworkUtils.getDatabase(session);
                String table = session.getInputStream().readUTF();

                File[] values = Utils.getValues(database, table);

                byte fieldSize = session.getInputStream().readByte();

                String[] fields = new String[fieldSize];

                if (fieldSize == 0) {
                    for (File value : values) {
                        Files.deleteIfExists(value.toPath());
                    }
                } else {
                    for (int i = 0; i < fieldSize; i++) {
                        fields[i] = session.getInputStream().readUTF();
                    }

                    Filter filter = new Filter(String.join(",", fields), session.getInputStream().readUTF());

                    filter.apply(values, fieldSize, fields, (file) -> Files.deleteIfExists(file.toPath()));

                }



                valid = true;
            } catch (Exception e) {
                Logger.printError(e);
            }

            session.writeReturnResponse(valid);
        }
    }

}