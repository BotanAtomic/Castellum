package org.castellum.entity;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class Database extends ConcurrentHashMap<String, Table> {
    private String name;

    public Database(String name) {
        this.name = name;

        File[] tables = new File("database/" + name + "/").listFiles();

        if (tables != null)
            for (File table : tables)
                put(table.getName(), new Table(table, this));
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
