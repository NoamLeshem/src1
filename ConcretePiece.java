import java.util.ArrayList;
import java.util.Objects;

public abstract class ConcretePiece implements Piece
{
    private Player owner;
    private String name;
    private int distance;

    private final ArrayList<Position> moveHistory;

    public ConcretePiece()
    {
        this.moveHistory = new ArrayList<>();
        this.setDistance(0);
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getOwner()
    {
        return this.owner;
    }

    public abstract String getType();

    public ArrayList<Position> getMoveHistory() {
        return this.moveHistory;
    }

    public void addMove(Position curr)
    {
        this.moveHistory.add(curr);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcretePiece piece = (ConcretePiece) o;
        return Objects.equals(owner, piece.owner) && Objects.equals(name, piece.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, name);
    }
}
