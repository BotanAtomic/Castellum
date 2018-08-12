package org.castellum.utils.filter;

import org.castellum.utils.Utils;
import org.json.JSONObject;

import javax.script.*;
import java.io.File;

public class Filter {
    private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    private final Invocable function;

    public Filter(String fields, String condition) throws Exception {
        String function = "function call(" + String.join(",", fields) + ") {" +
                "return " + "" + condition + "" + ";" +
                "}";

        Compilable baseCompilable = (Compilable) engine;
        CompiledScript compiledScript = baseCompilable.compile(function);

        compiledScript.eval();

        this.function = (Invocable) compiledScript.getEngine();
    }

    public void apply(File[] files, byte fieldSize, String[] fields, FilterHandler handler) throws Exception {
        if (files != null)
            for (File value : files) {
                JSONObject object = new JSONObject(Utils.toString(value));

                Object[] args = new Object[fieldSize];

                for (int i = 0; i < fieldSize; i++) {
                    args[i] = object.get(fields[i]);
                }

                if ((boolean) function.invokeFunction("call", args))
                    handler.filter(value);
            }
    }


}
