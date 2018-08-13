package org.castellum.utils.filter;

import org.castellum.entity.Value;

import javax.script.*;
import java.util.Set;

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

    public void apply(Set<Value> values, byte fieldSize, String[] fields, FilterHandler handler) {
        values.forEach(object -> {
            Object[] args = new Object[fieldSize];

            for (int i = 0; i < fieldSize; i++) {
                args[i] = object.get(fields[i]);
            }

            try {
                if ((boolean) function.invokeFunction("call", args))
                    handler.filter(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
