package com.konradkarimi.bskapp.utils;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import com.konradkarimi.bskapp.services.FirestoreService;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAUtils {

    private static String RSAUtils_TAG = "RSA";
    private static String ANDROID_KEY_STORE = "AndroidKeyStore";

    private FirestoreService fsService;
    private String keyAlias;

    public RSAUtils() {
        fsService = new FirestoreService();
        keyAlias = "RSAKeyPair";
    }


    public KeyPair generateKeyPair() {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
            KeyGenParameterSpec.Builder keyGenParameterSpec = new KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1);
            keyPairGenerator.initialize(keyGenParameterSpec.build());
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return keyPairGenerator.generateKeyPair();
    }


    public String exportPublicKey() {
        byte[] publicKey = new byte[0];
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
            publicKey = keyStore.getCertificate(keyAlias).getPublicKey().getEncoded();

        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String exportable = Base64.encodeToString(publicKey, Base64.NO_WRAP);
        return String.valueOf(exportable);
    }

    public PublicKey importPublicKey(String publicKey) {
        KeyStore keyStore;
        PublicKey pubKey = null;
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);

            byte[] publicKeyBytes = Base64.decode(publicKey, Base64.NO_WRAP);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            pubKey = keyFactory.generatePublic(keySpec);

        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return pubKey;
    }


    private KeyStore createKeyStore() {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyStore;
    }

    public KeyPair getKeyPair() {
        try {
            KeyStore keyStore = createKeyStore();

            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, null);
            PublicKey publicKey = keyStore.getCertificate(keyAlias).getPublicKey();

            if (privateKey != null && publicKey != null) {
                return new KeyPair(publicKey, privateKey);
            } else {
                generateKeyPair();
                getKeyPair();
            }

        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeKeyStoreEntries() {
        try {
            KeyStore keystore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keystore.load(null);
            keystore.deleteEntry(keyAlias);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void generateKeyIfnotExist() {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                if (!keyAlias.equals(aliases.nextElement())) {
                    generateKeyPair();
                }
            }

        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String encryptData(String data, Key publicKey) {
        byte[] bytes = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            bytes = cipher.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public String decryptData(String data, Key privateKey) {
        byte[] decodedData = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] encryptedData = Base64.decode(data, Base64.DEFAULT);
            decodedData = cipher.doFinal(encryptedData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return new String(decodedData);
    }

}
