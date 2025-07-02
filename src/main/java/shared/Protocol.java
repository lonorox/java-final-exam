package shared;

public class Protocol {
    public static final int PORT = 5555;

    // Message types
    public static final String MOVE = "MOVE";
    public static final String GAME_STATE = "GAME_STATE";
    public static final String CHAT = "CHAT";
    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";
    public static final String GAME_OVER = "GAME_OVER";
    public static final String ERROR = "ERROR";
    public static final String PGN_REQUEST = "PGN_REQUEST";
    public static final String PGN_RESPONSE = "PGN_RESPONSE";
    public static final String DISCONNECT = "DISCONNECT";  // Add this line
}