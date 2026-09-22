package za.co.wrightcut

import android.app.Application
import za.co.wrightcut.data.local.WrightCutDatabase
import za.co.wrightcut.data.network.AuthTokenManager

class WrightCutApplication : Application() {
    lateinit var database: WrightCutDatabase
        private set
    lateinit var authTokenManager: AuthTokenManager
        private set

    override fun onCreate() {
        super.onCreate()
        database = WrightCutDatabase.getDatabase(this)
        authTokenManager = AuthTokenManager(this)
    }
}
