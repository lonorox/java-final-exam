package GM;

public class Castling {
    private boolean kingSide;
    private boolean queenSide;
    private boolean kingMoved;

    Castling(){
        this.kingSide = false;
        this.queenSide = false;
        this.kingMoved = false;
    }

    public boolean getKingSide() {
        return kingSide;
    }
    public void setKingSide(boolean kingSide) {
        this.kingSide = kingSide;
    }
    public boolean getQueenSide() {
        return queenSide;
    }
    public void setQueenSide(boolean queenSide) {
        this.queenSide = queenSide;
    }
    public boolean getKingMoved() {
        return kingMoved;
    }
    public void setKingMoved(boolean kingMoved) {
        this.kingMoved = kingMoved;
    }
}
