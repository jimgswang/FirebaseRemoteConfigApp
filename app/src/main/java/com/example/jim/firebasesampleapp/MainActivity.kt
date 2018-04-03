package com.example.jim.firebasesampleapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var prop = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val settings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()
        remoteConfig.setConfigSettings(settings)
        remoteConfig.setDefaults(mapOf("prop1" to false))
        remoteConfig.setDefaults(mapOf("prop2" to false))

        var cacheExpiration: Long = 3600 // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        cacheExpiration = 0

        btn.setOnClickListener {
            firebaseAnalytics.setUserId("testUser")
            prop = (prop + 1) % 2
            firebaseAnalytics.setUserProperty("testProperty", prop.toString())
            Log.d("APPLOG", prop.toString())
            firebaseAnalytics.logEvent("testEvent", null)

            remoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        remoteConfig.activateFetched()
                        Log.d("APPLOG", "FETCHED VAL PROP: " + remoteConfig.getBoolean("prop1"))
                        Log.d("APPLOG", "FETCHED VAL AUDIENCE: " + remoteConfig.getBoolean("prop2"))
                    } else {
                        Log.d("APPLOG", "failed")
                    }
                }
        }
    }
}
