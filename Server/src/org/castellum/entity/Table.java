package org.castellum.entity;

import org.castellum.logger.Logger;
import org.castellum.utils.Utils;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CopyOnWriteArraySet;

public class Table extends CopyOnWriteArraySet<Value> {

    private String name;

    private JSONArray fields;

    private Database database;

    Table(File file, Database database) {
        this.name = file.getName();

        this.database = database;

        File[] values = file.listFiles();

        if (values != null) {
            for (File value : values) {
                try {
                    if (!value.getName().equals("configuration")) {
                        add(new Value(value));
                    } else {
                        fields = new JSONArray(Utils.toString(value));
                    }
                } catch (IOException e) {
                    Logger.printError(e);
                }
            }
        }
    }


    public Table(String name, Database database) {
        this.name = name;
        this.database = database;
    }

    public JSONArray getFields() {
        return fields;
    }

    public Path getConfigurationPath() {
        return Paths.get("database/" + database.getName() + "/" + this.name + "/configuration");
    }

    public void reload() throws IOException {
        this.fields = new JSONArray(Utils.toString(new File(getConfigurationPath().toString())));
    }

    public String getName() {
        return this.name;
    }

    public Path newFile() {
        return Paths.get("database/" + database.getName() + "/" + this.name + "/" + System.nanoTime());
    }

    @Override
    public String toString() {
        return this.name;
    }
}

