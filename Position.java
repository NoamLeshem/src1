import java.util.ArrayList;

public class Position
{
    private int x,y;
    private int stepped;
    public Position(int x, int y)
    {
        this.setX(x);
        this.setY(y);
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

    public int getStepped() {
        return this.stepped;
    }

    public void setStepped(int stepped) {
        this.stepped = stepped;
    }
}
