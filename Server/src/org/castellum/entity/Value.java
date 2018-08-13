package org.castellum.entity;

import org.castellum.utils.Utils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class Value extends JSONObject {

    private File file;

    public Value(File file) throws IOException {
        super(Utils.toString(file));
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
