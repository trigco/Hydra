# Slipstream Upstream Review

## Current evaluation decision

**Preferred upstream for technical evaluation:** [EndPositive/slipstream](https://github.com/EndPositive/slipstream).

This is the original Slipstream project, not the archived Rust rewrite. Its repository contains C and C++ source files, uses Meson/Ninja for builds, and includes PicoQUIC as a Git submodule. The project describes itself as a high-performance DNS channel using QUIC multipath, adaptive congestion control, and parallel routing across resolvers.

Repository evidence reviewed on 2026-11-12:
- Source tree: https://github.com/EndPositive/slipstream
- Build definition: https://github.com/EndPositive/slipstream/blob/main/meson.build
- Submodules: https://github.com/EndPositive/slipstream/blob/main/.gitmodules
- License: https://github.com/EndPositive/slipstream/blob/main/LICENSE
- README and upstream documentation: https://github.com/EndPositive/slipstream

Recent upstream commits visible at review time included server-side nonblocking I/O, bounded write queues, and TCP half-close handling. This is evidence of recent source activity, not a guarantee of security, Android compatibility, or production readiness.

## Android integration status

No engine has been integrated into Hydra. Android NDK compatibility has **not** been established.

Before any future native integration, an independent review would need to establish:
- Supported Android ABIs and toolchain requirements.
- Whether the upstream build and all submodules can be built reproducibly with the Android NDK.
- Native dependency and license compatibility.
- A documented, supported application interface and lifecycle.
- Resource ownership, cancellation, error propagation, and safe shutdown.
- Independent security and performance review.

Hydra must not treat a desktop/Linux binary as an Android-ready library. No upstream source or binary has been copied into this repository.

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

EndPositive's original project is the preferred candidate for a **source and compatibility evaluation only**. This is not approval to deploy a DNS covert tunnel or a claim that an Android integration exists. A general-purpose Android VPN foundation can be developed independently of that decision.
