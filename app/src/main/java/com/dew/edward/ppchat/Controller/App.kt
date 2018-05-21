package com.dew.edward.ppchat.Controller

import android.app.Application
import android.support.v4.content.LocalBroadcastManager
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.dew.edward.ppchat.Model.UserProfile
import com.dew.edward.ppchat.Utilities.SOCKET_URL
import com.dew.edward.ppchat.Utilities.SharedPrefs
import io.socket.client.IO

/*
 * Created by Edward on 5/20/2018.
 */

class App: Application(){
    companion object {
        lateinit var sharedPrefs: SharedPrefs
        lateinit var requestQueue: RequestQueue
        lateinit var localBroadcastManager: LocalBroadcastManager
    }

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = SharedPrefs(applicationContext)
        requestQueue = Volley.newRequestQueue(applicationContext)
        localBroadcastManager = LocalBroadcastManager.getInstance(applicationContext)
    }
}
