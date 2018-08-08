package org.castellum.core;

import org.castellum.configuration.ConfigurationBinder;
import org.castellum.logger.Logger;
import org.castellum.network.CastellumServer;
import org.castellum.security.EncryptionUtil;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        EncryptionUtil.loadPrivateKey(new File("private.key"));
        Logger.initialize();

        CastellumServer CastellumServer = ConfigurationBinder.inject(new CastellumServer());

        CastellumServer.start();
    }

}
