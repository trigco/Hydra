# Hydra Tunnel Roadmap

## Phase 0 — Repository and design
- [x] Create public repository
- [x] Document product scope and initial architecture
- [x] Record that the app has no built-in server directory
- [ ] Choose and add an explicit open-source license
- [ ] Review Slipstream upstream license and integration requirements

## Phase 1 — Android foundation
- [ ] Create Android Gradle project
- [ ] Set application ID and display name (`Hydra Tunnel`)
- [ ] Build Material 3 navigation: Home, Configs, Settings
- [ ] Add dark and AMOLED appearance options
- [ ] Add ViewModel + StateFlow UI state
- [ ] Add configuration model, validation, and local persistence
- [ ] Add import/export with strict input validation

## Phase 2 — Slipstream engine integration
- [ ] Pin and document the upstream Slipstream revision
- [ ] Confirm supported Android ABIs and native build requirements
- [ ] Build Rust native libraries for supported ABIs
- [ ] Define a small, versioned JNI/FFI interface
- [ ] Implement engine start, status, error, and stop lifecycle
- [ ] Add deterministic cancellation and native resource cleanup

## Phase 3 — Android VPN data plane
- [ ] Implement Android VpnService permission and foreground lifecycle
- [ ] Define TUN packet ownership and forwarding model
- [ ] Implement the supported TCP/UDP flow bridge
- [ ] Prevent VPN routing loops for engine and resolver sockets
- [ ] Verify DNS routing and IPv4/IPv6 behavior
- [ ] Run packet-level tests before claiming full-device VPN support

## Phase 4 — DNS and MTU
- [ ] Implement resolver configuration and health tracking
- [ ] Implement bounded DNS worker queue
- [ ] Implement primary/fallback mode
- [ ] Implement fastest-healthy selection
- [ ] Implement opt-in aggressive resolver racing
- [ ] Validate DNS responses and implement TTL-aware cache
- [ ] Implement MTU manual setting and validation
- [ ] Add auto MTU only after a tested path-MTU strategy exists
- [ ] Test DNS failure, timeout, cancellation, reconnect, and leak scenarios

## Phase 5 — Performance and release readiness
- [ ] Add structured diagnostics with secrets redacted
- [ ] Profile CPU, RAM, battery, and thermal impact
- [ ] Test on low-end and current Android devices
- [ ] Test airplane mode, network changes, IPv6, and process death
- [ ] Add CI build and automated tests
- [ ] Document user setup, limitations, and troubleshooting
- [ ] Prepare reproducible release builds

## Definition of done

A feature is complete only when it has implementation, tests, documented limitations, and device validation appropriate to its risk. UI mockups and configuration fields alone do not count as a working VPN feature.
