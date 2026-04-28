package com.assignments.mtnpen.android;

import android.os.Bundle;
import android.provider.Settings;

import com.assignments.mtnpen.network.DeviceIdProvider;
import com.assignments.mtnpen.R;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.assignments.mtnpen.MountainPenguins;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.
        initialize(new MountainPenguins(new DeviceIdProvider() {
            @Override
            public String getDeviceId() {
                return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        }, getString(R.string.api_base_url)), configuration);
    }
}
