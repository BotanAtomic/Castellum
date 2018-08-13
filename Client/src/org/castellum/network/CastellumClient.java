package org.castellum.network;

import org.castellum.entity.CastellumDatabase;
import org.castellum.protocol.NetworkProtocol;
import org.castellum.security.EncryptionUtil;
import org.castellum.source.CastellumDataSource;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class CastellumClient {

    private Socket socket;

    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    private CastellumClient() {
        //block non-permitted constructor
    }

    public static CastellumClient login(CastellumDataSource dataSource) {
        CastellumClient castellumClient = new CastellumClient();

        try {
            Socket socket = new Socket(dataSource.getHost(), dataSource.getPort());

            PublicKey publicKey = dataSource.getPublicKey();

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            byte[] username = EncryptionUtil.encrypt(dataSource.getUsername().getBytes(), publicKey);
            byte[] password = EncryptionUtil.encrypt(dataSource.getPassword().getBytes(), publicKey);

            outputStream.writeByte(NetworkProtocol.LOGIN);
            outputStream.writeShort(username.length);
            outputStream.write(username);
            outputStream.write(password);

            if (inputStream.readBoolean()) {
                castellumClient.inputStream = inputStream;
                castellumClient.outputStream = outputStream;

                return castellumClient;
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public boolean selectDatabase(CastellumDatabase database) {
        return selectDatabase(database.getName());
    }

    public boolean selectDatabase(String database) {
        try {
            outputStream.writeByte(NetworkProtocol.SELECT_DATABASE);
            outputStream.writeUTF(database);

            return inputStream.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getDatabases() {
        List<String> databases = new ArrayList<>();

        try {
            outputStream.writeByte(NetworkProtocol.GET_DATABASE);

            short size = inputStream.readShort();

            while (size > 0) {
                databases.add(inputStream.readUTF());
                size--;
            }

            return databases;
        } catch (IOException e) {
            e.printStackTrace();
            return databases;
        }

    }


}
