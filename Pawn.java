public class Pawn extends ConcretePiece
{
    private int kills;
    public Pawn(Player owner)
    {
        this.setOwner(owner);
        this.setKills(0);
    }

    public void setKills(int kills)
    {
        this.kills = kills;
    }

    public int getKills() {
        return this.kills;
    }

    public void kill()
    {
        this.kills++;
    }

    @Override
    public String getType() {
        return this.getOwner().isPlayerOne() ? "♙" : "♟";
    }
}
