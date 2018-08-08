package org.castellum.network.handler;

import org.castellum.network.CastellumSession;
import org.castellum.security.EncryptionUtil;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class LoginHandler implements NetworkHandler {

    @Override
    public void handle(CastellumSession session, AsynchronousSocketChannel channel) {

        session.nativeRead(ByteBuffer.allocate(4), (byteBuffer -> {
            int size = byteBuffer.getInt(0);

            try {
                session.nativeRead(ByteBuffer.allocate(size * 2), (data -> {
                    byte[] credentialsData = data.array();

                    byte[][] credentials = new byte[2][size];

                    System.arraycopy(credentialsData, 0, credentials[0], 0, size);
                    System.arraycopy(credentialsData, size, credentials[1], 0, size);

                    String login = new String(EncryptionUtil.decrypt(credentials[0]));
                    String password = new String(EncryptionUtil.decrypt(credentials[1]));

                    session.getRoot().login(login, password, session);

                }));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }));


    }

}
