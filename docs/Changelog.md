# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),  
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [v3.0.0] – 2026-01-08

### Added
- Start screen / main menu with animated blurred background and settings controls.
- Music toggle with persistent enable/disable state.
- Adjustable UI text scaling for accessibility across menus and overlays.
- In-game pause system with dedicated pause menu overlay.
- End-of-game screen displaying final score, run statistics, and leaderboard.
- Persistent leaderboard with binary file storage in the user’s home directory.
- Score system based on time survived, kills, and deaths.
- JavaFX node IDs on key UI elements to support reliable automated testing.

### Changed
- Game flow extended to support explicit states (`RUNNING`, `PAUSED`, `GAME_OVER`) via `GameController`.
- Game timer updated so paused time does not contribute to elapsed run time.
- Restart behaviour updated to reset run statistics to prevent stat farming.
- Application bootstrap (`App`) updated to manage menu → game → end screen transitions.
- Input handling adjusted so all active inputs are cleared when pausing.

### Fixed
- Timer drift where time continued to increase while paused.
- Game timer incorrectly resetting on death rather than on full restart.
- Input leakage caused by held keys when resuming from pause.
- End screen UI not resetting correctly between runs.
- Leaderboard save button allowing repeated saves in a single run.
- Visual issues where pause and end overlays were not blurred or layered correctly.

### Testing
- Added automated tests for main menu, pause behaviour, end screen, and scoring logic.
- Refined tests to target logic and state transitions rather than rendering output.
- Achieved ≥80% line coverage for core gameplay and controller logic.
- Adjusted tests to run consistently under both IntelliJ and Maven runners.

### Notes
- User-specific leaderboard data (`.dat`) is ignored by version control.
- Version 3 builds on the MVC refactor completed in Version 2 without altering core gameplay mechanics.

---

## [v2.0.0] - 2025-12-19

### Added
- Full migration from **Swing to JavaFX**, including new `App` entry point and `FxRenderer`.
- Dedicated `InputHandler` class to fully separate input processing from rendering and logic.
- `EntityState` class to replace inner-state handling and improve extensibility.
- New `Background` class to decouple background rendering from entity drawing.
- Comprehensive Javadocs added across the codebase (Game, Player, Enemy, Entity, Renderer, InputHandler, MapBlock, App).

### Changed
- Refactored architecture to a clear **MVC-style separation**:
    - `Game` acts as the authoritative model and owns all game state.
    - Rendering moved entirely into `FxRenderer`.
    - Input handled independently via `InputHandler`.
- Player, Enemy, and Bullet update loops decomposed into smaller helper methods to reduce complexity and improve readability.
- Shooting logic encapsulated within Player/Game responsibility boundaries.
- Game loop updated to use a **catch-up timing mechanism**, removing FPS dependence on device performance and ensuring stable ~60 FPS behaviour.
- Restart logic centralised in `Game`, removing responsibility from Player.
- Removed all remaining static state from `MapBlocks` and runtime entities.

### Fixed
- Collision physics for bullets and enemies corrected after JavaFX migration.
- Double-jump behaviour restored after refactor.
- Removed lingering dependencies on `Canvas` from Player, Enemy, Bullet, Game, and InputHandler.
- Corrected update order issues caused by earlier mixed rendering/logic responsibilities.

### Documentation
- `SoftwareDesign.md` updated to reflect final v2 architecture and refactoring rationale.
- `Testing.md` updated with completed test coverage notes and refactor-related changes.
- Javadocs standardised and expanded with author tags and clear responsibility descriptions.

### Notes
- This release represents the **completion of Task 4**.
- Tag created: `v2.0.0`.

---

## [v1.0.0] - 2025-11-10

### Fixed
- Call to non-existent `loadImgs()` in `CyborgPlatform.java`; corrected to `loadImages()` so the game initialises correctly.
- Incorrect sprite path in `Game` (`Sprites/clod.png` → `Sprites/cloud.png`), preventing image load failures.
- Wrong map filename in `MapBlocks` loader (`Map.txt` → `Maps.txt`), fixing startup `FileNotFoundException`.

### Verification
- Launching via `CyborgPlatform.main(...)` opens a playable game window with map and sprites rendered correctly.
- No runtime exceptions during initialisation.

### Notes
- Screenshot of the running game attached to Task 1 issue.
- Tag created: `v1.0.0`.

---
