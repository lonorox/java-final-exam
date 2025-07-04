# Java Chess Application

A full-featured Java chess application supporting both local and online play, with robust game analysis, persistence, and multiplayer capabilities.

---
# How to Run the Java Chess Application
## Prerequisites
- **SQLite JDBC driver** should be present in the `lib/` directory.
---
## Using Maven (Recommended)
1. Open a terminal in your project root.
2. Compile the project:
   ```sh
   mvn clean compile
   ```
3. Run the application:
   ```sh
   mvn exec:java -Dexec.mainClass="Main"
   ```

---

## Without Maven (Manual Compilation)
1. Compile all Java files:
   ```sh
   javac -cp lib/sqlite-jdbc.jar -d out $(find src/main/java -name "*.java")
   ```
2. Run the application:
   ```sh
   java -cp lib/sqlite-jdbc.jar:out Main
   ```
   > On Windows, use `;` instead of `:` in the classpath:
   > ```
   > java -cp lib/sqlite-jdbc.jar;out Main
   > ```

---

## Using an IDE (IntelliJ IDEA, Eclipse, etc.)
1. Import the project (as a Maven project or standard Java project).
2. Make sure `lib/sqlite-jdbc.jar` is included as a library/dependency.
3. Set the main class to `Main` and run the project.

--- 
---


## Features

- **Complete Chess Rules:**
  - All standard chess rules implemented, including special moves (castling, promotion), check/checkmate, and draw conditions. **Note:** En passant is not fully implemented or may not work correctly.
- **Modern GUI:**
  - Interactive chessboard, move highlighting, turn/status display, move history, and responsive design.
- **Game Analysis:**
  - Move validation with detailed error messages, game review with move-by-move replay, and PGN import validation.
- **Persistence:**
  - SQLite database for saving/loading games, move history, and metadata (players, results, dates).
- **PGN Support:**
  - Full import/export of games in Portable Game Notation (PGN) format.
- **Multiplayer:**
  - Online play via client-server architecture, with real-time move synchronization and chat.
- **Computer Opponent:**
  - Basic AI with random legal move selection and configurable difficulty.
- **Time Controls:**
  - Configurable clocks, per-player time management, and timeout detection.

---

## Technical Details

### Architecture
- **Separation of Concerns:** Game logic, data, and presentation are clearly separated.
- **Event-Driven:** User interactions trigger appropriate controller actions.

### Data Flow
- User interacts with View components
- View notifies Controller of actions
- Controller updates Model state
- Model notifies View of changes
- View updates display accordingly

### File Organization
- **Database:** Centralized data management (`server/Database/`)
- **GM:** Game analysis and validation logic (`server/GM/`)
- **Model:** Maintains piece positions and board state (`client/NewGUI/Model/`)
- **Controller:** Mediates between model and view (`client/NewGUI/Controller/`)
- **View:** Swing-based UI components (`client/NewGUI/View/`)
- **PGN Handling:** Full import/export and parsing (`shared/PgnAnalyzers/`)
- **Legacy:** Previous implementations and compatibility (`shared/LegacyCore/`, `LegacyGUI/`)

---

## Core Implementation

### Chess Engine Game State Management
- `GameState` class tracks board positions, turn order, and game status.
- Implements all chess rules including:
  - Basic piece movements
  - Special moves (castling, promotion; **en passant not fully implemented**)
  - Check/checkmate detection
  - Draw conditions (stalemate, insufficient material)

### Move Validation
- Each piece type (Pawn, Knight, etc.) implements its own movement logic.
- `ChessController` validates moves against game rules.
- Legal move highlighting in the UI.

### PGN Support
- Full Portable Game Notation import/export.
- Move logging in PGN format.
- Game metadata (player names, result, date) storage.

### User Interface GUI Components
- `GameWindow`: Main application frame
- `ChessBoardUI`: Interactive chess board with piece rendering
- `SquareView`: Individual board square implementation
- `StartMenu`: Game configuration dialog

### Visual Features
- Piece highlighting for selection
- Legal move indicators
- Turn and game status display
- Move history panel

### Game Flow
- Configurable game modes (human/computer players)
- Adjustable time controls
- Game over detection and result display

### Database System Storage
- SQLite database for game persistence
- Tables for games and individual moves
- Metadata storage (players, results, dates)
- Operations:
  - Save/load complete games
  - Query game history
  - Import/export to PGN format
  - Game review functionality
- Implementation:
  - `DatabaseManager` handles all DB operations
  - JDBC for database connectivity
  - Transaction support for data integrity

### Network Architecture
- Client-server model
- TCP sockets for communication
- Object serialization for message passing
- Protocol:
  - Custom message format (`Message` class)
  - Defined operations (move, chat, etc.)
  - Game state synchronization
- Features:
  - Player matching system
  - Move validation on server
  - Real-time chat
  - Game result recording

### Other Features
- **Game Analysis:**
  - Move validation with detailed error messages
  - Game review system with move-by-move replay
  - PGN import validation
- **Computer Opponent:**
  - Random legal move selection
  
### Technical Highlights
- **Concurrency:**
  - Multithreaded server
  - Background game clock
  - Asynchronous move processing
- **Error Handling:**
  - Comprehensive validation
  - Custom exceptions
  - Error logging system

---

## Project Structure Deep Dive

### Model Layer
- `Board`: Maintains piece positions and board state
- `Piece`: Abstract base class for all chess pieces
- `Position`: Represents board coordinates
- `Move`: Encapsulates move information with Builder pattern

### Controller Layer
- `ChessController`: Mediates between model and view
- `GameMaster`: Handles move validation and game rules
- `PGNReader`: Parses PGN files into game data

### View Layer
- Swing-based UI components
- Custom rendering for chess pieces
- Responsive design for different window sizes

---

## Usage

### Starting the Application

1. **Run `Main.java`**
   - The main GUI appears with options:
     - **Import PGN:** Import chess games from PGN files
     - **Export to PGN:** Export player games from the database into a PGN file
     - **Play Game:** Start a new game with the modern GUI
     - **Open Game from DB:** Browse and replay games from database
     - **Quit:** Exit the application

### Importing PGN Files

- Click "Import PGN"
- Select a PGN file
- Use the **Select** button to import a specific game from the PGN file, or use **Import All to database** to import all games.
- Games are imported to the database for later review

### Reviewing Games

- Click "Open Game from DB"
- Select a game from the database
- Use replay controls to step through moves

---

## How to Build & Run

1. **Dependencies:**
   - Java 17+
   - SQLite JDBC driver (included in `lib/`)
2. **Compile:**
   - Use your preferred IDE or run:
     ```sh
     javac -cp lib/sqlite-jdbc.jar src/main/java/**/*.java
     ```
3. **Run:**
   - From your IDE, run `Main.java`, or use:
     ```sh
     java -cp lib/sqlite-jdbc.jar:src/main/java Main
     ```

---

## Development Challenges

- Implementing all chess rules correctly
- Handling special cases like en passant
- Check detection without infinite recursion
- Maintaining consistent game state in network play
- Handling disconnections
- Turn management between players
- Efficient board representation
- Minimizing UI repaints
- Database query optimization

---

## Credits

- Developed by [Your Name/Team]
- Uses open-source libraries for SQLite and Java Swing

---

## License

[Specify your license here]

