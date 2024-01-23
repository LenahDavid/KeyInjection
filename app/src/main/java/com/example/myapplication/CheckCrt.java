package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class CheckCrt {

    private static final String TAG = CheckCrt.class.getSimpleName();

    public static void main(String[] args, Context context) {
        try {
            // Get public and private keys from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences(context);
            String publicKeyString = getFromSharedPreferences(sharedPreferences, "publicKey", "");
            String privateKeyString = getFromSharedPreferences(sharedPreferences, "privateKey", "");

            // Original message
            String originalMessage = "Hello, this is a test message.";

            // Encrypt the message with the public key
            String encryptedMessage = encryptWithPublicKey(originalMessage, publicKeyString);

            // Decrypt the message with the private key
            String decryptedMessage = decryptWithPrivateKey(encryptedMessage, privateKeyString);

            // Print results
            Log.d(TAG, "Original Message: " + originalMessage);
            Log.d(TAG, "Encrypted Message: " + encryptedMessage);
            Log.d(TAG, "Decrypted Message: " + decryptedMessage);

            // Verify that the decrypted message matches the original message
            if (originalMessage.equals(decryptedMessage)) {
                Log.d(TAG, "Decryption successful. The private key can decrypt messages encrypted with the public key.");
            } else {
                Log.d(TAG, "Decryption failed. The private key cannot decrypt messages correctly.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("MyKeys", Context.MODE_PRIVATE);
    }

    public static String getFromSharedPreferences(SharedPreferences sharedPreferences, String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    private static String encryptWithPublicKey(String message, String publicKeyString) throws Exception {
        PublicKey publicKey = getPublicKeyFromString(publicKeyString);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public static String decryptWithPrivateKey(String encryptedMessage, String privateKeyString) throws Exception {
        PrivateKey privateKey = getPrivateKeyFromString(privateKeyString);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] encryptedBytes = Base64.decode(encryptedMessage, Base64.DEFAULT);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static PublicKey getPublicKeyFromString(String publicKeyString) throws Exception {
        byte[] keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private static PrivateKey getPrivateKeyFromString(String privateKeyString) throws Exception {
        byte[] keyBytes = Base64.decode(privateKeyString, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}
