package com.example.triptix.Util;


import com.example.triptix.Config.ReadFileProperties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AesEncryptionUtil {
    private ReadFileProperties readFileProperties;
    private static String SECRET_KEY ;
    private static String INIT_VECTOR ;

    public AesEncryptionUtil() {
        readFileProperties = new ReadFileProperties();
//        this.SECRET_KEY = readFileProperties.readFile().getProperty("SECRET_KEY_AesEncryption");
        this.SECRET_KEY = "khadepzaicutehii";
//        this.INIT_VECTOR = readFileProperties.readFile().getProperty("INIT_VECTOR_AesEncryption");
        this.INIT_VECTOR = "khadepzaicutehii";
    }

    public static String encrypt(String plainText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(INIT_VECTOR.getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");     //hoạt động AES/CBC/PKCS5PADDING. chế độ CBC yêu cầu cung cấp một vector khởi tạo (IV - Initialization Vector) để mã hóa và giải mã dữ liệu.
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(INIT_VECTOR.getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

//    public static void main(String[] args) {
//        try {
//            String plainText = "1000000";
//            System.out.println("before encrypt: "+plainText);
//
//            String encryptedText = encrypt(plainText);
//            System.out.println("Encrypted text: " + encryptedText);
//
//            String decryptedText = decrypt(encryptedText);
//            System.out.println("Decrypted text: " + decryptedText);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}