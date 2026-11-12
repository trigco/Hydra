package com.hydra.tunnel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SlipstreamLaunchSpecTest {

    private fun validSpec() = SlipstreamLaunchSpec(
        executablePath = "/data/app/lib/libslipstream_client.so",
        domain = "tunnel.example.com",
        resolvers = listOf("1.1.1.1:53", "[2001:db8::1]:53"),
        tcpListenPort = 5201
    )

    @Test
    fun validatesLaunchConfiguration() {
        assertNull(validSpec().validate())
    }

    @Test
    fun createsArgumentVectorWithoutShellParsing() {
        val argv = validSpec().toArgv()

        assertTrue(argv.contains("--domain"))
        assertTrue(argv.contains("tunnel.example.com"))
        assertEquals(2, argv.count { it == "--resolver" })
    }

    @Test
    fun rejectsUnsupportedCongestionControl() {
        assertEquals(
            "Unsupported congestion-control algorithm.",
            validSpec().copy(congestionControl = "unknown").validate()
        )
    }

    @Test
    fun rejectsMissingExecutable() {
        assertEquals(
            "Slipstream executable is not packaged.",
            validSpec().copy(executablePath = "").validate()
        )
    }
}
