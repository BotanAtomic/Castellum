package org.castellum.configuration;

import org.castellum.generator.Configuration;
import org.castellum.json.JSONUtils;
import org.castellum.logger.Logger;
import org.castellum.utils.Utils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationBinder {

    private final static String CONFIGURATION_FILE = "server-configuration.json";

    private static final Map<String, Object> configurationData = new HashMap<>();

    static {
        try {
            JSONUtils.implementInMap(configurationData, new JSONObject(Utils.toString(new File(CONFIGURATION_FILE))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static <T> T inject(T instance) {

        Field[] fields = instance.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Configuration.class)) {
                field.setAccessible(true);
                String value = field.getAnnotation(Configuration.class).value();

                try {
                    field.set(instance, configurationData.get(value));
                } catch (IllegalAccessException e) {
                    Logger.printError(e);
                    System.exit(0);
                }
            }
        }

        return instance;
    }

}
