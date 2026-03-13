package com.assignments.mtnpen.android;

import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.assignments.mtnpen.MountainPenguins;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.
        initialize(new MountainPenguins(), configuration);

        FirebaseApp app = FirebaseApp.initializeApp(this);
        Log.d("FIREBASE_TEST", "FirebaseApp = " + app);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello Firestore");
        data.put("time", System.currentTimeMillis());

        db.collection("testCollection")
            .add(data)
            .addOnSuccessListener(documentReference ->
                Log.d("FIREBASE_TEST", "WRITE OK: " + documentReference.getId()))
            .addOnFailureListener(e ->
                Log.e("FIREBASE_TEST", "WRITE FAILED", e));

        initialize(new MountainPenguins(), new AndroidApplicationConfiguration());
    }
}
