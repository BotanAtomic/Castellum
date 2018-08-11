import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;

public class Main {

    private Socket socket;

    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    private short encryptionSize;

    public static void main(String[] args) throws Exception {
        EncryptionUtil.loadPublicKey(new File("public.key"));

        new Main(new Socket("127.0.0.1", 7800));
    }

    private Main(Socket socket) throws Exception {
        this.socket = socket;
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());

        encryptionSize = (short) EncryptionUtil.encrypt("test".getBytes()).length;

        login("root", "AZLKDjneofkl,nsmlKOPAJEZFOPnzaefkls,fczpoFJzaopif,nzpoaf98azf498aef78z641fdazd4896az4d56az1f56afz");

        selectDatabase("sniffy");

        String database = "test";
        createDatabase(database);
        selectDatabase(database);

        createTable("table_test");
        createField("field_test", "table_test", Fields.STRING);

        removeDatabase(database);
    }

    static int i = 0;

    void login(String username, String password) throws Exception {
        outputStream.writeByte(0);
        outputStream.writeShort(encryptionSize);
        outputStream.write(EncryptionUtil.encrypt(username.getBytes()));
        outputStream.write(EncryptionUtil.encrypt(password.getBytes()));

        if (inputStream.readBoolean()) {
            System.out.println("Log success " + (++i));
        } else {
            System.out.println("Log failed");
        }
    }

    void createDatabase(String database) throws Exception {
        outputStream.writeByte(1);
        outputStream.writeUTF(database);

        if (inputStream.readBoolean()) {
            System.out.println("Database creation success");
        } else {
            System.out.println("Database creation failed");
        }
    }

    void selectDatabase(String database) throws Exception {
        outputStream.writeByte(4);
        outputStream.writeUTF(database);

        if (inputStream.readBoolean()) {
            System.out.println("Database selection success");
        } else {
            System.out.println("Cannot select database");
        }
    }

    void createTable(String table, String database) throws Exception {
        outputStream.writeByte(2);
        outputStream.writeBoolean(true);
        outputStream.writeUTF(database);
        outputStream.writeUTF(table);

        if (inputStream.readBoolean()) {
            System.out.println("Table creation success");
        } else {
            System.out.println("Table creation failed");
        }
    }

    void createTable(String table) throws Exception {
        outputStream.writeByte(2);
        outputStream.writeBoolean(false);
        outputStream.writeUTF(table);

        if (inputStream.readBoolean()) {
            System.out.println("Table [auto] creation success");
        } else {
            System.out.println("Table [auto] creation failed");
        }
    }

    void createField(String field, String table, Fields fieldType) throws Exception {
        outputStream.writeByte(3);
        outputStream.writeBoolean(false);
        outputStream.writeUTF(table);
        outputStream.writeUTF(field);
        outputStream.writeByte(fieldType.ordinal());

        if (inputStream.readBoolean()) {
            System.out.println("Field creation success");
        } else {
            System.out.println("Field creation failed");
        }
    }

    void createField(String field, String database, String table, Fields fieldType) throws Exception {
        outputStream.writeByte(3);
        outputStream.writeBoolean(true);
        outputStream.writeUTF(database);
        outputStream.writeUTF(table);
        outputStream.writeUTF(field);
        outputStream.writeByte(fieldType.ordinal());

        if (inputStream.readBoolean()) {
            System.out.println("Field creation success");
        } else {
            System.out.println("Field creation failed");
        }
    }

    void removeField(String field, String table) throws Exception {
        outputStream.writeByte(7);
        outputStream.writeBoolean(false);
        outputStream.writeUTF(table);
        outputStream.writeUTF(field);

        if (inputStream.readBoolean()) {
            System.out.println("Field delete success");
        } else {
            System.out.println("Field delete failed");
        }
    }

    void removeDatabase(String database) throws Exception {
        outputStream.writeByte(5);
        outputStream.writeUTF(database);

        if (inputStream.readBoolean()) {
            System.out.println("Field delete success");
        } else {
            System.out.println("Field delete failed");
        }
    }


}
