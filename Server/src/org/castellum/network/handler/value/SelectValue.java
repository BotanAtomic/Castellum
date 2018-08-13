package org.castellum.network.handler.value;

import org.castellum.entity.Database;
import org.castellum.entity.Table;
import org.castellum.entity.Value;
import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.castellum.utils.NetworkUtils;
import org.castellum.utils.filter.Filter;

import java.util.HashSet;

public class SelectValue implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        if (session.isConnected()) {
            try {
                Database database = NetworkUtils.getDatabase(session);
                Table table = database.get(session.getInputStream().readUTF());


                byte fieldSize = session.getInputStream().readByte();

                String[] fields = new String[fieldSize];

                if (fieldSize == 0) {
                    session.getOutputStream().writeInt(table.size());

                    for (Value value : table)
                        session.getOutputStream().writeUTF(value.toString());

                } else {
                    for (int i = 0; i < fieldSize; i++) {
                        fields[i] = session.getInputStream().readUTF();
                    }

                    HashSet<Value> filteredValues = new HashSet<>();

                    Filter filter = new Filter(String.join(",", fields), session.getInputStream().readUTF());

                    filter.apply(table, fieldSize, fields, (filteredValues::add));

                    session.getOutputStream().writeInt(filteredValues.size());

                    filteredValues.forEach(value -> {
                        try {
                            session.getOutputStream().writeUTF(value.toString());
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
