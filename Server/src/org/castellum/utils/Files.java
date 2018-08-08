package org.castellum.utils;

import org.castellum.logger.Logger;

import java.io.File;
import java.io.IOException;

public class Files {

    public static String toString(File file) {
        try {
            return new String(java.nio.file.Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            Logger.writeError(e);
            return "";
        }
    }

}
