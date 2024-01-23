package com.example.myapplication;

import static com.example.myapplication.CheckCrt.decryptWithPrivateKey;

import android.content.SharedPreferences;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import com.usdk.apiservice.aidl.device.DeviceInfo;
import com.usdk.apiservice.aidl.device.UDeviceManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

public class IsoMessage {
    private static final String TAG = IsoMessage.class.getSimpleName();
    private SharedPreferences sharedPreferences;

    public IsoMessage(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public boolean sendPublicKey(String publicKey, String modulus, String exponent) {
        try {
            // Construct the data to be sent
            String serialNo = getSerialNumber();
            String data = String.format("%s|%s|%s|%s", serialNo, publicKey, modulus, exponent);

            // Establish a socket connection
            try (Socket socket = new Socket("3.6.98.232", 17881);
                 OutputStream outputStream = socket.getOutputStream();
                 InputStream inputStream = socket.getInputStream()) {

                // Send data to the server
                outputStream.write(data.getBytes());
                outputStream.flush();

                // Receive the response from the server
                byte[] buffer = new byte[1024]; // Adjust the size according to your expected response size
                int bytesRead = inputStream.read(buffer);

                if (bytesRead > 0) {
                    // Process the received encrypted response
                    String responseString = new String(buffer, 0, bytesRead);
                    Log.d(TAG, "Received encrypted response: " + responseString);

                    // Decrypt the response using the private key from SharedPreferences
                    String privateKeyString = getPrivateKeyFromPreferences();
                    String decryptedResponse = decryptWithPrivateKey(responseString, privateKeyString);

                    // Log the decrypted response
                    Log.d(TAG, "Decrypted response: " + decryptedResponse);

                    // Check the decryptedResponse for success or failure
                    return processDecryptedResponse(decryptedResponse);
                } else {
                    Log.d(TAG, "Failed to receive response");
                    return false; // Return false on receive failure
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error: " + e.getMessage());
            return false; // Return false on exception
        }
    }

    private String getPrivateKeyFromPreferences() {
        // Retrieve the private key from SharedPreferences
        return sharedPreferences.getString("privateKey", "");
    }

    private boolean processDecryptedResponse(String decryptedResponse) {
        // Your logic for processing the decrypted response goes here
        // For example, check if the decryptedResponse equals "Success" for success
        if ("Success".equals(decryptedResponse)) {
            // Additional processing or logging for success
            return true;
        } else {
            // Additional processing or logging for failure
            return false;
        }
    }

    public static String getSerialNumber() throws RemoteException {
        UDeviceManager deviceManager = DeviceHelper.me().getDeviceManager();
        DeviceInfo deviceInfo = deviceManager.getDeviceInfo();
        return deviceInfo.getSerialNo();
    }

}

