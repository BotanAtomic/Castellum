package org.castellum.network.handler;

import org.castellum.logger.Logger;
import org.castellum.network.CastellumSession;
import org.castellum.security.EncryptionUtil;

import java.io.IOException;

public class LoginHandler implements NetworkHandler {

    @Override
    public void handle(CastellumSession session) {
        try {
            short size = session.getInputStream().readShort();

            byte[] login = new byte[size];
            byte[] password = new byte[size];
            session.getInputStream().readFully(login);
            session.getInputStream().readFully(password);

            session.getRoot().login(EncryptionUtil.decrypt(login), EncryptionUtil.decrypt(password), session);

            Logger.println("Session $ login : $", session.getSessionId(), session.isConnected() ? "success" : "failed");
        } catch (IOException e) {
            e.printStackTrace();
        }

        session.writeReturnResponse(session.isConnected());
    }

}
