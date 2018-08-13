package org.castellum.entity;

import org.castellum.field.Field;

public class CastellumField {

    private final String name;
    private final Field field;

    public CastellumField(String name, Field field) {
        this.name = name;
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }
}
