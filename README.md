# Chess Game Project

A Java implementation of a chess game with PGN (Portable Game Notation) support.

There is a PGN support for: Basic pice moves, captures, castling, en passant, check (not checkmates) and promotions

Input can be path to a single pgn file, or folder with multiple pgn files (which are then concatenated as a single pgn file).
Using 4 threads to validate chess games in parallel, handled in Chess/chess.java

run: Main.java "Path_to_pgn"

or from IDE run Main.java and file path will be accepted from terminal using Scanner 


unit tests are in Tests/UnitTests.java, each test has either individual situations, or it is a game leading to a certain situation for example:
1) Rook and King are in castling position, then castling is tested
2) Initial chess setup, leading to en passant by following moves: "1. e4 d5 2. e5 f5 3. exf6ep g5 4. f4 gxf4 1-0";

Program creates two text files:
1) errors.txt, which stores where each invalid game terminated, including last state of board and reason for termination
2) boards.txt, that stores last state of every successful game's boards

Terminal shows which thread is done with validating games, final outputs are number of accepted games, number of valid games and number of invalid games, also id's of each games with their
validation supports.

## Project Structure

### Chess Package
- `Board.java`: Represents the chess board and piece positions
- `ChessGame.java`: Main game logic and state management
- `chess.java`: Entry point for the application, handles threads.

### Pieces Package
- `Piece.java`: Abstract base class for all chess pieces
- `Pawn.java`: Pawn piece implementation
- `Knight.java`: Knight piece implementation
- `Bishop.java`: Bishop piece implementation
- `Rook.java`: Rook piece implementation
- `Queen.java`: Queen piece implementation
- `King.java`: King piece implementation

### GM (Game Master) Package
- `GameMaster.java`: Controls game flow, move validation, and game analysis

### PgnAnalyzers Package
- `PGNReader.java`: Reads and parses PGN files containing chess games

### Exceptions Package
- Contains custom exception class for error handling

## Logging System

### Error Logging
- `ErrorLogger.java`: Handles and logs game errors and invalid moves
- Logs to `errors.txt` with error details such as, moves up until the error, last state of board, and reason for error.

### Error Logging
- `BoardLogger.java`: Handles and logs game errors and invalid moves
- Logs to `boards.txt` with last state of every valid game


## Features
- Chess board representation
- Piece movement validation
- PGN file reading and parsing
- Multi-threaded game analysis
- Support for standard chess rules and moves
