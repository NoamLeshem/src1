public class King extends ConcretePiece
{
    public King(Player defender)
    {
        this.setOwner(defender);
    }

    @Override
    public String getType()
    {
        return "♔";
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
