package com.example.user.imageeditor;

import android.app.Application;

import com.adobe.creativesdk.aviary.IAviaryClientCredentials;
import com.adobe.creativesdk.foundation.AdobeCSDKFoundation;
import com.adobe.creativesdk.foundation.auth.IAdobeAuthClientCredentials;

public class MainApplication extends Application implements IAviaryClientCredentials {
    private static final String CREATIVE_SDK_CLIENT_ID = "dec7b440ebd842acbb78bec6e0a15f80";
    private static final String CREATIVE_SDK_CLIENT_SECRET = "d919930e-668e-4d07-b869-818347e585bf";

    @Override
    public void onCreate() {
        super.onCreate();
        AdobeCSDKFoundation.initializeCSDKFoundation(getApplicationContext());
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_CLIENT_SECRET;
    }

    @Override
    public String getBillingKey() {
        return "";
    }
}
