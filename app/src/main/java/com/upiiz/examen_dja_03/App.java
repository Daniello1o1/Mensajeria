package com.upiiz.examen_dja_03;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {
    private int activitiesRunning = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStarted(Activity activity) {
                activitiesRunning++;

                if (activitiesRunning == 1) {
                    setOnline();
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
                activitiesRunning--;

                if (activitiesRunning == 0) {
                    setOffline();
                }
            }

            // No necesitas implementar los otros métodos
            public void onActivityCreated(Activity a, Bundle b) {}
            public void onActivityResumed(Activity a) {}
            public void onActivityPaused(Activity a) {}
            public void onActivitySaveInstanceState(Activity a, Bundle b) {}
            public void onActivityDestroyed(Activity a) {}
        });
    }

    private void setOnline() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("usuarios")
                .child(uid);

        ref.child("estado").setValue("online");
        ref.child("estado").onDisconnect().setValue("offline");
    }

    private void setOffline() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference("usuarios")
                .child(uid);

        ref.child("estado").setValue("offline");
    }
}