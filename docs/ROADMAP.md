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
- [ ] Add ViewModel + StateFlow UI state
- [ ] Add profile model, validation, and local persistence
- [ ] Add import/export with strict input validation
- [ ] Add unit and UI tests for profile management

## Phase 2 — Android VPN foundation
- [ ] Implement Android VpnService permission and foreground lifecycle
- [ ] Define TUN packet ownership and forwarding interfaces
- [ ] Add deterministic start, stop, cancellation, and cleanup state machine
- [ ] Prevent routing loops for app-owned sockets
- [ ] Add permission-denied, revoked, process-death, and network-change tests

## Phase 3 — Transport compatibility review
- [x] Select EndPositive/slipstream as preferred source-evaluation candidate
- [ ] Review exact upstream revision, build requirements, and submodule licenses
- [ ] Determine whether a supported Android NDK build is feasible
- [ ] Document ABI, native interface, and maintenance requirements
- [ ] Do not integrate or redistribute until compatibility and license reviews pass

## Phase 4 — Diagnostics and quality
- [ ] Add structured diagnostics with secrets redacted
- [ ] Add clear connection-state and error models
- [ ] Profile CPU, RAM, battery, and thermal impact
- [ ] Test on low-end and current Android devices
- [ ] Test airplane mode, network changes, IPv6, and process death
- [ ] Add CI build and automated tests
- [ ] Document user setup, limitations, and troubleshooting
- [ ] Prepare reproducible release builds

## Definition of done

A feature is complete only when it has implementation, tests, documented limitations, and device validation appropriate to its risk. UI mockups and configuration fields alone do not count as a working VPN feature. Never claim full-device VPN support until packet forwarding has been implemented and tested on physical devices.
