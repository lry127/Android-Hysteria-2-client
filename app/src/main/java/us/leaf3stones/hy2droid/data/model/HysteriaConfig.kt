package us.leaf3stones.hy2droid.data.model

import java.io.Serializable

data class HysteriaConfig(
    val server: String = "",
    val password: String = "",
    val sni: String = ""
) : Serializable {
    fun getFullConfig(): String {
        val mapper = mapOf(SERVER_ADDRESS_PLACEHOLDER to server, PASSWORD_PLACEHOLDER to password)
        var resultingConf = HYSTERIA_CONFIG_TEXT_DATA
        for (m in mapper) {
            resultingConf = resultingConf.replace(m.key, m.value)
        }
        val sniData = if (sni.isBlank()) "" else getSniData(sni)
        return resultingConf.replace(SNI_PLACEHOLDER, sniData)
    }

    private fun getSniData(sni: String): String {
        return """
tls:
    sni: $sni
        """.trimIndent()
    }

    companion object {
        private const val SERVER_ADDRESS_PLACEHOLDER = "__SERVER_ADDRESS_PLACEHOLDER__"
        private const val PASSWORD_PLACEHOLDER = "__PASSWORD_PLACEHOLDER__"
        private const val SNI_PLACEHOLDER = "__SNI_PLACEHOLDER__"
        private const val HYSTERIA_CONFIG_TEXT_DATA = """
server: $SERVER_ADDRESS_PLACEHOLDER

auth: $PASSWORD_PLACEHOLDER

$SNI_PLACEHOLDER

bandwidth:
  up: 10 mbps
  down: 10 mbps

socks5:
  listen: 127.0.0.1:1080

fastOpen: true

http:
  listen: 127.0.0.1:1081
  
quic:
  maxIdleTimeout: 30s 
  keepAlivePeriod: 20s 
  
"""
    }
}