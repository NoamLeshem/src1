import java.util.ArrayList;
import java.util.HashSet;

public class Position
{
    private int x,y;
    private HashSet<ConcretePiece> stepped;
    public Position(int x, int y)
    {
        this.setX(x);
        this.setY(y);
        this.stepped = new HashSet<>();
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + this.getX() + ", " + this.getY() + ")";
    }

    public int getStepped()
    {
        return this.stepped.size();
    }

    public void addUniquePiece(ConcretePiece piece)
    {
        this.stepped.add(piece);
//        System.out.println("(" + this.getX() + ", " + this.getY() + ") : " +stepped);
//        if (this.stepped.isEmpty())
//        {
//            this.stepped.add(piece);
//            return;
//        }
//        for (int i = 0; i < this.stepped.size(); i++) {
//            if (this.stepped.get(i).getName().equals(piece.getName()))
//                continue;
//            this.stepped.add(piece);
//        }
    }
}
