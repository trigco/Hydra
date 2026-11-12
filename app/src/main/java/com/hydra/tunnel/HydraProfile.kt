package com.hydra.tunnel

import org.json.JSONObject
import java.util.UUID

data class HydraProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "My profile",
    val domain: String = "",
    val listenPort: Int = 5201,
    val resolvers: List<String> = listOf("1.1.1.1:53", "8.8.8.8:53", "9.9.9.9:53"),
    val aggressiveDns: Boolean = false,
    val dnsWorkers: Int = 4,
    val mtu: Int = 1280
) {
    fun validate(): String? {
        if (name.isBlank() || name.length > 48) return "Name must be 1–48 characters."
        if (!isValidHost(domain)) return "Enter a valid tunnel domain."
        if (listenPort !in 1..65535) return "Port must be between 1 and 65535."
        if (resolvers.isEmpty() || resolvers.size > 3) return "Add between 1 and 3 DNS resolvers."
        if (resolvers.any { !isValidResolver(it) }) return "A resolver must be a hostname or IP address with an optional port."
        if (dnsWorkers !in 1..8) return "DNS workers must be between 1 and 8."
        if (mtu !in 1280..1500) return "MTU must be between 1280 and 1500."
        return null
    }

    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("name", name)
        .put("domain", domain)
        .put("listenPort", listenPort)
        .put("resolvers", org.json.JSONArray(resolvers))
        .put("aggressiveDns", aggressiveDns)
        .put("dnsWorkers", dnsWorkers)
        .put("mtu", mtu)

    companion object {
        private val HOST = Regex("^(?=.{1,253}$)([A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)(\\.([A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?))*$")
        private val IPV4 = Regex("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")

        private fun isValidHost(value: String): Boolean =
            value.length <= 253 && HOST.matches(value)

        private fun isValidResolver(value: String): Boolean {
            val input = value.trim()
            if (input.isEmpty() || input.length > 260) return false
            val host: String
            val port: String?
            if (input.startsWith("[")) {
                val end = input.indexOf(']')
                if (end < 0) return false
                host = input.substring(1, end)
                val suffix = input.substring(end + 1)
                if (suffix.isNotEmpty() && !suffix.startsWith(":")) return false
                port = suffix.removePrefix(":").ifEmpty { null }
                if (host.isEmpty() || host.any { !(it.isDigit() || it in "abcdefABCDEF:.%") }) return false
            } else {
                val split = input.lastIndexOf(':')
                if (split > 0 && input.indexOf(':') == split) {
                    host = input.substring(0, split)
                    port = input.substring(split + 1)
                } else {
                    host = input
                    port = null
                }
                val validHost = HOST.matches(host) || (IPV4.matches(host) && host.split('.').all { it.toIntOrNull() in 0..255 })
                if (!validHost) return false
            }
            return port == null || (port.toIntOrNull() in 1..65535)
        }

        fun fromJson(json: JSONObject): HydraProfile {
            val resolverArray = json.optJSONArray("resolvers") ?: org.json.JSONArray()
            val resolvers = buildList {
                for (i in 0 until resolverArray.length()) {
                    val value = resolverArray.optString(i).trim()
                    if (value.isNotEmpty()) add(value)
                }
            }
            return HydraProfile(
                id = json.optString("id").takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString(),
                name = json.optString("name", "Imported profile"),
                domain = json.optString("domain"),
                listenPort = json.optInt("listenPort", 5201),
                resolvers = resolvers,
                aggressiveDns = json.optBoolean("aggressiveDns", false),
                dnsWorkers = json.optInt("dnsWorkers", 4),
                mtu = json.optInt("mtu", 1280)
            )
        }
    }
}
