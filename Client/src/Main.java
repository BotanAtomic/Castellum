import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws Exception {
        EncryptionUtil.loadPublicKey(new File("public.key"));

        Socket socket = new Socket("127.0.0.1", 7800);

        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());


        byte[] login = EncryptionUtil.encrypt("root".getBytes());
        byte[] password = EncryptionUtil.encrypt("password".getBytes());

        dataOutputStream.writeByte(0);
        dataOutputStream.writeInt(login.length);
        dataOutputStream.write(login);
        dataOutputStream.write(password);

    }

}
