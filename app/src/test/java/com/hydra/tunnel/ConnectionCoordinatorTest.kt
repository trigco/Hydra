package com.hydra.tunnel

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConnectionCoordinatorTest {

    @After
    fun resetCoordinator() {
        ConnectionCoordinator.reset()
    }

    @Test
    fun acceptsExpectedLifecycleTransitions() {
        ConnectionCoordinator.transition(ConnectionState.Preparing)
        ConnectionCoordinator.transition(ConnectionState.Connecting)
        ConnectionCoordinator.transition(ConnectionState.Connected)
        ConnectionCoordinator.transition(ConnectionState.Stopping)
        ConnectionCoordinator.transition(ConnectionState.Idle)

        assertEquals(ConnectionState.Idle, ConnectionCoordinator.state.value)
    }

    @Test
    fun rejectsConnectedWithoutConnecting() {
        val error = runCatching {
            ConnectionCoordinator.transition(ConnectionState.Connected)
        }.exceptionOrNull()

        assertTrue(error is IllegalArgumentException)
        assertEquals(ConnectionState.Idle, ConnectionCoordinator.state.value)
    }

    @Test
    fun failedStateCanRetryAfterResetToPreparing() {
        ConnectionCoordinator.transition(ConnectionState.Failed("test failure"))
        ConnectionCoordinator.transition(ConnectionState.Preparing)

        assertEquals(ConnectionState.Preparing, ConnectionCoordinator.state.value)
    }

    @Test
    fun trafficIsOnlyPublishedWhenConnected() {
        val snapshot = TrafficSnapshot(
            receivedBytesPerSecond = 100,
            transmittedBytesPerSecond = 50,
            receivedBytesTotal = 1_000,
            transmittedBytesTotal = 500
        )

        ConnectionCoordinator.publishTraffic(snapshot)
        assertEquals(TrafficSnapshot(), ConnectionCoordinator.traffic.value)

        ConnectionCoordinator.transition(ConnectionState.Preparing)
        ConnectionCoordinator.transition(ConnectionState.Connecting)
        ConnectionCoordinator.transition(ConnectionState.Connected)
        ConnectionCoordinator.publishTraffic(snapshot)

        assertEquals(snapshot, ConnectionCoordinator.traffic.value)
    }
}
