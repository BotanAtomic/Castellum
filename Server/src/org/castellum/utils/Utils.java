package org.castellum.utils;

import org.castellum.fields.Field;
import org.castellum.network.CastellumSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class Utils {

    public static String toString(File file) throws IOException {
        return new String(java.nio.file.Files.readAllBytes(file.toPath()));
    }


    public static Field getFieldType(JSONArray fields, String field) {
        int size = fields.length();

        for (int i = 0; i < size; i++) {
            JSONObject object = fields.getJSONObject(i);
            if (object.has(field))
                return Field.values()[object.getInt(field)];
        }

        return null;
    }

    public static Object getObjectFromField(CastellumSession session, Field fieldType) throws IOException {
        switch (fieldType) {
            case BOOLEAN:
                return session.getInputStream().readBoolean();
            case BYTE:
                return session.getInputStream().readByte();
            case CHAR:
                return session.getInputStream().readChar();
            case SHORT:
                return session.getInputStream().readShort();
            case INTEGER:
                return session.getInputStream().readInt();
            case LONG:
                return session.getInputStream().readLong();
            case FLOAT:
                return session.getInputStream().readFloat();
            case DOUBLE:
                return session.getInputStream().readDouble();
            case STRING:
                return session.getInputStream().readUTF();

        }
        return null;
    }

}
