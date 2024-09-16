package us.leaf3stones.hy2droid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.vpnPrefDataStore: DataStore<Preferences> by preferencesDataStore(name = "vpn_preferences")
val KEY_IS_VPN_CONFIG_READY = booleanPreferencesKey("is_vpn_config_ready")
val KEY_VPN_CONFIG_PATH = stringPreferencesKey("vpn_config_path")
const val MASTER_HYSTERIA_CONFIG_FILE_NAME = "master-conf.yaml"
const val TUN2SOCKS_CONFIG_FILE_NAME = "tun2socks.yaml"


const val TUN2SOCKS_CONFIG_TEXT_DATA = """
tunnel:
  # Interface name
  name: tun0
  # Interface MTU
  mtu: 1500
  # Multi-queue
  multi-queue: false
  # IPv4 address
  ipv4: 10.0.88.88
  # IPv6 address
  ipv6: 'fc00::1'

socks5:
  # Socks5 server port
  port: 1080
  # Socks5 server address (ipv4/ipv6)
  address: 127.0.0.1
  # Socks5 UDP relay mode (tcp|udp)
  udp: 'udp'
"""