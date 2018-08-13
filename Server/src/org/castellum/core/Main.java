package org.castellum.core;

import org.castellum.configuration.ConfigurationBinder;
import org.castellum.logger.Logger;
import org.castellum.network.CastellumServer;

public class Main {

    public static void main(String[] args) {
        System.out.println(" _____       ___   _____   _____   _____   _       _       _   _       ___  ___  \n" +
                "/  ___|     /   | /  ___/ |_   _| | ____| | |     | |     | | | |     /   |/   | \n" +
                "| |        / /| | | |___    | |   | |__   | |     | |     | | | |    / /|   /| | \n" +
                "| |       / / | | \\___  \\   | |   |  __|  | |     | |     | | | |   / / |__/ | | \n" +
                "| |___   / /  | |  ___| |   | |   | |___  | |___  | |___  | |_| |  / /       | | \n" +
                "\\_____| /_/   |_| /_____/   |_|   |_____| |_____| |_____| \\_____/ /_/        |_| \n\n");

        Logger.initialize();

        CastellumServer CastellumServer = ConfigurationBinder.inject(new CastellumServer());

        CastellumServer.start();
    }

}
