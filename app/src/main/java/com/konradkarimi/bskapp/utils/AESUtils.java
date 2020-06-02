package com.konradkarimi.bskapp.utils;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import com.konradkarimi.bskapp.services.FirestoreService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class AESUtils {

    private static String AESUtils_TAG = "AES";

    private FirestoreService fsService;
    public SecretKey oriKey;
    public SecretKey restoredKey;
    public byte[] iIv;

    public AESUtils() {
        fsService = new FirestoreService();
    }

    public SecretKey generateKey() {
        SecretKey key = null;
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec keygenParameterSpec = new KeyGenParameterSpec.Builder("AESsymKey", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build();
            keygen.init(keygenParameterSpec);
            key = keygen.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return key;
    }

    public SecretKey getSymmetricKey() {
        KeyStore keyStore = createKeystore();
        if (!isKeyExist(keyStore)) {
            generateKey();
        }
        SecretKey key = null;
        try {
            key = (SecretKey) keyStore.getKey("AESsymKey", null);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return key;
    }

    public Boolean isKeyExist(KeyStore keyStore) {
        try {
            Enumeration<String> alieses = keyStore.aliases();
            while (alieses.hasMoreElements()) {
                return ("AESsymKey".equals(alieses.nextElement()));
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    public KeyStore createKeystore() {
        KeyStore keystore = null;
        try {
            keystore = KeyStore.getInstance("AndroidKeyStore");
            keystore.load(null);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keystore;
    }

    public void removeKeyFromKeystore() {
        KeyStore keyStore = createKeystore();
        if (isKeyExist(keyStore)) {
            try {
                keyStore.deleteEntry("AESsymKey");
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }
    }


    public HashMap<String, byte[]> encryptText(String textToEncrypt) {
        removeKeyFromKeystore();

        byte[] cipherText = new byte[0];
        byte[] plainText = textToEncrypt.getBytes();
        byte[] iv = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(ENCRYPT_MODE, getSymmetricKey());
            iv = cipher.getIV();
            cipherText = cipher.doFinal(plainText);
            fsService.saveIV(Arrays.toString(iv));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        HashMap<String, byte[]> encrypted = new HashMap<>();
        encrypted.put("IV", iv);
        encrypted.put("CipherText", cipherText);
        return encrypted;
    }

    public String decrypt(byte[] ivBytes, byte[] encryptedText) {
        String decryptedData = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(DECRYPT_MODE, getSymmetricKey(), new GCMParameterSpec(128, ivBytes));
            byte[] decrypted = cipher.doFinal(encryptedText);
            decryptedData = new String(decrypted, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedData;
    }

    private String convertKeyToString(SecretKey key) throws IllegalArgumentException {
        if (!key.getAlgorithm().equalsIgnoreCase("AES")) {
            throw new IllegalArgumentException("Not an AES key");
        }
        byte[] keyData = key.getEncoded();
        String stringKey = Base64.encodeToString(keyData, Base64.NO_WRAP);
        Log.i(AESUtils_TAG, "Convert " + Arrays.toString(key.getEncoded()));
        Log.i(AESUtils_TAG, "Convert " + stringKey);
        return stringKey;
    }

    private SecretKey convertStringToKey(String key) throws IllegalStateException {
        byte[] keyData = Base64.decode(key, Base64.NO_WRAP);
        SecretKey aesKey = new SecretKeySpec(keyData, 0, keyData.length, "AES");
        Log.i(AESUtils_TAG, "Revert " + key);
        Log.i(AESUtils_TAG, "Revert " + Arrays.toString(aesKey.getEncoded()));
        return aesKey;
    }

}
