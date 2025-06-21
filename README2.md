# Chess GUI Documentation

## Overview
The Chess GUI application provides a user-friendly interface for managing and reviewing chess games. It supports importing PGN files, playing games, and reviewing games from a database.

## Main Components

### ChessGUI
- Main entry point for the GUI application
- Provides three main buttons:
  - Import PGN: Import chess games from PGN files
  - Play Game: Start a new chess game (to be implemented)
  - Open Game from DB: Access and review games stored in the database

### ChessReplayPanel
- Handles the step-by-step replay of chess games
- Features:
  - Visual chessboard display with piece images
  - Player names display
  - Move navigation controls
  - Game status display
  - Winner announcement at game end

## Functionality Details

### PGN Import
1. File Selection
   - Opens a file chooser dialog
   - Supports .pgn file format
   - Validates file content

2. Import Options
   - Import ALL games to database
   - Select specific game to import
   - Review selected game
   - Cancel import

### Database Operations
1. Game Loading
   - Retrieves games from the database
   - Displays game list with player names and event information
   - Supports game selection for review

2. Game Review
   - Validates game before review
   - Displays error message for invalid games
   - Shows step-by-step replay interface

### Game Review Interface
1. Board Display
   - 8x8 chessboard with alternating colors
   - Piece images loaded from resources
   - Centered layout

2. Controls
   - Next button for move progression
   - Close button to exit review
   - Status display showing current move and player

3. Game Information
   - Player names display
   - Move counter
   - Winner announcement at game end

## File Structure
```
src/
├── main/
│   ├── java/
│   │   └── Chess/
│   │       ├── ChessGUI.java
│   │       └── ChessReplayPanel.java
│   └── resources/
│       └── images/
│           └── [chess piece images]
```

## Future Improvements
1. Split ChessGUI into smaller, more focused components
2. Add game playing functionality
3. Implement move validation during replay
4. Add support for game annotations
5. Enhance visual design and user experience 