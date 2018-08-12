package org.castellum.network.handler.value;

import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.network.api.NetworkHandler;
import org.castellum.utils.NetworkUtils;
import org.castellum.utils.Utils;
import org.json.JSONObject;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
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


                    String function = "function call(" + String.join(",", fields) + ") {" +
                            "return " + "" + session.getInputStream().readUTF() + "" + ";" +
                            "}";

                    Compilable baseCompilable = (Compilable) session.getEngine();
                    CompiledScript compiledScript = baseCompilable.compile(function);

                    compiledScript.eval();

                    Invocable invocable = (Invocable) compiledScript.getEngine();


                    if (values != null)
                        for (File value : values) {
                            JSONObject object = new JSONObject(Utils.toString(value));

                            Object[] args = new Object[fieldSize];

                            for (int i = 0; i < fieldSize; i++) {
                                args[i] = object.get(fields[i]);
                            }

                            if ((boolean) invocable.invokeFunction("call", args))
                                Files.deleteIfExists(value.toPath());
                        }
                }


                valid = true;
            } catch (Exception e) {
                Logger.writeError(e);
            }

            session.writeReturnResponse(valid);
        }
    }

}
