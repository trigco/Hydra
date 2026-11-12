# Slipstream Rust Upstream Review

Hydra Tunnel currently targets [Mygod/slipstream-rust](https://github.com/Mygod/slipstream-rust).

## Maintenance status

The upstream repository was archived on 2026-08-28 and its maintainer posted a deprecation notice on the same date. The maintainer says active maintenance has stopped because the DNS-over-QUIC architecture requires substantial DNS-specific adaptations to QUIC, resolver-aware scheduling remains application-specific, and maintaining the custom picoquic/multipath stack has ongoing cost.

References:
- Repository: https://github.com/Mygod/slipstream-rust
- Deprecation discussion: https://github.com/Mygod/slipstream-rust/discussions/111
- Upstream license: Apache-2.0

## Integration policy

- Treat upstream as an archived dependency, not an actively maintained platform.
- Pin any evaluated upstream revision; do not track a floating branch in release builds.
- Preserve Apache-2.0 license and copyright notices for upstream code and binaries.
- Audit native dependencies, build scripts, submodules, and supported Android ABIs before vendoring.
- Keep the Android app boundary small so the engine can be replaced if required.
- Do not ship a production APK until security, compatibility, and maintenance risks have been reviewed.

## Decision gate

Before implementing the production engine adapter, decide whether to:
1. Integrate the archived upstream at a pinned revision for a limited prototype; or
2. Maintain a fork with an explicit patch/update policy; or
3. Select a different actively maintained transport implementation.

No option is selected by this document. The current Android UI scaffold is independent of the transport implementation.
