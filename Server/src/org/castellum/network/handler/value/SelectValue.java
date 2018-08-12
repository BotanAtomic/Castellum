package org.castellum.network.handler.value;

import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.castellum.utils.NetworkUtils;
import org.castellum.utils.Utils;
import org.castellum.utils.filter.Filter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class SelectValue implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            try {
                String database = NetworkUtils.getDatabase(session);
                String table = session.getInputStream().readUTF();

                File[] values = Utils.getValues(database, table);

                byte fieldSize = session.getInputStream().readByte();

                String[] fields = new String[fieldSize];

                if (fieldSize == 0) {
                    session.getOutputStream().writeInt(values.length);

                    for (File value : values)
                        session.getOutputStream().writeUTF(Utils.toString(value));

                } else {
                    for (int i = 0; i < fieldSize; i++) {
                        fields[i] = session.getInputStream().readUTF();
                    }

                    HashSet<File> filteredFiles = new HashSet<>();

                    Filter filter = new Filter(String.join(",", fields), session.getInputStream().readUTF());

                    filter.apply(values, fieldSize, fields, filteredFiles::add);

                    session.getOutputStream().writeInt(filteredFiles.size());

                    filteredFiles.forEach(file -> {
                        try {
                            session.getOutputStream().writeUTF(Utils.toString(file));
                        } catch (Exception e) {
                            Logger.printError(e);
                        }
                    });

                }


            } catch (Exception e) {
                Logger.printError(e);
            }

        }
    }

}
