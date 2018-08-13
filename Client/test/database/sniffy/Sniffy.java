package database.sniffy;

import database.sniffy.tables.Screensaver;
import org.castellum.entity.CastellumDatabase;

public class Sniffy implements CastellumDatabase {

    public static Screensaver SCREENSAVER = new Screensaver();

    @Override
    public String getName() {
        return "sniffy";
    }
}
