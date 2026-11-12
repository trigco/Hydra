package com.hydra.tunnel

/**
 * Typed command contract for the upstream Slipstream client.
 *
 * This does not execute a process. The native binary must first be built for
 * the target Android ABI, audited, packaged, and verified by integration tests.
 */
data class SlipstreamLaunchSpec(
    val executablePath: String,
    val domain: String,
    val resolvers: List<String>,
    val tcpListenPort: Int,
    val congestionControl: String = "dcubic",
    val keepAliveInterval: Int = 400,
    val enableGso: Boolean = false
) {
    fun validate(): String? {
        if (executablePath.isBlank()) return "Slipstream executable is not packaged."
        if (domain.isBlank()) return "Tunnel domain is required."
        if (resolvers.isEmpty()) return "At least one resolver is required."
        if (resolvers.any { it.isBlank() }) return "Resolver values must not be blank."
        if (tcpListenPort !in 1..65535) return "Local TCP listen port is invalid."
        if (congestionControl !in setOf("bbr", "dcubic")) return "Unsupported congestion-control algorithm."
        if (keepAliveInterval !in 0..3600) return "Keep-alive interval must be 0–3600."
        return null
    }

    /**
     * Arguments follow the upstream slipstream-client CLI contract.
     * Values are separate argv entries; no shell interpolation is used.
     */
    fun toArgv(): List<String> = buildList {
        add("--tcp-listen-port")
        add(tcpListenPort.toString())
        resolvers.forEach { resolver ->
            add("--resolver")
            add(resolver)
        }
        add("--domain")
        add(domain)
        add("--congestion-control")
        add(congestionControl)
        add("--keep-alive-interval")
        add(keepAliveInterval.toString())
        if (enableGso) add("--gso")
    }
}
