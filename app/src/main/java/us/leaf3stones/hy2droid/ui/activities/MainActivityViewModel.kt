package us.leaf3stones.hy2droid.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import us.leaf3stones.hy2droid.data.repository.HysteriaConfigRepository
import us.leaf3stones.hy2droid.data.model.HysteriaConfig
import us.leaf3stones.hy2droid.proxy.Hysteria2VpnService

class MainActivityViewModel : ViewModel() {
    private val _state = MutableStateFlow(UiState(false, HysteriaConfig(), false))
    val state get() = _state.asStateFlow()

    private val configRepo = HysteriaConfigRepository()

    init {
        viewModelScope.launch {
            val config = configRepo.loadConfig()
            _state.update {
                it.copy(configData = config)
            }
        }
    }

    fun startVpnService(context: Context) {
        val vpnStatusObserver = object : Hysteria2VpnService.Companion.VpnStatusObserver {
            override fun onVpnStarted() {
                _state.update { curr ->
                    curr.copy(isVpnConnected = true)
                }
            }

            override fun onVpnStopped() {
                _state.update { curr ->
                    curr.copy(isVpnConnected = false)
                }
            }
        }
        Hysteria2VpnService.addObserver(vpnStatusObserver)

        Intent(context, Hysteria2VpnService::class.java).apply {
            setAction(Hysteria2VpnService.ACTION_START_VPN)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(context, this)
            } else {
                context.startService(this)
            }
        }
    }

    fun stopVpnService(context: Context) {
        Intent(context, Hysteria2VpnService::class.java).apply {
            setAction(Hysteria2VpnService.ACTION_STOP_VPN)
            context.startService(this)
        }
    }


    fun onServerChanged(string: String) {
        onConfigDataChanged(_state.value.configData.copy(server = string))
    }

    fun onPasswordChanged(password: String) {
        onConfigDataChanged(_state.value.configData.copy(password = password))
    }

    fun onSniChanged(sni: String) {
        onConfigDataChanged(_state.value.configData.copy(sni = sni))
    }

    private fun onConfigDataChanged(newConfigData: HysteriaConfig) {
        _state.update {
            it.copy(configData = newConfigData)
        }
    }

    fun onConfigConfirmed() {
        Log.d("tag", "confirmed: " + _state.value.configData)
        if (isUserConfigValid()) {
            viewModelScope.launch {
                configRepo.saveConfig(_state.value.configData)
            }
        } else {
            _state.update {
                it.copy(shouldShowConfigInvalidRemainder = true)
            }
        }
    }

    fun onConfigInvalidRemainderDismissed() {
        _state.update {
            it.copy(shouldShowConfigInvalidRemainder = false)
        }
    }

    private fun isUserConfigValid(): Boolean {
        val config = _state.value.configData
        return config.server.isNotBlank() && config.password.isNotBlank()
    }
}

data class UiState(
    val isVpnConnected: Boolean,
    val configData: HysteriaConfig,
    val shouldShowConfigInvalidRemainder: Boolean
)
