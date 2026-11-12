package com.hydra.tunnel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface ConnectionState {
    data object Idle : ConnectionState
    data object Preparing : ConnectionState
    data object Connecting : ConnectionState
    data object Connected : ConnectionState
    data object Stopping : ConnectionState
    data class Failed(val reason: String) : ConnectionState
}

data class TrafficSnapshot(
    val receivedBytesPerSecond: Long = 0L,
    val transmittedBytesPerSecond: Long = 0L,
    val receivedBytesTotal: Long = 0L,
    val transmittedBytesTotal: Long = 0L
)

object ConnectionCoordinator {
    private val mutableState = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    val state: StateFlow<ConnectionState> = mutableState.asStateFlow()

    private val mutableTraffic = MutableStateFlow(TrafficSnapshot())
    val traffic: StateFlow<TrafficSnapshot> = mutableTraffic.asStateFlow()

    @Synchronized
    fun transition(next: ConnectionState) {
        val current = mutableState.value
        val allowed = when (current) {
            ConnectionState.Idle -> next is ConnectionState.Preparing || next is ConnectionState.Failed
            ConnectionState.Preparing -> next is ConnectionState.Connecting || next is ConnectionState.Stopping || next is ConnectionState.Failed
            ConnectionState.Connecting -> next is ConnectionState.Connected || next is ConnectionState.Stopping || next is ConnectionState.Failed
            ConnectionState.Connected -> next is ConnectionState.Stopping || next is ConnectionState.Failed
            ConnectionState.Stopping -> next is ConnectionState.Idle || next is ConnectionState.Failed
            is ConnectionState.Failed -> next is ConnectionState.Idle || next is ConnectionState.Preparing
        }
        require(allowed) { "Invalid connection state transition: $current -> $next" }
        mutableState.value = next
        if (next is ConnectionState.Idle || next is ConnectionState.Failed) {
            mutableTraffic.value = TrafficSnapshot()
        }
    }

    @Synchronized
    fun reset() {
        mutableState.value = ConnectionState.Idle
        mutableTraffic.value = TrafficSnapshot()
    }

    fun publishTraffic(snapshot: TrafficSnapshot) {
        require(snapshot.receivedBytesPerSecond >= 0L && snapshot.transmittedBytesPerSecond >= 0L)
        require(snapshot.receivedBytesTotal >= 0L && snapshot.transmittedBytesTotal >= 0L)
        if (mutableState.value is ConnectionState.Connected) mutableTraffic.value = snapshot
    }
}
