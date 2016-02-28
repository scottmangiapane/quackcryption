package co.twoduck.quackcryption;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.*;

public class AES {
    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("ASCII"));
            SecretKeySpec sks = new SecretKeySpec(key.getBytes("ASCII"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, sks, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return new String(Base64.encodeBase64(encrypted));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("ASCII"));
            SecretKeySpec sks = new SecretKeySpec(key.getBytes("ASCII"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, sks, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted.getBytes()));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
