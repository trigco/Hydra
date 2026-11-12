# Hydra Tunnel — Android Architecture

## 1. Product model

Hydra Tunnel is a user-configured Android VPN client. It does not ship with a public server directory. Users manage their own local profiles and only connect to networks and endpoints they are authorized to use.

Navigation:
- Home
- Configs
- Settings

## 2. High-level components

```text
Jetpack Compose UI
       |
ViewModels / StateFlow
       |
Profile Repository + Connection Coordinator
       |
Android VpnService
       |
TUN interface
       |
Packet Forwarding Interface
       |
Approved, independently reviewed transport provider
```

This is a target architecture, not a claim that these components are implemented.

## 3. Android VPN lifecycle

The Android layer is responsible for:
- Requesting and handling VPN permission.
- Starting a foreground service with a clear user-visible notification.
- Building and closing the TUN interface.
- Handling user stop, permission revocation, process death, and network changes.
- Reporting connection state without blocking the main thread.
- Releasing file descriptors, sockets, and worker resources deterministically.

The connection coordinator should expose explicit states such as Idle, Preparing, Connecting, Connected, Reconnecting, Stopping, and Failed. Invalid transitions must be rejected and surfaced as actionable errors.

## 4. Packet forwarding boundary

A TUN interface provides IP packets. A transport provider may expose a different interface. These interfaces must not be treated as interchangeable.

Any future packet-forwarding component must have a documented contract for packet ownership, supported protocols, backpressure, cancellation, errors, and shutdown. Do not claim full-device VPN support until packet forwarding is implemented and verified with packet-level tests on physical devices.

## 5. DNS and network configuration

DNS behavior must be explicit and documented. The app must not claim leak protection based only on a configured resolver address. Any DNS policy requires tests for IPv4, IPv6, reconnects, network changes, resolver failure, and VPN permission revocation.

Do not add parallel resolver requests or aggressive retry behavior without a bounded resource policy, a clear user benefit, and tests for query amplification and failure handling.

## 6. MTU

MTU belongs to the Android TUN interface and must be coordinated with the actual forwarding path. Manual values require validation. Automatic MTU selection should only be added after a reliable path-MTU strategy exists.

Do not silently apply a value that the underlying path cannot carry. Test large packets, fragmentation, IPv4/IPv6 behavior, and path changes.

## 7. Performance principles

- Keep UI work off network and native threads.
- Use bounded queues and backpressure.
- Avoid one operating-system thread per connection or packet.
- Reuse buffers where safe and avoid unnecessary per-packet allocations.
- Sample UI traffic statistics at a modest interval.
- Batch diagnostic writes and keep verbose logging opt-in.
- Cancel workers and close native resources deterministically.
- Profile CPU, memory, battery, and thermal behavior on physical devices, including lower-end hardware.

## 8. Security and configuration handling

- Do not store credentials or private keys in logs.
- Validate imported profile data and enforce file and field size limits.
- Never execute arbitrary commands from imported configuration files.
- Keep third-party license and copyright notices intact.
- Review native dependencies and their licenses before redistribution.
- Do not expose secrets in crash reports or diagnostic exports.

## 9. Current implementation status

The repository currently contains an early Android UI/build scaffold and documentation. The VPN service, TUN packet forwarding, transport provider, profile persistence, DNS policy, and MTU application are not implemented.