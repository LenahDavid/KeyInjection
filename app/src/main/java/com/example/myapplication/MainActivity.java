
package com.example.myapplication;



import static com.example.myapplication.R.id.ShowKeys;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private Context mContext;
    private String modulusString; // Declare modulusString variable
    private String exponentString; // Declare exponentString variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyKeys", Context.MODE_PRIVATE);

        Button generateButton = findViewById(R.id.Generate);
        Button showKeysButton = findViewById(R.id.ShowKeys);
        Button sendKeysButton = findViewById(R.id.SendKeys);

        generateButton.setOnClickListener(v -> {
            try {
                // Generate the RSA key pair
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048); // Adjust the key size as needed
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                PublicKey publicKey = keyPair.getPublic();
                PrivateKey privateKey = keyPair.getPrivate();

                // Get the RSA public key
                RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;

                // Get modulus and exponent
                BigInteger modulus = rsaPublicKey.getModulus();
                BigInteger exponent = rsaPublicKey.getPublicExponent();

                // Convert to strings
                modulusString = modulus.toString(16); // Convert to hexadecimal string
                exponentString = exponent.toString(16); // Convert to hexadecimal string

                // Save the modulus and exponent to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("modulus", modulusString);
                editor.putString("exponent", exponentString);
                editor.apply();

                // Convert keys to strings
                String publicKeyString = Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
                String privateKeyString = Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT);

                // Save the keys to SharedPreferences
                editor.putString("publicKey", publicKeyString);
                editor.putString("privateKey", privateKeyString);
                editor.apply();

                Toast.makeText(getApplicationContext(), "RSA Key Pair Generated and Saved", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "RSA Key Pair Generation Failed", Toast.LENGTH_SHORT).show();
            }
        });

        showKeysButton.setOnClickListener(v -> {
            // Retrieve the public key from SharedPreferences
            String publicKeyString = sharedPreferences.getString("publicKey", "");

            // Display the public key in an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("RSA Public Key");
            builder.setMessage(publicKeyString);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        sendKeysButton.setOnClickListener(v -> {

            // Retrieve the public key from SharedPreferences

            String publicKeyString = sharedPreferences.getString("publicKey", "");


            if (!publicKeyString.isEmpty()) {

                new Thread(() ->{
                    try {
                        // Call the IsoMessage class to send the public key
                        IsoMessage isoMessage = new IsoMessage();
                        boolean success = isoMessage.sendPublicKey(publicKeyString, modulusString, exponentString);

                        if (success) {
                            // Handle a successful transaction
                            this.runOnUiThread(()->{
                                Toast.makeText(this, "public key sent ", Toast.LENGTH_SHORT).show();
                            });
                        } else {

                            // Handle the case where it failed to reach the server
                            this.runOnUiThread(()->{
                                Toast.makeText(this, "Failed to reach the server", Toast.LENGTH_SHORT).show();
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        this.runOnUiThread(()->{
                            Toast.makeText(this, "Failed to send the public key", Toast.LENGTH_SHORT).show();
                        });

                    }
                }).start();

            } else {
                Toast.makeText(this, "Public Key not found in SharedPreferences", Toast.LENGTH_SHORT).show();
            }
        });


    }
}






