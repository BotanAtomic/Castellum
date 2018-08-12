import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.math.BigInteger;
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

        for (int i = 0; i < 0; i++)
            addValue("screensaver",
                    new Value("id", Fields.INTEGER, (short)i),
                    new Value("path", Fields.STRING, "http://92.222.64.224/"),
                    new Value("short_value", Fields.SHORT, 887),
                    new Value("valid", Fields.BOOLEAN, false),
                    new Value("price", Fields.DOUBLE, 5.666687));

        removeValue("screensaver");

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

    void addValue(String table, Value... values) throws Exception {
        long time = System.currentTimeMillis();

        outputStream.writeByte(8);
        outputStream.writeBoolean(false);
        outputStream.writeUTF(table);
        outputStream.writeByte(values.length);

        for (Value value : values) {
            outputStream.writeUTF(value.name);
            switch (value.fieldType) {
                case BOOLEAN:
                    outputStream.writeBoolean((Boolean) value.value);
                    break;
                case BYTE:
                    outputStream.writeByte((Byte) value.value);
                    break;
                case CHAR:
                    outputStream.writeChar((Character) value.value);
                    break;
                case SHORT:
                    outputStream.writeShort(new BigInteger(String.valueOf(value.value)).shortValue());
                    break;
                case INTEGER:
                    outputStream.writeInt(new BigInteger(String.valueOf(value.value)).intValue());
                    break;
                case LONG:
                    outputStream.writeLong(new BigInteger(String.valueOf(value.value)).longValue());
                    break;
                case FLOAT:
                    outputStream.writeFloat((float) value.value);
                    break;
                case DOUBLE:
                    outputStream.writeDouble((double) value.value);
                    break;
                case STRING:
                    outputStream.writeUTF((String) value.value);
                    break;

            }
        }


        if (inputStream.readBoolean()) {
            long diff = System.currentTimeMillis() - time;
            System.out.println("Value add success / " + diff);
        } else {
            System.out.println("Value add failed");
        }

    }


    void removeValue(String table) throws Exception {
        long time = System.currentTimeMillis();

        outputStream.writeByte(9);
        outputStream.writeBoolean(false);
        outputStream.writeUTF(table);

        outputStream.writeByte(0);

       /** outputStream.writeUTF("id");
        outputStream.writeUTF("path");
        outputStream.writeUTF("short_value");

        outputStream.writeUTF("(id > 0 && path == \'http://92.222.64.224/\') || (short_value < 888)");**/

        if (inputStream.readBoolean()) {
            System.out.println("Field delete success");
        } else {
            System.out.println("Field delete failed");
        }

        long diff = System.currentTimeMillis() - time;

        System.out.println("Remove time value " + diff);
    }

    class Value {

        String name;
        Fields fieldType;
        Object value;

        Value(String name, Fields fieldType, Object value) {
            this.name = name;
            this.fieldType = fieldType;
            this.value = value;
        }
    }

    class Condition {


    }


}
