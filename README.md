# Hydra Tunnel

Hydra Tunnel is an open-source Android VPN client project built around the Slipstream Rust transport.

> **Status: early development.** The repository currently contains project documentation and architecture planning. A working VPN connection, DNS racing, and MTU controls have not been implemented yet.

## Project goals

- A clean, responsive Android interface built with Kotlin and Jetpack Compose.
- Integrate the upstream Slipstream Rust client through a carefully defined native boundary.
- Provide a correct Android `VpnService` and TUN packet-forwarding architecture.
- Offer editable, importable, and exportable local configurations.
- Provide configurable DNS resolvers, bounded parallel DNS racing, caching, and failover.
- Expose safe MTU controls and validate settings against the actual tunnel path.
- Keep CPU, memory, battery, and thermal overhead low, including on lower-end devices.

## Planned app navigation

- **Home** — connection status, traffic counters, connect/disconnect.
- **Configs** — create, edit, duplicate, import, export, select, and delete configurations.
- **Settings** — DNS, MTU, diagnostics, appearance, and app preferences.

There will be no built-in public server directory. Users configure their own endpoints.

## Technology direction

| Layer | Planned technology |
|---|---|
| Android app | Kotlin, Jetpack Compose, Material 3 |
| VPN lifecycle | Android `VpnService` |
| Tunnel engine | Slipstream Rust (upstream integration) |
| Native bridge | JNI / Rust FFI, selected after integration review |
| Async DNS coordination | Bounded asynchronous tasks; avoid unbounded thread creation |
| Local preferences | DataStore; configuration persistence design to be finalized |
| Testing | Kotlin tests, Rust tests, Android device profiling |

## Development status

See [ROADMAP](docs/ROADMAP.md) and [ARCHITECTURE](docs/ARCHITECTURE.md).

## Upstream project

Hydra Tunnel builds on the work in [Mygod/slipstream-rust](https://github.com/Mygod/slipstream-rust). Upstream code and dependencies retain their original licenses and notices. No upstream source has been copied into this repository yet.

## Open source and licensing

This repository is public, but public visibility alone does not grant permission to reuse, modify, or redistribute its code. A project license still needs to be selected and added. Until then, all rights are reserved by default. Please open an issue or discussion before assuming a reuse license.

## Safety and scope

Only configure endpoints and networks you own or are authorized to use. Hydra Tunnel is a client application; it does not provide VPN servers or network access by itself.
