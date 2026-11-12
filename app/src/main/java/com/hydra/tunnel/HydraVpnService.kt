package com.hydra.tunnel

import android.net.VpnService
import android.content.Intent
import android.os.IBinder

/**
 * Android VPN lifecycle boundary.
 *
 * This service intentionally does not establish a TUN interface yet. A TUN
 * interface without a working packet-forwarding provider would black-hole
 * device traffic. It must only be enabled for sessions backed by a verified
 * transport adapter and packet bridge.
 */
class HydraVpnService : VpnService() {

    override fun onBind(intent: Intent?): IBinder? = super.onBind(intent)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ConnectionCoordinator.transition(ConnectionState.Failed(
            "VPN transport is not ready. No TUN interface was created."
        ))
        stopSelf(startId)
        return START_NOT_STICKY
    }

    override fun onRevoke() {
        if (ConnectionCoordinator.state.value !is ConnectionState.Idle) {
            ConnectionCoordinator.transition(ConnectionState.Stopping)
            ConnectionCoordinator.transition(ConnectionState.Idle)
        }
        super.onRevoke()
    }

    override fun onDestroy() {
        if (ConnectionCoordinator.state.value !is ConnectionState.Idle) {
            ConnectionCoordinator.transition(ConnectionState.Idle)
        }
        super.onDestroy()
    }
}
