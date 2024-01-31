import java.util.ArrayList;

public class ConcretePlayer implements Player
{
    private ArrayList<ConcretePiece> pieces;
    private boolean defender;
    private int wins;
    public ConcretePlayer(boolean defender)
    {
        this.pieces = new ArrayList<>();
        this.setDefender(defender);
        this.setWins(0);
    }

    public void setWins(int wins)
    {
        this.wins = wins;
    }

    private void setDefender(boolean defender)
    {
        this.defender = defender;
    }

    @Override
    public boolean isPlayerOne()
    {
        return this.defender;
    }

    @Override
    public int getWins()
    {
        return this.wins;
    }

    public ArrayList<ConcretePiece> getPieces()
    {
        return this.pieces;
    }

    public void addPiece(ConcretePiece piece)
    {
        this.pieces.add(piece);
    }
}
