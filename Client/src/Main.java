import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws Exception {
        EncryptionUtil.loadPublicKey(new File("public.key"));

        Socket socket = new Socket("127.0.0.1", 7800);

        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());


        byte[] login = EncryptionUtil.encrypt("root".getBytes());
        byte[] password = EncryptionUtil.encrypt("AZLKDjneofkl,nsmlKOPAJEZFOPnzaefkls,fczpoFJzaopif,nzpoaf98azf498aef78z641fdazd4896az4d56az1f56afz".getBytes());

        outputStream.writeByte(0);
        outputStream.writeShort(login.length);
        outputStream.write(login);
        outputStream.write(password);

        if(inputStream.readBoolean()) {
            System.out.println("Log success");
        } else {
            System.out.println("Log failed");
        }

        String database = "sniffy";

        outputStream.writeByte(1);
        outputStream.writeUTF(database);

        if(inputStream.readBoolean()) {
            System.out.println("Database creation success");
        } else {
            System.out.println("Database creation failed");
        }

        String table = "screensaver";

        outputStream.writeByte(2);
        outputStream.writeUTF(database);
        outputStream.writeUTF(table);

        if(inputStream.readBoolean()) {
            System.out.println("Table creation success");
        } else {
            System.out.println("Table creation failed");
        }
    }

}
