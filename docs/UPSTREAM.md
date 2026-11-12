# Slipstream Upstream Review

## Current evaluation decision

**Preferred upstream for technical evaluation:** [EndPositive/slipstream](https://github.com/EndPositive/slipstream).

This is the original Slipstream project, not the archived Rust rewrite. Its repository contains C and C++ source files, uses Meson/Ninja for builds, and includes PicoQUIC as a Git submodule. The project describes itself as a high-performance DNS tunnel using QUIC multipath, adaptive congestion control, and parallel routing across resolvers. The upstream README explicitly describes it as a covert channel, so Hydra treats it as a user-operated transport only for domains, resolvers, and servers the operator owns or is authorized to use.

Repository evidence reviewed on 2026-11-12:
- Source tree: https://github.com/EndPositive/slipstream
- Build definition: https://github.com/EndPositive/slipstream/blob/main/meson.build
- Submodules: https://github.com/EndPositive/slipstream/blob/main/.gitmodules
- License: https://github.com/EndPositive/slipstream/blob/main/LICENSE
- README and upstream documentation: https://github.com/EndPositive/slipstream

Recent upstream commits visible at review time included server-side nonblocking I/O, bounded write queues, and TCP half-close handling. This is evidence of recent source activity, not a guarantee of security, Android compatibility, or production readiness.

## Android integration status

No engine binary or upstream source has been integrated into Hydra. Android NDK compatibility has **not** been established. A typed Kotlin launch-argument contract has been added, but it is not a process runner and cannot start a tunnel.

Before native integration, an independent review and reproducible build must establish:
- Supported Android ABIs and toolchain requirements.
- Whether the upstream build and all submodules can be built reproducibly with the Android NDK.
- Native dependency and license compatibility.
- A documented, supported application interface and lifecycle.
- Resource ownership, cancellation, error propagation, and safe shutdown.
- Independent security and performance review.

Hydra must not treat a desktop/Linux binary as an Android-ready library. No upstream source or binary has been copied into this repository.

## Confirmed upstream client interface

The current upstream client CLI accepts `--tcp-listen-port`, one or more `--resolver` values, `--domain`, `--congestion-control` (`bbr` or `dcubic`), `--gso`, and `--keep-alive-interval`. The client exposes a local TCP listener; it is not itself an Android TUN packet forwarder. Hydra must therefore provide and test a separate packet-to-stream bridge before full-device VPN routing can be enabled. The current upstream `meson.build` builds separate `slipstream-client` and `slipstream-server` executables and pulls Picoquic through a CMake subproject. Its `.gitmodules` also lists SPCDNS, lua-resty-base-encoding, and quick_arg_parser.

## Android architecture reference

[VortexOne404/slipstream-client-android](https://github.com/VortexOne404/slipstream-client-android) is useful only as an architectural reference for Android VpnService, TUN ownership, and a tun2socks-style packet bridge. Its README describes a TUN-to-tun2socks-to-Slipstream/SOCKS pipeline and notes that UDP/IPv6 behavior depends on the selected implementation. Its claims about throughput and efficiency are not independent benchmarks.

Do not copy its implementation without a separate code, dependency, and license review.

## Other candidates reviewed

- [Mygod/slipstream-rust](https://github.com/Mygod/slipstream-rust): archived. Its maintainer's deprecation discussion explains the maintenance burden of the DNS-over-QUIC compatibility layer and custom multipath stack.
- [Fox-Fig/slipstream-rust-plus](https://github.com/Fox-Fig/slipstream-rust-plus): archived, with the latest visible commits dated March 2026. Its README contains performance claims that have not been independently verified.
- [MasterDnsVPN](https://github.com/masterking32/MasterDnsVPN): a separate project, not a Slipstream fork; it is outside the current engine selection.

## Hydra integration policy

- Keep the Android UI and app lifecycle separate from any transport implementation.
- Do not vendor upstream code or binaries until license and dependency notices have been reviewed.
- Pin an exact upstream revision for any future evaluation.
- Keep third-party license and copyright notices intact.
- Do not claim VPN, DNS leak protection, throughput, or battery performance without implementation and repeatable tests.
- Use only networks and endpoints the operator owns or is authorized to use.

## Decision

EndPositive's original project is the preferred candidate for a **source and compatibility evaluation only**. This is not a claim that an Android integration exists. Use only operator-owned or explicitly authorized domains, resolvers, and server infrastructure. A general-purpose Android VPN foundation can be developed independently of that decision.
