This Java chess application provides a complete chess experience with both local and online gameplay options. The system implements all standard chess rules while offering additional features for game analysis, persistence, and multiplayer functionality.

## Java Features Used
**Lambdas:**
- File filtering using lambda expressions: PgnAnalyzers/FileHandler.java
- Enhanced switch expressions: GM/moveValidators.java

**Streams:**
- Functional-style file operations and filtering: PgnAnalyzers/FileHandler.java
- Data transformation and processing: Database/DatabaseManager.java

**Records:**
- Immutable game data structure: LegacyCore/ChessGame.java

**Threads & Concurrency:**
- Although not used in the current version, planned or potential uses include:
- Client-server communication handling
- Background game clock implementation
- Computer move calculation
- Asynchronous database operations

**File I/O:**
- PGN file reading/writing: PgnAnalyzers/FileHandler.java, PgnAnalyzers/PGNReader.java
- Game state serialization
- Database export/import operations

**Network I/O:**
- Socket communication for multiplayer
- Object serialization over network
- Custom message protocol implementation

**Database:**
- SQLite integration and database logic: Database/DatabaseManager.java
- Game storage and retrieval: Database/GameRecorder.java, Database/GameReplayer.java
- PGN utility functions: Database/PgnDatabaseUtility.java


### Starting the Application
1. Run `Main.java`
2. The main GUI appears with options:
   - **Import PGN**: Import chess games from PGN files
   - **Play Game**: Start a new game with the modern GUI
   - **Open Game from DB**: Browse and replay games from database
   - **Quit**: Exit the application

### Importing PGN Files
1. Click "Import PGN"
2. Select a PGN file
3. Games are imported to database for later review

### Reviewing Games
1. Click "Open Game from DB"
2. Select a game from the database
3. Use replay controls to step through moves

## Technical Details

### Architecture
- **Separation of Concerns**: Game logic, data, and presentation are clearly separated
- **Event-Driven**: User interactions trigger appropriate controller actions
- **Modular Design**: Components can be easily extended or modified

### Data Flow
1. User interacts with View components
2. View notifies Controller of actions
3. Controller updates Model state
4. Model notifies View of changes
5. View updates display accordingly

### File Organization
- **Database**: Centralized data management
- **GM**: Game analysis and validation logic


## core implementation ##
**Chess Engine**
Game State Management:
- GameState class tracks board positions, turn order, and game status.
- Implements all chess rules including:
  • Basic piece movements
  • Special moves (castling, en passant, promotion)
  • Check/checkmate detection
  • Draw conditions (stalemate, insufficient material)

Move Validation:
- Each piece type (Pawn, Knight, etc.) implements its own movement logic.
- ChessController validates moves against game rules.
- Legal move highlighting in the UI.

PGN Support:
- Full Portable Game Notation import/export.
- Move logging in PGN format.
- Game metadata (player names, result, date) storage.

**User Interface**
GUI Components:
- GameWindow: Main application frame
- ChessBoardUI: Interactive chess board with piece rendering
- SquareView: Individual board square implementation
- StartMenu: Game configuration dialog

Visual Features:
- Piece highlighting for selection
- Legal move indicators
- Turn and game status display
- Move history panel

Game Flow:
- Configurable game modes (human/computer players)
- Adjustable time controls
- Game over detection and result display

**Database System**
Storage:
- SQLite database for game persistence
- Tables for games and individual moves
- Metadata storage (players, results, dates)

Operations:
- Save/load complete games
- Query game history
- Import/export to PGN format
- Game review functionality

Implementation:
- DatabaseManager handles all DB operations
- JDBC for database connectivity
- Transaction support for data integrity

**Network**
Architecture:
- Client-server model
- TCP sockets for communication
- Object serialization for message passing

Protocol:
- Custom message format (Message class)
- Defined operations (move, chat, etc.)
- Game state synchronization

Features:
- Player matching system
- Move validation on server
- Real-time chat
- Game result recording

**Other Features**
Game Analysis:
- Move validation with detailed error messages
- Game review system with move-by-move replay
- PGN import validation

Computer Opponent:
- Basic AI implementation
- Random legal move selection
- Configurable difficulty levels

Time Controls:
- Configurable game clocks
- Time management per player
- Timeout detection

**Technical Highlights**
Design Patterns:
- MVC architecture separation
- Observer pattern for UI updates
- Factory pattern for piece creation
- Builder pattern for complex moves

Concurrency:
- Multithreaded server
- Background game clock
- Asynchronous move processing

Error Handling:
- Comprehensive validation
- Custom exceptions
- Error logging system

**Project Structure Deep Dive**
Model Layer:
- Board: Maintains piece positions and board state
- Piece: Abstract base class for all chess pieces
- Position: Represents board coordinates
- Move: Encapsulates move information with Builder pattern

Controller Layer:
- ChessController: Mediates between model and view
- GameMaster: Handles move validation and game rules
- PGNReader: Parses PGN files into game data

View Layer:
- Swing-based UI components
- Custom rendering for chess pieces
- Responsive design for different window sizes

**Development Challenges**
Move Validation:
- Implementing all chess rules correctly
- Handling special cases like en passant
- Check detection without infinite recursion

Network Synchronization:
- Maintaining consistent game state
- Handling disconnections
- Turn management between players

Performance:
- Efficient board representation
- Minimizing UI repaints
- Database query optimization

