# Cyborg Platformer Game

**Author:** Jacob Rush / psyjr14  
**Course:** COMP2013 Coursework 2025


A simple 2D zombie shooting platformer used for coursework. This README tells you how to build/run, how to play and where to find documentation.

---
## Quick Start

### Requirements
- **Java:** JDK 17 (or the version your brief specifies)
- **IDE:** IntelliJ IDEA (Community or Ultimate)
- **Build tool:** <Gradle/Maven/None> (use what your project actually uses)

### Build & Run

**Option A — IntelliJ**
1. Open the project folder in IntelliJ.
2. Create/run a configuration that points to the `main` method in `CyborgPlatform` (package: `<your.package>`).
3. Click **Run**.

**Option B — Gradle**
```bash
./gradlew run
```
**Option C - Windows**
```bash
.\gradlew.bat run
```

## How to Play

Move: WASD

Jump: W (you can double jump)

Shoot: SPACE

Restart Level: ESC

Quit: ENTER

Goal: Reach the end of the level (the “Winner Tunnel”) while avoiding hazards and zombies. Shoot enemies, survive, and make it to the exit.

[Changelog](docs/Changelog.md)  
[Software Design](docs/SoftwareDesign.md)
## Gameplay Preview

![Gameplay GIF](gifs/StartGif.gif)
![Gameplay GIF2](gifs/EnemyExampleGif.gif)
