import database.DatabaseRepository;
import org.castellum.exception.CastellumException;
import org.castellum.network.CastellumClient;
import org.castellum.security.EncryptionUtil;
import org.castellum.source.CastellumDataSource;

import java.io.File;

public class Main {

    private static String database = "sniffy";

    public static void main(String[] args) throws Exception {
        CastellumDataSource dataSource = CastellumDataSource.build()
                .setHost("127.0.0.1")
                .setPort(7800)
                .setUsername("root")
                .setPassword("password_root_test_$aùm$âzepf")
                .setPublicKey(EncryptionUtil.loadPublicKey(new File("public.key")));

        CastellumClient client = CastellumClient.login(dataSource);

        if (client == null) {
            throw new CastellumException("Invalid credential");
        }


        if (!client.selectDatabase(DatabaseRepository.SNIFFY)) { // OR : if (!client.selectDatabase("sniffy"))
            throw new CastellumException(String.format("Cannot select database %s", database));
        }

        System.out.printf("Database %s successfully selected \n", database);

        client.getDatabases().forEach(System.out::println);
    }

}
