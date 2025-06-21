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
