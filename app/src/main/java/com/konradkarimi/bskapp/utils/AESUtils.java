package com.konradkarimi.bskapp.utils;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.konradkarimi.bskapp.services.FStoreCallback;
import com.konradkarimi.bskapp.services.FirestoreService;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;
import static javax.crypto.Cipher.getInstance;

public class AESUtils {

    private static String AESUtils_TAG = "AES";

    private FirestoreService fsService;
    public SecretKey oriKey;
    public SecretKey restoredKey;
    public byte[] ini;

    public AESUtils() {
        fsService = new FirestoreService();
    }

    private SecretKey generateKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[16];
        secureRandom.nextBytes(key);
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        fsService.saveKey(convertKeyToString(secretKey));
        oriKey = secretKey;
        return secretKey;
    }

    private byte[] generateInitVector() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] initVector = new byte[12];
        secureRandom.nextBytes(initVector);
        ini = initVector;
        return initVector;
    }

    public byte[] encryptText(String textToEncrypt) {

        SecretKey secretKey = generateKey();
        byte[] initVector = generateInitVector();

        final Cipher cipher;
        byte[] cipherMessage = new byte[0];
        try {
            cipher = getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, initVector);
            cipher.init(ENCRYPT_MODE, secretKey, parameterSpec);
            byte[] cipherText = cipher.doFinal(textToEncrypt.getBytes());

            ByteBuffer byteBuffer = ByteBuffer.allocate(initVector.length + cipherText.length);
            byteBuffer.put(initVector);
            byteBuffer.put(cipherText);
            cipherMessage = byteBuffer.array();

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return cipherMessage;
    }

    public byte[] decryptText(final String textToDecrypt) {
        final byte[][] plainText = {new byte[0]};
        fsService.getKey(new FStoreCallback() {
            @Override
            public void onValue(String string) {
                SecretKey secretKey = convertStringToKey(string);
                restoredKey = secretKey;
                try {
                    final Cipher cipher = getInstance("AES/GCM/NoPadding");
                    AlgorithmParameterSpec gcmInitVector = new GCMParameterSpec(128, textToDecrypt.getBytes(), 0, 12);
                    cipher.init(DECRYPT_MODE, secretKey, gcmInitVector);
                    plainText[0] = cipher.doFinal(textToDecrypt.getBytes(), 12, textToDecrypt.length() - 12);

                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            }
        });
        return plainText[0];
    }

    private String convertKeyToString(SecretKey key) throws IllegalArgumentException {
        if (!key.getAlgorithm().equalsIgnoreCase("AES")) {
            throw new IllegalArgumentException("Not an AES key");
        }
        byte[] keyData = key.getEncoded();
        String stringKey = Base64.encodeToString(keyData, Base64.DEFAULT);
        Log.i(AESUtils_TAG, "Convert " + Arrays.toString(key.getEncoded()));
        Log.i(AESUtils_TAG, "Convert " + stringKey);
        return stringKey;
    }

    private SecretKey convertStringToKey(String key) throws IllegalStateException {
        byte[] keyData = Base64.decode(key, Base64.DEFAULT);
        SecretKey aesKey = new SecretKeySpec(keyData, 0, keyData.length, "AES");
        Log.i(AESUtils_TAG, "Revert " + key);
        Log.i(AESUtils_TAG, "Revert " + Arrays.toString(aesKey.getEncoded()));
        return aesKey;
    }

}
