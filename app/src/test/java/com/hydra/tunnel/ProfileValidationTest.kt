package com.hydra.tunnel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileValidationTest {

    @Test
    fun acceptsWellFormedProfile() {
        val profile = HydraProfile(
            name = "Test",
            domain = "tunnel.example.com",
            listenPort = 5201,
            resolvers = listOf("1.1.1.1:53", "dns.google:53"),
            dnsWorkers = 4,
            mtu = 1280
        )

        assertNull(profile.validate())
    }

    @Test
    fun rejectsInvalidHostname() {
        val profile = HydraProfile(domain = "bad host name")

        assertEquals("Enter a valid tunnel domain.", profile.validate())
    }

    @Test
    fun rejectsOutOfRangeMtu() {
        val profile = HydraProfile(domain = "tunnel.example.com", mtu = 1200)

        assertEquals("MTU must be between 1280 and 1500.", profile.validate())
    }

    @Test
    fun rejectsOutOfRangeWorkerCount() {
        val profile = HydraProfile(domain = "tunnel.example.com", dnsWorkers = 9)

        assertEquals("DNS workers must be between 1 and 8.", profile.validate())
    }
}
