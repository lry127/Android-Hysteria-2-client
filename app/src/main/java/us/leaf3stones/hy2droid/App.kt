package us.leaf3stones.hy2droid

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.runBlocking
import us.leaf3stones.hy2droid.data.KEY_IS_VPN_CONFIG_READY
import us.leaf3stones.hy2droid.data.TUN2SOCKS_CONFIG_FILE_NAME
import us.leaf3stones.hy2droid.data.TUN2SOCKS_CONFIG_TEXT_DATA
import us.leaf3stones.hy2droid.data.vpnPrefDataStore
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

class App : Application() {
    companion object {
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        val tun2socksConfigFile = File(filesDir, TUN2SOCKS_CONFIG_FILE_NAME)
        runBlocking {
            vpnPrefDataStore.edit { pref ->
                pref[KEY_IS_VPN_CONFIG_READY] = true
            }
        }

        FileOutputStream(tun2socksConfigFile).use {
            it.write(TUN2SOCKS_CONFIG_TEXT_DATA.toByteArray(StandardCharsets.UTF_8))
        }
    }
}