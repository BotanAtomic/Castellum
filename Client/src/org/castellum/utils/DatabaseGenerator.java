package org.castellum.utils;

import javafx.util.Pair;
import org.castellum.exception.CastellumException;
import org.castellum.field.Field;
import org.castellum.network.CastellumClient;
import org.castellum.source.CastellumDataSource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class DatabaseGenerator {

    public static void generate(CastellumDataSource dataSource, String sourceFolder, String packageName)
            throws CastellumException, IOException {
        CastellumClient client = CastellumClient.login(dataSource);

        if (client != null) {

            String basePath = sourceFolder + "/" + packageName.replaceAll("\\.", "/") + "/";

            if (!Files.exists(Paths.get(basePath)))
                Files.createDirectory(Paths.get(basePath));

            List<String> databases = client.getDatabases();

            Files.write(Paths.get(basePath + "DatabaseRepository.java"),
                    generateRepositoryClass(databases, packageName).getBytes());

            for (String database : databases) {
                Path path = Paths.get(basePath + database);
                Path databasePath = Paths.get(basePath + database + "/" + capitalize(database) + ".java");

                if (Files.notExists(path))
                    Files.createDirectory(path);

                List<Pair<String, JSONArray>> tables = client.getTables(database);

                for (Pair<String, JSONArray> table : tables) {
                    String tableName = table.getKey();
                    Path tablePath = Paths.get(path.toString() + "/" + tableName);

                    if (Files.notExists(tablePath))
                        Files.createDirectory(tablePath);

                    Files.write(Paths.get(tablePath.toString() + "/" + capitalize(tableName) + ".java"),
                            generateTableClass(database, tableName, packageName, table.getValue()).getBytes());
                }

                Files.write(databasePath, generateDatabaseClass(database, packageName, tables).getBytes());
            }

        } else {
            throw new CastellumException("Cannot login to server");
        }
    }

    private static String generateRepositoryClass(List<String> databases, String packageName) {
        StringBuilder builder = new StringBuilder();

        if (!packageName.isEmpty())
            builder.append("package ").append(packageName).append(";\n\n");


        databases.forEach(database -> builder.append("import ").append(packageName).append(".")
                .append(database).append(".").append(capitalize(database)).append(";\n"));

        builder.append("\npublic class DatabaseRepository {\n\n");

        databases.forEach(database -> builder.append("  public static ").append(capitalize(database)).append(" ")
                .append(database.toUpperCase()).append(" = ").append("new ").append(capitalize(database)).append("();\n\n"));

        builder.append("}");

        return builder.toString();

    }


    private static String generateDatabaseClass(String database, String packageName, List<Pair<String, JSONArray>> tables) {

        StringBuilder builder = new StringBuilder();

        if (!packageName.isEmpty())
            builder.append("package ").append(packageName).append(".").append(database.toLowerCase()).append(";\n\n");


        for (Pair<String, JSONArray> table : tables) {
            String tableName = table.getKey().toLowerCase();

            builder.append("import org.castellum.entity.CastellumDatabase;\nimport ").append(packageName).append(".")
                    .append(database).append(".").append(tableName)
                    .append(".").append(capitalize(tableName)).append(";\n");
        }

        builder.append("\npublic class ").append(capitalize(database)).append(" implements CastellumDatabase {\n\n");

        tables.forEach(table -> builder.append("    public static ").append(capitalize(table.getKey())).append(" ")
                .append(table.getKey().toUpperCase()).append(" = new ")
                .append(capitalize(table.getKey())).append("();\n\n"));


        builder.append("    @Override\n");
        builder.append("    public String getName() {\n");
        builder.append("        return \"").append(database).append("\";\n");
        builder.append("    }\n");

        builder.append("}");
        return builder.toString();

    }

    private static String generateTableClass(String database, String table, String packageName, JSONArray fields) {
        database = database.toLowerCase();

        StringBuilder builder = new StringBuilder();

        if (!packageName.isEmpty())
            builder.append("package ").append(packageName).append(".").append(database)
                    .append(".").append(table.toLowerCase()).append(";\n\n");

        builder.append("import org.castellum.entity.CastellumTable;" +
                "\nimport org.castellum.entity.CastellumField;" +
                "\nimport org.castellum.field.Field;\n\n");

        builder.append("public class ").append(capitalize(table)).append(" implements CastellumTable {\n\n");

        fields.forEach(object -> {
            JSONObject field = (JSONObject) object;
            String key = field.keys().next();
            builder.append("    public static CastellumField ").append(key.toUpperCase())
                    .append(" = new CastellumField(\"").append(key).append("\", Field.")
                    .append(Field.values()[field.getInt(key)].name()).append(");\n\n");

        });

        builder.append("    @Override\n");
        builder.append("    public String getName() {\n");
        builder.append("        return \"").append(table).append("\";\n");
        builder.append("    }\n");

        builder.append("}");
        return builder.toString();

    }

    private static String capitalize(String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1).toLowerCase();
    }


}
