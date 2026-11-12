# Hydra Tunnel

Hydra Tunnel is an open-source Android VPN client project focused on a clean user experience, user-managed profiles, and a modular application architecture.

> **Status: early development.** The repository currently contains an Android UI/build scaffold and project documentation. A working VPN connection, packet-forwarding data plane, DNS subsystem, and MTU application have not been implemented.

## Project goals

- A responsive Android interface built with Kotlin and Jetpack Compose.
- A proper Android VpnService lifecycle and explicit packet-forwarding architecture.
- Editable, importable, exportable, and locally stored user profiles.
- Clear connection state, diagnostics, and error reporting.
- Low CPU, memory, battery, and thermal overhead, verified through device profiling.

## Planned app navigation

- **Home** — connection state, traffic counters, and connection controls.
- **Configs** — create, edit, duplicate, import, export, select, and delete local profiles.
- **Settings** — appearance, diagnostics, and app preferences.

There will be no built-in public server directory. Users manage their own profiles and endpoints.

## Technology direction

| Layer | Planned technology |
|---|---|
| Android app | Kotlin, Jetpack Compose, Material 3 |
| VPN lifecycle | Android VpnService |
| Packet handling | A separately reviewed and tested packet-forwarding component |
| Native integration | Android NDK; exact interface to be selected after compatibility review |
| Local preferences | DataStore / local persistence, implementation pending |
| Testing | Kotlin tests, native tests where applicable, Android device profiling |

## Upstream evaluation

[EndPositive/slipstream](https://github.com/EndPositive/slipstream) is the preferred candidate for source and compatibility evaluation. It is a C/C++ project with a Meson/Ninja build and PicoQUIC submodule. Android NDK compatibility has not been established, and no upstream source or binary has been integrated.

The archived Rust rewrite and other candidates are documented in [docs/UPSTREAM.md](docs/UPSTREAM.md). The Android VPN foundation remains transport-independent.

## Development status

See [ROADMAP](docs/ROADMAP.md) and [ARCHITECTURE](docs/ARCHITECTURE.md).

## Open source and licensing

Hydra-authored project code is licensed under the GNU General Public License v3.0; see [COPYING](COPYING). Third-party code and dependencies retain their own licenses and notices. Review all applicable terms before redistributing third-party source or binaries.

## Safety and scope

Only configure networks and endpoints you own or are authorized to use. Hydra does not provide VPN servers or network access by itself.
