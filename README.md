````md
# Othello (Java / JavaFX)

A complete **Othello (Reversi)** implementation in **Java** with a **JavaFX** graphical interface, engineered with a clear separation between **game rules (domain)** and **UI** using an **MVC architecture**.

---

## Demo (Screenshots / GIF)

> Yes, you can put images in a README.
> Create a folder: `docs/images/` and place your images there, then reference them like this:
> `![alt text](docs/images/<file>.png)`

### Recommended visuals to include
1) **Main game screen** (board + scores + current player)  
2) **Legal moves / suggestions** visible on the board  
3) **AI mode** (show difficulty selection or robot turn)  
4) **End game dialog** (winner/draw + final score + time)  
5) **Architecture diagram** (simple MVC diagram: View → Controller → Model)

### Insert your images here
![Gameplay - main screen](docs/images/gameplay-main.png)
![Gameplay - legal moves highlighted](docs/images/gameplay-legal-moves.png)
![Gameplay - AI mode](docs/images/gameplay-ai.png)
![UI - end game dialog](docs/images/ui-endgame-dialog.png)
![Architecture - MVC diagram](docs/images/architecture-mvc.png)

---

## Features

### Game modes
- **Player vs Player (PVP)**
- **Player vs AI** with **two difficulty levels** (Easy / Hard)

### Gameplay & UX
- **Move validation** (illegal moves rejected)
- **Playable moves suggestion** (highlighting valid positions)
- **Undo**
  - Up to **1 move** in PVP
  - Up to **2 moves** in AI mode
- **Reset** at any time
- **Game timer**
- **End-game dialog** with **score**, **time**, and **winner / draw**

---

## Tech Stack
- Java (JDK **17+** recommended)
- JavaFX
- Gradle (Wrapper included)
- Modular project (`module-info.java`)

---

## Quickstart

### Requirements
```bash
java -version
````

### Run

* Linux/macOS:

```bash
./gradlew run
```

* Windows:

```cmd
./gradlew.bat run
```

### Test

```bash
./gradlew test
```

---

## Project Explanation (A to Z)

### 1) Scope & goals

This project delivers a playable Othello game with a modern UI and quality-of-life features:
multiple modes (PVP / AI), move suggestions, undo, reset, timer, and a complete end-game flow.

### 2) Architecture (MVC)

The codebase is organized around a strict **Model–View–Controller** separation:

* **Model (Domain)**
  Encapsulates the rules of Othello: board state, legal moves, disc flipping, score computation,
  end-game detection, and history management (undo).

* **View (JavaFX UI)**
  Renders the board and the UI elements (scores, turn info, dialogs). The view is passive:
  it displays state and emits events.

* **Controller**
  Orchestrates the interaction between UI and domain:
  translates clicks into domain actions, triggers UI refresh, manages the timer,
  and runs the AI turn when enabled.

> Insert your architecture diagram here (MVC diagram)
> ![Architecture - MVC diagram](docs/images/architecture-mvc.png)

### 3) Domain model (core concepts)

Typical elements in the domain layer:

* Board representation (8×8) and cell states (empty / black / white)
* Turn management (current player)
* Legal move detection (must capture at least one line)
* Disc flipping algorithm (apply captured lines in all directions)
* Game status (running / finished, winner or draw)
* Optional move history snapshots for undo

### 4) Main gameplay flow (from click to updated board)

1. Receive a click on a cell (row, col)
2. Validate move (game running, cell empty, move is legal)
3. Save a snapshot for undo (if enabled)
4. Apply the move:

   * place the piece
   * flip captured discs in all directions
5. Update computed state (scores, next player, status)
6. Refresh UI rendering (board + labels)
7. If AI mode:

   * wait briefly (optional)
   * compute robot move (depending on difficulty)
   * apply + refresh again

### 5) AI behavior (two difficulty levels)

* **Easy**: simple strategy (e.g., random among legal moves, or basic heuristics)
* **Hard**: stronger heuristic (e.g., prefer corners, mobility, disc stability, avoid risky edges)

> Insert a screenshot showing AI mode here
> ![Gameplay - AI mode](docs/images/gameplay-ai.png)

### 6) Testing strategy

Automated tests focus on domain correctness:

* legal/illegal moves
* disc flipping correctness
* score computation
* end-game detection (no moves / full board)
* undo behavior

---

## Repository Structure

* `src/main/java/.../othello/` → Othello domain + controller + dialogs
* `src/main/java/.../board_game_library/...` → board-game UI abstractions & JavaFX implementation
* `src/test/java/.../othello/` → automated tests

---

## Build Artifacts

If you want to publish a runnable `.jar`, prefer **GitHub Releases** rather than committing binaries into the repository.

---

## License

Add a `LICENSE` file (MIT / Apache-2.0 recommended) if you plan to share the project publicly.

```
```
