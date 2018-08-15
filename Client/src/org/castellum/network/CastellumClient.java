package org.castellum.network;

import javafx.util.Pair;
import org.castellum.entity.CastellumDatabase;
import org.castellum.entity.CastellumField;
import org.castellum.entity.CastellumTable;
import org.castellum.field.Field;
import org.castellum.protocol.NetworkProtocol;
import org.castellum.security.EncryptionUtil;
import org.castellum.source.CastellumDataSource;
import org.json.JSONArray;

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

    public static CastellumClient login(CastellumDataSource dataSource) throws Exception {
        CastellumClient castellumClient = new CastellumClient();

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

    }

    public boolean selectDatabase(CastellumDatabase database) throws IOException {
        return selectDatabase(database.getName());
    }

    public boolean selectDatabase(String database) throws IOException {
        outputStream.writeByte(NetworkProtocol.SELECT_DATABASE);
        outputStream.writeUTF(database);

        return inputStream.readBoolean();
    }

    public boolean removeDatabase(String database) throws IOException {
        outputStream.writeByte(NetworkProtocol.REMOVE_DATABASE);
        outputStream.writeUTF(database);

        return inputStream.readBoolean();
    }

    public List<String> getDatabases() throws IOException {
        List<String> databases = new ArrayList<>();

        outputStream.writeByte(NetworkProtocol.GET_DATABASE);

        short size = inputStream.readShort();

        while (size > 0) {
            databases.add(inputStream.readUTF());
            size--;
        }

        return databases;
    }

    /**
     * @param database
     * @return list of table with their fields in #JSONArray
     */

    public List<Pair<String, JSONArray>> getTables(CastellumDatabase database) throws IOException {
        return getTables(database.getName());
    }

    public List<Pair<String, JSONArray>> getTables(String database) throws IOException {
        List<Pair<String, JSONArray>> tables = new ArrayList<>();

        outputStream.writeByte(NetworkProtocol.GET_TABLE);

        outputStream.writeUTF(database);

        short size = inputStream.readShort();

        while (size > 0) {
            tables.add(new Pair<>(inputStream.readUTF(), new JSONArray(inputStream.readUTF())));
            size--;
        }

        return tables;
    }


    /**
     * @param database
     * @return
     */
    public boolean createDatabase(String database) throws IOException {
        outputStream.writeByte(NetworkProtocol.CREATE_DATABASE);

        outputStream.writeUTF(database);

        return inputStream.readBoolean();
    }

    /**
     * @param table    name of table to create
     * @param database if database is empty, the server get the selected database
     * @return if the table is successfully created
     */

    public boolean createTable(String table, String database) throws IOException {
        outputStream.writeByte(NetworkProtocol.CREATE_TABLE);

        outputStream.writeBoolean(!database.isEmpty());

        if (!database.isEmpty())
            outputStream.writeUTF(database);

        outputStream.writeUTF(table);

        return inputStream.readBoolean();
    }


    public boolean createTable(String table, CastellumDatabase database) throws IOException {
        return createTable(table, database.getName());
    }

    public boolean createTable(String table) throws IOException {
        return createTable(table, "");
    }

    /**
     * @param table    name of table to remove
     * @param database if database is empty, the server get the selected database
     * @return if the table is successfully removed
     */

    public boolean removeTable(String table, String database) throws IOException {
        outputStream.writeByte(NetworkProtocol.REMOVE_TABLE);

        outputStream.writeBoolean(!database.isEmpty());

        if (!database.isEmpty())
            outputStream.writeUTF(database);

        outputStream.writeUTF(table);

        return inputStream.readBoolean();
    }


    public boolean removeTable(String table, CastellumDatabase database) throws IOException {
        return removeTable(table, database.getName());
    }

    public boolean removeTable(String table) throws IOException {
        return removeTable(table, "");
    }


    public boolean createField(String database, String table, String field, Field fieldType) throws IOException {
        outputStream.writeByte(NetworkProtocol.CREATE_FIELD);

        outputStream.writeBoolean(!database.isEmpty());

        if (!database.isEmpty())
            outputStream.writeUTF(database);

        outputStream.writeUTF(table);
        outputStream.writeUTF(field);
        outputStream.writeByte(fieldType.ordinal());

        return inputStream.readBoolean();
    }

    public boolean createField(CastellumDatabase database, CastellumTable table, String field, Field fieldType) throws IOException {
        return createField(database.getName(), table.getName(), field, fieldType);
    }

    public boolean createField(CastellumDatabase database, String table, String field, Field fieldType) throws IOException {
        return createField(database.getName(), table, field, fieldType);
    }

    public boolean createField(CastellumTable table, String field, Field fieldType) throws IOException {
        return createField("", table.getName(), field, fieldType);
    }

    public boolean createField(String table, String field, Field fieldType) throws IOException {
        return createField("", table, field, fieldType);
    }

    public boolean removeField(String database, String table, String field) throws IOException {
        outputStream.writeByte(NetworkProtocol.REMOVE_FIELD);

        outputStream.writeBoolean(!database.isEmpty());

        if (!database.isEmpty())
            outputStream.writeUTF(database);

        outputStream.writeUTF(table);
        outputStream.writeUTF(field);

        return inputStream.readBoolean();
    }

    public boolean removeField(CastellumDatabase database, CastellumTable table, String field) throws IOException {
        return removeField(database.getName(), table.getName(), field);
    }

    public boolean removeField(CastellumDatabase database, String table, String field) throws IOException {
        return removeField(database.getName(), table, field);
    }

    public boolean removeField(CastellumTable table, String field) throws IOException {
        return removeField("", table.getName(), field);
    }

    public boolean removeField(String table, String field) throws IOException {
        return removeField("", table, field);
    }

    public boolean removeField(CastellumDatabase database, CastellumTable table, CastellumField field) throws IOException {
        return removeField(database.getName(), table.getName(), field.getName());
    }

    public boolean removeField(CastellumDatabase database, String table, CastellumField field) throws IOException {
        return removeField(database.getName(), table, field.getName());
    }

    public boolean removeField(CastellumTable table, CastellumField field) throws IOException {
        return removeField("", table.getName(), field.getName());
    }

    public boolean removeField(String table, CastellumField field) throws IOException {
        return removeField("", table, field.getName());
    }

    public boolean insertValue(String database, String table) {
        return false; //TODO
    }


    public void close() throws IOException {
        socket.close();
    }
}
