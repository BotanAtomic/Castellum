package org.castellum.security;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class EncryptionUtil {

    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.genKeyPair();
    }

    public static byte[] encrypt(byte[] decrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(decrypted);
    }

    public static byte[] decrypt(byte[] encrypted) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public static void saveKey(Key key, String name) throws Exception {
        FileOutputStream out = new FileOutputStream(name + ".key");
        out.write(key.getEncoded());
        out.close();
    }

    public static void loadPrivateKey(File file) throws Exception {
        byte[] bytes = Files.readAllBytes(file.toPath());
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        privateKey = keyFactory.generatePrivate(ks);
    }

    public static void loadPublicKey(File file) throws Exception {
        byte[] bytes = Files.readAllBytes(file.toPath());
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        publicKey = keyFactory.generatePublic(ks);
    }


}
