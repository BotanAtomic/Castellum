package database.sniffy.tables;

import org.castellum.entity.CastellumField;
import org.castellum.entity.CastellumTable;
import org.castellum.field.Field;

public class Screensaver  implements CastellumTable {

    @Override
    public String getName() {
        return "screensaver";
    }

    public static CastellumField ID = new CastellumField("id", Field.INTEGER);

    public static CastellumField PATH = new CastellumField("path", Field.STRING);

    public static CastellumField SHORT_VALUE = new CastellumField("short_value", Field.SHORT);

    public static CastellumField VALID = new CastellumField("valid", Field.BOOLEAN);

    public static CastellumField PRICE = new CastellumField("price", Field.DOUBLE);

}
