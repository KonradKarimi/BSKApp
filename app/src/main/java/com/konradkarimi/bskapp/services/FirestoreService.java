package com.konradkarimi.bskapp.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class FirestoreService {

    private FirebaseFirestore db;
    private String keyString;

    public FirestoreService() {
        db = FirebaseFirestore.getInstance();
    }

    public String getKeyString() {
        return keyString;
    }

    public void setKeyString(String keyString) {
        this.keyString = keyString;
    }

    public void saveKey(String key) {
        Map<String, Object> secretData = new HashMap<>();
        secretData.put("AESKey", key);
        db.document("Keys/AESKey").set(secretData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("BSK", "Saved AESKey ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("BSK", "Failed to save Key");
            }
        });
    }

    public void saveIV(String iv) {
        Map<String, Object> secretData = new HashMap<>();
        secretData.put("IV", iv);
        db.document("Keys/AESKey").set(secretData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("BSK", "Saved IV ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("BSK", "Failed to save IV");
            }
        });
    }

    public void saveSecrets(String key, String iv) {
        Map<String, Object> secretData = new HashMap<>();
        secretData.put("key", key);
        secretData.put("iv", iv);
        db.document("Keys/AESKey").set(secretData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("BSK", "Saved secretData ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("BSK", "Failed to save secretdata");
            }
        });
    }

    public void getKey(final FStoreCallback fStoreCallback) {
        db.document("Keys/AESKey").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            Map<String, Object> secretKey = new HashMap<>();

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.i("BSK", "Document data " + doc.getData());
                        secretKey = doc.getData();
                        fStoreCallback.onValue(secretKey.get("AESKey").toString());
                    } else {
                        Log.e("BSK", "no such document");
                    }
                } else {
                    Log.e("BSK", "Failed retrieving ", task.getException());
                }
            }
        });

    }

}

