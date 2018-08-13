package org.castellum.utils;

import org.castellum.exception.CastellumException;
import org.castellum.network.CastellumClient;
import org.castellum.source.CastellumDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseGenerator {

    public static void generate(CastellumDataSource dataSource, String sourceFolder, String packageName)
            throws CastellumException, IOException {
        CastellumClient client = CastellumClient.login(dataSource);

        if (client != null) {

            String basePath = sourceFolder + "/" + packageName.replaceAll("\\.", "/") + "/";

            for (String database : client.getDatabases()) {
                Files.createDirectory(Paths.get(basePath + database));

            }

        } else {
            throw new CastellumException("Cannot login to server");
        }
    }


    private static String generateDatabaseClass(String database, String packageName) {
        StringBuilder builder = new StringBuilder();

        if (!packageName.isEmpty())
            builder.append("package ").append(packageName).append(".").append(database).append("\n");


        return builder.toString();

    }



}
