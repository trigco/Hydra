# Hydra Tunnel — Initial Architecture

## 1. Product model

Hydra Tunnel is a user-configured Android VPN client. It does not ship with a server list. A user creates or imports one or more local configurations and chooses which one to activate.

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
Configuration + Connection Coordinator
       |
Android VpnService
       |
TUN interface <----> Packet Forwarding Layer
                         |
                  Slipstream Adapter
                         |
                  Slipstream Rust client

DNS subsystem:
DNS requests -> bounded worker queue -> resolver race/selection
             -> response validation -> cache -> DNS routing
```

This is a target architecture, not a claim that the components are already implemented.

## 3. Slipstream integration boundary

The upstream Slipstream Rust client exposes a TCP listener and has its own DNS/resolver behavior. An Android TUN interface carries IP packets. These are different interfaces and cannot be connected by simply forwarding raw TUN packets to the Slipstream TCP listener.

Before implementing the bridge, inspect the exact upstream client interfaces and supported operating modes. The adapter must define how TCP and UDP flows are handled, how DNS is routed, how sockets are protected from VPN routing loops, and how shutdown/cancellation works.

Do not claim full-device VPN support until the TUN-to-engine forwarding path has been implemented and tested.

## 4. DNS subsystem

### Resolver configuration

Each resolver entry should contain:
- Stable identifier and display name
- Transport and endpoint (for example, UDP/TCP DNS or an explicitly supported encrypted DNS transport)
- Enabled state
- Optional timeout override
- Last observed latency and health state (runtime-only)

### Aggressive DNS mode

Proposed behavior:
1. Accept a DNS request and apply per-client/request deduplication where safe.
2. Send the same request concurrently to a bounded set of enabled resolvers.
3. Accept the first response that passes transaction-ID, question, format, and configured validation checks.
4. Return the accepted response and cancel outstanding work.
5. Update resolver health/latency observations.
6. Cache only responses permitted by the cache policy and their TTL.

Important safeguards:
- A bounded worker pool and bounded queue; no unbounded task or thread creation.
- Per-resolver and global concurrency limits.
- Short, configurable deadlines and bounded retries.
- Cancellation of losing requests.
- Do not treat a malformed packet, timeout, SERVFAIL, or mismatched question as a winning answer.
- Consider DNSSEC and resolver trust policy; racing does not itself make DNS answers trustworthy.
- Avoid caching failures as successful answers.
- Avoid sending queries to disabled or untrusted resolvers.

The initial design should support three policies:
- **Primary / fallback**
- **Fastest healthy resolver**
- **Aggressive race** (parallel requests; first acceptable response wins)

Aggressive racing increases upstream query volume. It must remain opt-in and bounded.

### DNS routing and leak prevention

The Android VPN routing policy must be explicit. DNS requests must use the intended route and avoid recursive routing through the VPN itself. Use Android's VPN socket-protection mechanisms where appropriate. Test IPv4, IPv6, split routing, reconnect, and failure cases. Do not claim leak protection until packet-level tests verify it.

## 5. MTU

MTU belongs to the Android TUN interface and must be coordinated with the actual forwarding/tunnel path. Provide:
- Auto mode only after a reliable path-MTU strategy exists.
- Manual mode with platform-appropriate validation.
- Clear handling of IPv4/IPv6 minimum requirements.
- Tests for fragmentation, large packets, and path changes.

Do not silently apply a value that the underlying path cannot carry.

## 6. Performance principles

- Keep UI work off network/engine threads and engine work off the main thread.
- Use bounded queues and backpressure.
- Prefer asynchronous tasks over creating one OS thread per DNS worker.
- Reuse packet buffers where safe and avoid avoidable per-packet allocations.
- Sample UI traffic statistics at a modest interval rather than polling continuously.
- Batch or buffer diagnostic writes; make verbose logging opt-in.
- Cancel all workers and close native resources deterministically.
- Profile CPU, memory, battery, and thermal behavior on physical devices, including lower-end hardware.

## 7. Security and configuration handling

- Do not store private keys, credentials, or sensitive endpoint tokens in logs.
- Validate imported configuration data and enforce size limits.
- Never execute arbitrary commands from imported configuration files.
- Keep upstream license notices intact.
- Review the upstream Slipstream license and all dependency licenses before vendoring or redistributing binaries.

## 8. Current implementation status

This document records the intended design. It does not imply that the VPN bridge, DNS engine, resolver racing, or MTU application is implemented.
