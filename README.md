````md
# Othello (Java / JavaFX)

A complete **Othello (Reversi)** implementation in **Java** with a **JavaFX** desktop UI. The project is engineered with a clear separation between **domain rules** and **presentation/UI** following an **MVC architecture**, and includes **PVP** and **AI** modes (Easy/Hard).

---

## Screenshots & Diagrams

Create a folder `docs/images/` and add your images using the filenames below.

### Gameplay (UI)
Save your UI screenshot as: `docs/images/ui-gameplay.png`
![Othello UI - Gameplay](docs/images/ui-gameplay.png)

### Use Case Diagram
Save your use-case diagram as: `docs/images/use-cases.png`
![Use Case Diagram](docs/images/use-cases.png)

### Component / Architecture Diagram
Save your component diagram as: `docs/images/components.png`
![Component Diagram](docs/images/components.png)

---

## Features

- **Game modes**
  - **Player vs Player (PVP)**
  - **Player vs AI** with **two difficulty levels** (Easy / Hard)

- **Gameplay**
  - **Legal move validation** (illegal moves rejected)
  - **Playable moves suggestion** (highlight valid positions)
  - **Disc flipping** in all directions (core Othello rule)
  - **Score tracking** (Black/White)
  - **Game timer**
  - **End-game dialog** with final score and **winner/draw**

- **Quality-of-life**
  - **Undo**
    - Up to **1 move** in PVP
    - Up to **2 moves** in AI mode
  - **Reset** at any time

---

## Tech Stack

- Java (JDK **17+** recommended)
- JavaFX
- Gradle (Wrapper included)
- Modular project (`module-info.java`)

---

## Quickstart

### Requirements
Verify your Java installation:
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

### 1) Scope and objectives

The objective is to deliver a fully playable Othello game with a modern desktop UI, multiple modes (PVP / AI), and user-oriented features (suggestions, undo, reset, timer, end-game flow).

### 2) Supported use cases

* Start a new game
* Choose a mode: **PVP**, **Easy AI**, or **Hard AI**
* Play by selecting a cell on the board
* Visualize **valid moves** through suggestions
* Undo moves (within the allowed limits per mode)
* Reset the game at any time
* Finish the game and display final results (score + winner/draw)

### 3) Architecture (MVC)

The implementation follows a strict separation of responsibilities:

* **Model (Domain)**

  * Board representation (8×8) and cell states
  * Legal move detection (a move is legal only if it captures at least one line)
  * Disc flipping algorithm (captures applied across all relevant directions)
  * Score computation and end-game detection
  * Move snapshot/history mechanism to support undo

* **View (JavaFX UI)**

  * Renders the board, pieces, and user interface elements (buttons, labels)
  * Displays current player, score, timer, and valid move suggestions
  * Shows end-game results via a dialog

* **Controller**

  * Translates UI events (board clicks and button actions) into domain operations
  * Orchestrates state transitions and UI refresh
  * Manages timer updates and AI turn execution (when enabled)

### 4) Main gameplay flow

1. The user clicks a cell on the board.
2. The controller validates the action (game running, cell available).
3. The domain validates legality (must capture at least one direction).
4. If legal:

   * A snapshot is stored (undo support).
   * The move is applied and discs are flipped.
   * Scores and current player are updated.
5. The view is refreshed (board + labels + suggestions).
6. In AI mode, the controller triggers the robot move based on the selected difficulty.

### 5) AI modes

* **Easy AI**: baseline behavior for accessible gameplay.
* **Hard AI**: stronger heuristic choices for more competitive play.

### 6) Testing strategy

Automated tests focus on domain correctness:

* legal/illegal move validation
* disc flipping correctness
* score computation
* end-game detection
* undo behavior

Run the test suite with:

```bash
./gradlew test
```

---

## Repository Structure

* `src/main/java/.../othello/`
  Domain + controller + UI dialogs
* `src/main/java/.../board_game_library/...`
  UI abstractions and JavaFX implementation
* `src/test/java/.../othello/`
  Automated tests


