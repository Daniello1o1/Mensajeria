package com.upiiz.examen_dja_03;

import android.content.Context;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.InputStream;
import java.util.Collections;

public class FcmAuth {
    public static String getAccessToken(Context context) throws Exception {
        InputStream is = context.getResources().openRawResource(R.raw.service_account);

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(is)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));

        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }
}
