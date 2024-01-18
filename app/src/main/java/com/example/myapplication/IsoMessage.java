
package com.example.myapplication;

import android.os.RemoteException;
import android.util.Log;
import org.jpos.core.Configuration;
import org.jpos.core.SimpleConfiguration;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOFieldPackager;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.util.LogListener;
import org.jpos.util.Logger;
import org.jpos.util.ProtectedLogListener;
import org.jpos.util.SimpleLogListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
//import java.util.Base64;
import android.util.Base64;
import com.example.myapplication.Packager;
import com.usdk.apiservice.aidl.device.DeviceInfo;
import com.usdk.apiservice.aidl.device.UDeviceManager;
import com.example.myapplication.DeviceHelper;

import org.json.JSONObject;



import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class IsoMessage {

    public boolean sendPublicKey(String publicKey, String modulus, String exponent) {
        try {
            String serialNo = getSerialNumber();
            String data = serialNo + "|" + publicKey + "|" + modulus + "|" + exponent;

            Socket socket = new Socket("3.6.122.107", 14937);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            outputStream.write(data.getBytes());
            outputStream.flush();

            byte[] buffer = new byte[1024]; // Adjust the size according to your expected response size
            int bytesRead = inputStream.read(buffer);

            if (bytesRead > 0) {
                String responseString = new String(buffer, 0, bytesRead);
                // Process the response string as needed
                // ...

                // For example, you can log the response
                System.out.println("Received response: " + responseString);

                // Check the responseString for success or failure based on your application logic
                return responseString.equals("00");
            } else {
                System.out.println("Failed to receive response");
                return false; // Return false on receive failure
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
            return false; // Return false on exception
        }
    }

    public static String getSerialNumber() throws RemoteException {
        UDeviceManager deviceManager = DeviceHelper.me().getDeviceManager();
        DeviceInfo deviceInfo = deviceManager.getDeviceInfo();

        return deviceInfo.getSerialNo();

    }

    public static void main(String[] args) {
        IsoMessage isoMessage = new IsoMessage();
        isoMessage.sendPublicKey("publicKey", "modulus", "exponent");
    }
}




