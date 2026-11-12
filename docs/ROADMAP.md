# Hydra Tunnel Roadmap

## Phase 0 — Repository and design
- [x] Create public repository
- [x] Document product scope and initial architecture
- [x] Record that the app has no built-in server directory
- [x] Add GPL-3.0 project license
- [x] Review candidate upstream repositories and record evaluation decision

## Phase 1 — Android foundation
- [x] Create initial Android Gradle project
- [x] Set application ID and display name (Hydra Tunnel)
- [x] Build initial Material 3 navigation: Home, Configs, Settings
- [ ] Add dark and AMOLED appearance options
- [x] Add connection StateFlow coordinator (profile screen state remains local Compose state)
- [x] Add profile model, validation, and local persistence
- [x] Add JSON import/export with strict size, version, and profile validation
- [x] Add unit tests for profile validation and connection lifecycle (UI tests remain pending)

## Phase 2 — Android VPN foundation
- [x] Register Android VpnService lifecycle boundary (TUN creation intentionally disabled until transport is ready)
- [ ] Define TUN packet ownership and forwarding interfaces
- [ ] Add deterministic start, stop, cancellation, and cleanup state machine
- [ ] Prevent routing loops for app-owned sockets
- [ ] Add permission-denied, revoked, process-death, and network-change instrumentation tests

## Phase 3 — Transport compatibility review
- [x] Select EndPositive/slipstream as preferred source-evaluation candidate
- [x] Review upstream CLI arguments, Meson build graph, submodules, and Apache-2.0 license
- [x] Add typed Kotlin launch specification and argument-vector tests
- [ ] Pin exact upstream commit and build Android ABI artifacts in reproducible CI
- [ ] Integrate and test native process lifecycle and local TCP listener readiness
- [ ] Determine whether a supported Android NDK build is feasible
- [ ] Document ABI, native interface, and maintenance requirements
- [ ] Do not integrate or redistribute until compatibility and license reviews pass

## Phase 4 — Diagnostics and quality
- [x] Add basic VPN/transport readiness diagnostics (structured redacted logs remain pending)
- [x] Add explicit connection-state and traffic snapshot models
- [ ] Profile CPU, RAM, battery, and thermal impact
- [ ] Test on low-end and current Android devices
- [ ] Test airplane mode, network changes, IPv6, and process death
- [ ] Add CI build and automated tests
- [ ] Document user setup, limitations, and troubleshooting
- [ ] Prepare reproducible release builds

## Definition of done

A feature is complete only when it has implementation, tests, documented limitations, and device validation appropriate to its risk. UI mockups and configuration fields alone do not count as a working VPN feature. Never claim full-device VPN support until packet forwarding has been implemented and tested on physical devices.
