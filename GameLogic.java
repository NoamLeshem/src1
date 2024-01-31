import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GameLogic implements PlayableLogic
{
    private final int BOARD_SIZE = 11;
    private final ConcretePlayer attacker = new ConcretePlayer(false);
    private final ConcretePlayer defender = new ConcretePlayer(true);
    private final ArrayList<ConcretePiece> pieces = new ArrayList<>();
    private boolean finished;
    private boolean attackerTurn;
    private ConcretePiece[][] board;

    private  Position[][] positions;

    public GameLogic()
    {
        this.reset();
    }

    @Override
    public boolean move(Position a, Position b)
    {
        if (!moveValid(a,b))
            return false;

        this.board[b.getX()][b.getY()] = (ConcretePiece) getPieceAtPosition(a);
        this.board[a.getX()][a.getY()] = null;

        ConcretePiece piece = (ConcretePiece) getPieceAtPosition(b);
        piece.addMove(b);
        piece.setDistance(piece.getDistance() + Math.abs((b.getX() - a.getX()) + Math.abs(b.getY() - a.getY())));

        positions[b.getX()][b.getY()].setStepped(positions[b.getX()][b.getY()].getStepped() + 1);

        capture(b);
        checkmate();

        if (isGameFinished())
            printStats(attackerTurn);

        this.setAttackerTurn(!this.isSecondPlayerTurn());
        return true;
    }

    private void printStats(boolean attackerTurn)
    {
        this.attacker.getPieces().sort(Comparator.comparingInt(piece -> piece.getMoveHistory().size()));
        this.defender.getPieces().sort(Comparator.comparingInt(piece -> piece.getMoveHistory().size()));
        if (attackerTurn)
        {
            // print attacker first then print defender
            for (ConcretePiece piece : this.attacker.getPieces())
            {
                if (piece.getMoveHistory().size() > 1)
                    System.out.println(piece.getName() + ": " + piece.getMoveHistory().toString());
            }
            // print defender first then print attacker
            for (ConcretePiece piece :  this.defender.getPieces())
            {
                if(piece.getMoveHistory().size() > 1)
                    System.out.println(piece.getName() + ": " + piece.getMoveHistory().toString());
            }
        }
        else
        {
            // print defender first then print attacker
            for (ConcretePiece piece :  this.defender.getPieces())
            {
                if (piece.getMoveHistory().size() > 1)
                    System.out.println(piece.getName() + ": " + piece.getMoveHistory().toString());
            }
            // print attacker first then print defender
            for (ConcretePiece piece : this.attacker.getPieces())
            {
                if (piece.getMoveHistory().size() > 1)
                    System.out.println(piece.getName() + ": " + piece.getMoveHistory().toString());
            }
        }
        System.out.println("*".repeat(75));
        // part 2
        this.pieces.sort((piece1, piece2) ->
        {
            if (piece1 instanceof King)
                return -Integer.compare(0, ((Pawn) piece2).getKills());
            else if (piece2 instanceof King)
                return -Integer.compare(((Pawn) piece1).getKills(), 0);
            if (((Pawn) piece1).getKills() == ((Pawn) piece2).getKills() &&
                    Integer.parseInt(piece1.getName().substring(1)) == Integer.parseInt(piece2.getName().substring(1)))
                return piece1.getOwner().isPlayerOne() != attackerTurn ? -1 : 1;
            return -Integer.compare(((Pawn) piece1).getKills(), ((Pawn) piece2).getKills());
        });
        for (ConcretePiece piece : this.pieces)
        {
            if (piece instanceof King)
                continue;
            if (((Pawn) piece).getKills() == 0)
                continue;
            System.out.println(piece.getName() + ": " + ((Pawn) piece).getKills() + " kills");
        }
        System.out.println("*".repeat(75));
        // part 3
        this.pieces.sort((piece1, piece2) ->
        {
            if (piece1.getDistance() == piece2.getDistance() &&
                    Integer.parseInt(piece1.getName().substring(1)) == Integer.parseInt(piece2.getName().substring(1)))
                return piece1.getOwner().isPlayerOne() != attackerTurn ? -1 : 1;
            return -Integer.compare(piece1.getDistance(), piece2.getDistance());
        });
        // TODO: FIXME!!!!
        for (ConcretePiece piece : this.pieces)
        {
            if (piece.getDistance() == 0)
                continue;
            System.out.println(piece.getName() + ": " + piece.getDistance() + " distance");
        }
        System.out.println("*".repeat(75));
        // part 4
        ArrayList <Position> temp = new ArrayList<>();
        for (int i = 0; i < this.getBoardSize(); i++)
            temp.addAll(Arrays.asList(positions[i]).subList(0, this.getBoardSize()));
        temp.sort(Comparator.comparingInt(Position::getStepped));
        for (Position pos : temp)
        {
            if (pos.getStepped() <= 1)
                continue;
            System.out.println(pos + " " + pos.getStepped() + " pieces");
        }
        System.out.println("*".repeat(75));
    }

    private boolean isKingCaptured()
    {
        //find king
        int kingX = -1;
        int kingY = -1;
        for (int i = 0; i < getBoardSize(); i++)
            for (int j = 0; j < getBoardSize(); j++)
                if (board[i][j] instanceof King)
                {
                    kingY = j;
                    kingX = i;
                    break;
                }

        if(kingX == 0)
            if(board[kingX + 1][kingY] instanceof Pawn && !board[kingX + 1][kingY].getOwner().isPlayerOne() &&
                    board[kingX][kingY + 1] instanceof Pawn && !board[kingX][kingY + 1].getOwner().isPlayerOne() &&
                    board[kingX][kingY - 1] instanceof Pawn && !board[kingX][kingY - 1].getOwner().isPlayerOne())
                return true;
        else if(kingX == this.getBoardSize() - 1)
            if(board[kingX - 1][kingY] instanceof Pawn && !board[kingX - 1][kingY].getOwner().isPlayerOne() &&
                    board[kingX][kingY + 1] instanceof Pawn && !board[kingX][kingY + 1].getOwner().isPlayerOne() &&
                    board[kingX][kingY - 1] instanceof Pawn && !board[kingX][kingY - 1].getOwner().isPlayerOne())
                return true;
        if(kingY == 0)
            if(board[kingX][kingY + 1] instanceof Pawn && !board[kingX][kingY + 1].getOwner().isPlayerOne() &&
                    board[kingX + 1][kingY] instanceof Pawn && !board[kingX + 1][kingY].getOwner().isPlayerOne() &&
                    board[kingX - 1][kingY] instanceof Pawn && !board[kingX - 1][kingY].getOwner().isPlayerOne())
                return true;
        if(kingY == this.getBoardSize() - 1)
            if(board[kingX][kingY - 1] instanceof Pawn && !board[kingX][kingY - 1].getOwner().isPlayerOne() &&
                    board[kingX + 1][kingY] instanceof Pawn && !board[kingX + 1][kingY].getOwner().isPlayerOne() &&
                    board[kingX - 1][kingY] instanceof Pawn && !board[kingX - 1][kingY].getOwner().isPlayerOne())
                return true;
        if(kingX + 1 < this.getBoardSize() && kingX - 1 > 0 && kingY + 1 < this.getBoardSize() &&  kingY - 1 > 0 &&
                board[kingX + 1][kingY] instanceof Pawn && !board[kingX + 1][kingY].getOwner().isPlayerOne() &&
                board[kingX - 1][kingY] instanceof Pawn && !board[kingX - 1][kingY].getOwner().isPlayerOne() &&
                board[kingX][kingY + 1] instanceof Pawn && !board[kingX][kingY + 1].getOwner().isPlayerOne() &&
                board[kingX][kingY - 1] instanceof Pawn && !board[kingX][kingY - 1].getOwner().isPlayerOne())
            return true;
        return false;
    }

    private void capture(Position d)
    {
        System.out.println("x: " + d.getX() + " y: " + d.getY());
        if (getPieceAtPosition(d) instanceof King)
            return;
        if (getPieceAtPosition(d).getOwner().isPlayerOne())
        {
            // corners
            if (d.getX() == 0) {
                if (this.board[d.getX()][1] != null &&
                        d.getY() == 2 &&
                        !this.board[d.getX()][1].getOwner().isPlayerOne() &&
                        this.board[d.getX()][1] instanceof Pawn)
                {
                    this.board[d.getX()][1] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
                if (this.board[d.getX()][this.getBoardSize() - 2] != null &&
                        d.getY() == this.getBoardSize() - 3 &&
                        !this.board[0][this.getBoardSize() - 2].getOwner().isPlayerOne() &&
                        this.board[0][this.getBoardSize() - 2] instanceof Pawn)
                {
                    this.board[d.getX()][this.getBoardSize() - 2] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
            }

            if (d.getX() == this.getBoardSize() - 1)
            {
                if (this.board[d.getX()][1] != null &&
                        d.getY() == 2 &&
                        !this.board[d.getX()][1].getOwner().isPlayerOne() &&
                        this.board[d.getX()][1] instanceof Pawn)
                {
                    this.board[d.getX()][1] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
                if (this.board[d.getX()][this.getBoardSize() - 2] != null &&
                        d.getY() == this.getBoardSize() - 3 &&
                        !this.board[d.getX()][this.getBoardSize() - 2].getOwner().isPlayerOne() &&
                        this.board[d.getX()][this.getBoardSize() - 2] instanceof Pawn)
                {
                    this.board[d.getX()][this.getBoardSize() - 2] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
            }
            if (d.getY() == 0)
            {
                if (this.board[1][d.getY()] != null &&
                        d.getX() == 2 &&
                        !this.board[1][d.getY()].getOwner().isPlayerOne() &&
                        this.board[1][d.getY()] instanceof Pawn)
                {
                    this.board[1][d.getY()] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
                if (this.board[this.getBoardSize() - 2][d.getY()] != null &&
                        d.getX() == this.getBoardSize() - 3 &&
                        !this.board[this.getBoardSize() - 2][d.getY()].getOwner().isPlayerOne() &&
                        this.board[this.getBoardSize() - 2][d.getY()] instanceof Pawn)
                {
                    this.board[this.getBoardSize() - 2][d.getY()] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
            }
            if (d.getY() == this.getBoardSize() - 1)
            {
                if (this.board[1][d.getY()] != null &&
                        d.getX() == 2 &&
                        !this.board[1][d.getY()].getOwner().isPlayerOne() &&
                        this.board[1][d.getY()] instanceof Pawn)
                {
                    this.board[1][d.getY()] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
                if (this.board[this.getBoardSize() - 2][d.getY()] != null &&
                        d.getX() == this.getBoardSize() - 3 &&
                        !this.board[this.getBoardSize() - 2][d.getY()].getOwner().isPlayerOne() &&
                        this.board[this.getBoardSize() - 2][d.getY()] instanceof Pawn)
                {
                    this.board[this.getBoardSize() - 2][d.getY()] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
            }

            // edges
            if (this.board[0][d.getY()] != null &&
                    d.getX() == 1 &&
                    !this.board[0][d.getY()].getOwner().isPlayerOne() &&
                    this.board[0][d.getY()] instanceof Pawn)
            {
                this.board[0][d.getY()] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
            if (this.board[this.getBoardSize() - 1][d.getY()] != null &&
                    d.getX() == this.getBoardSize() - 2 &&
                    !this.board[this.getBoardSize() - 1][d.getY()].getOwner().isPlayerOne() &&
                    this.board[this.getBoardSize() - 1][d.getY()] instanceof Pawn)
            {
                this.board[this.getBoardSize() - 1][d.getY()] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
            if (this.board[d.getX()][0] != null &&
                    d.getY() == 1 &&
                    !this.board[d.getX()][0].getOwner().isPlayerOne() &&
                    this.board[d.getX()][0] instanceof Pawn)
            {
                this.board[d.getX()][0] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
            if (this.board[d.getX()][this.getBoardSize() - 1] != null &&
                    d.getY() == this.getBoardSize() - 2 &&
                    !this.board[d.getX()][this.getBoardSize() - 1].getOwner().isPlayerOne() &&
                    this.board[d.getX()][this.getBoardSize() - 1] instanceof Pawn)
            {
                this.board[d.getX()][this.getBoardSize() - 1] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }

            // all the rest
            if (d.getX() + 2 < this.getBoardSize() &&
                    this.board[d.getX() + 1][d.getY()] != null &&
                    this.board[d.getX() + 2][d.getY()] != null &&
                    !this.board[d.getX() + 1][d.getY()].getOwner().isPlayerOne() &&
                    this.board[d.getX() + 2][d.getY()].getOwner().isPlayerOne() &&
                    this.board[d.getX() + 1][d.getY()] instanceof Pawn &&
                    this.board[d.getX() + 2][d.getY()] instanceof Pawn)
            {
                this.board[d.getX() + 1][d.getY()] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
            if (d.getX() - 2 >= 0 &&
                    this.board[d.getX() - 1][d.getY()] != null &&
                    this.board[d.getX() - 2][d.getY()] != null &&
                    !this.board[d.getX() - 1][d.getY()].getOwner().isPlayerOne() &&
                    this.board[d.getX() - 2][d.getY()].getOwner().isPlayerOne() &&
                    this.board[d.getX() - 1][d.getY()] instanceof Pawn &&
                    this.board[d.getX() - 2][d.getY()] instanceof Pawn)
            {
                this.board[d.getX() - 1][d.getY()] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
            if (d.getY() + 2 < this.getBoardSize() &&
                    this.board[d.getX()][d.getY() + 1] != null &&
                    this.board[d.getX()][d.getY() + 2] != null &&
                    !this.board[d.getX()][d.getY() + 1].getOwner().isPlayerOne() &&
                    this.board[d.getX()][d.getY() + 2].getOwner().isPlayerOne() &&
                    this.board[d.getX()][d.getY() + 1] instanceof Pawn &&
                    this.board[d.getX()][d.getY() + 2] instanceof Pawn)
            {
                this.board[d.getX()][d.getY() + 1] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
            if (d.getY() - 2 >= 0 &&
                    this.board[d.getX()][d.getY() - 1] != null &&
                    this.board[d.getX()][d.getY() - 2] != null &&
                    !this.board[d.getX()][d.getY() - 1].getOwner().isPlayerOne() &&
                    this.board[d.getX()][d.getY() - 2].getOwner().isPlayerOne() &&
                    this.board[d.getX()][d.getY() - 1] instanceof Pawn &&
                    this.board[d.getX()][d.getY() - 2] instanceof Pawn)
            {
                this.board[d.getX()][d.getY() - 1] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
        }
        else
        {
            // corners
            if (d.getX() == 0)
            {
                if (this.board[d.getX()][1] != null &&
                        d.getY() == 2 &&
                        this.board[d.getX()][1].getOwner().isPlayerOne() &&
                        this.board[d.getX()][1] instanceof Pawn)
                {
                    this.board[d.getX()][1] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
                if (this.board[d.getX()][this.getBoardSize() - 2] != null &&
                        d.getY() == this.getBoardSize() - 3 &&
                        this.board[d.getX()][this.getBoardSize() - 2].getOwner().isPlayerOne() &&
                        this.board[d.getX()][this.getBoardSize() - 2] instanceof Pawn)
                {
                    this.board[d.getX()][this.getBoardSize() - 2] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
            }
            if (d.getX() == this.getBoardSize() - 1)
            {
                if (this.board[d.getX()][1] != null &&
                        d.getY() == 2 &&
                        this.board[d.getX()][1].getOwner().isPlayerOne() &&
                        this.board[d.getX()][1] instanceof Pawn)
                {
                    this.board[d.getX()][1] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
                if (this.board[d.getX()][this.getBoardSize() - 2] != null &&
                        d.getY() == this.getBoardSize() - 3 &&
                        this.board[d.getX()][this.getBoardSize() - 2].getOwner().isPlayerOne() &&
                        this.board[d.getX()][this.getBoardSize() - 2] instanceof Pawn)
                {
                    this.board[d.getX()][this.getBoardSize() - 2] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
            }
            if (d.getY() == 0)
            {
                if (this.board[1][d.getY()] != null &&
                        d.getX() == 2 &&
                        this.board[1][d.getY()].getOwner().isPlayerOne() &&
                        this.board[1][d.getY()] instanceof Pawn)
                {
                    this.board[1][d.getY()] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
                if (this.board[this.getBoardSize() - 2][d.getY()] != null &&
                        d.getX() == this.getBoardSize() - 3 &&
                        this.board[this.getBoardSize() - 2][d.getY()].getOwner().isPlayerOne() &&
                        this.board[this.getBoardSize() - 2][d.getY()] instanceof Pawn)
                {
                    this.board[this.getBoardSize() - 2][d.getY()] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
            }
            if (d.getY() == this.getBoardSize() - 1)
            {
                if (this.board[1][d.getY()] != null &&
                        d.getX() == 2 &&
                        this.board[1][d.getY()].getOwner().isPlayerOne() &&
                        this.board[1][d.getY()] instanceof Pawn)
                {
                    this.board[1][d.getY()] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
                if (this.board[this.getBoardSize() - 2][d.getY()] != null &&
                        d.getX() == this.getBoardSize() - 3 &&
                        this.board[this.getBoardSize() - 2][d.getY()].getOwner().isPlayerOne() &&
                        this.board[this.getBoardSize() - 2][d.getY()] instanceof Pawn)
                {
                    this.board[this.getBoardSize() - 2][d.getY()] = null;
                    ((Pawn) this.board[d.getX()][d.getY()]).kill();
                }
            }

            // edges
            if (this.board[0][d.getY()] != null &&
                    d.getX() == 1 &&
                    this.board[0][d.getY()].getOwner().isPlayerOne() &&
                    this.board[0][d.getY()] instanceof Pawn)
            {
                this.board[0][d.getY()] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
            if (this.board[this.getBoardSize() - 1][d.getY()] != null &&
                    d.getX() == this.getBoardSize() - 2 &&
                    this.board[this.getBoardSize() - 1][d.getY()].getOwner().isPlayerOne() &&
                    this.board[this.getBoardSize() - 1][d.getY()] instanceof Pawn)
            {
                this.board[this.getBoardSize() - 1][d.getY()] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
            if (this.board[d.getX()][0] != null &&
                    d.getY() == 1 &&
                    this.board[d.getX()][0].getOwner().isPlayerOne() &&
                    this.board[d.getX()][0] instanceof Pawn)
            {
                this.board[d.getX()][0] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
            if (this.board[d.getX()][this.getBoardSize() - 1] != null &&
                    d.getY() == this.getBoardSize() - 2 &&
                    this.board[d.getX()][this.getBoardSize() - 1].getOwner().isPlayerOne() &&
                    this.board[d.getX()][this.getBoardSize() - 1] instanceof Pawn)
            {
                this.board[d.getX()][this.getBoardSize() - 1] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }

            // all the rest
            if (d.getX() + 2 < this.getBoardSize() &&
                    this.board[d.getX() + 1][d.getY()] != null &&
                    this.board[d.getX() + 2][d.getY()] != null &&
                    this.board[d.getX() + 1][d.getY()].getOwner().isPlayerOne() &&
                    !this.board[d.getX() + 2][d.getY()].getOwner().isPlayerOne() &&
                    this.board[d.getX() + 1][d.getY()] instanceof Pawn)
            {
                this.board[d.getX() + 1][d.getY()] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
            if (d.getX() - 2 >= 0 &&
                    this.board[d.getX() - 1][d.getY()] != null &&
                    this.board[d.getX() - 2][d.getY()] != null &&
                    this.board[d.getX() - 1][d.getY()].getOwner().isPlayerOne() &&
                    !this.board[d.getX() - 2][d.getY()].getOwner().isPlayerOne() &&
                    this.board[d.getX() - 1][d.getY()] instanceof Pawn)
            {
                this.board[d.getX() - 1][d.getY()] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
            if (d.getY() + 2 < this.getBoardSize() &&
                    this.board[d.getX()][d.getY() + 1] != null &&
                    this.board[d.getX()][d.getY() + 2] != null &&
                    this.board[d.getX()][d.getY() + 1].getOwner().isPlayerOne() &&
                    !this.board[d.getX()][d.getY() + 2].getOwner().isPlayerOne() &&
                    this.board[d.getX()][d.getY() + 1] instanceof Pawn)
            {
                this.board[d.getX()][d.getY() + 1] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
            if (d.getY() - 2 >= 0 &&
                    this.board[d.getX()][d.getY() - 1] != null &&
                    this.board[d.getX()][d.getY() - 2] != null &&
                    this.board[d.getX()][d.getY() - 1].getOwner().isPlayerOne() &&
                    !this.board[d.getX()][d.getY() - 2].getOwner().isPlayerOne() &&
                    this.board[d.getX()][d.getY() - 1] instanceof Pawn)
            {
                this.board[d.getX()][d.getY() - 1] = null;
                ((Pawn) this.board[d.getX()][d.getY()]).kill();
            }
        }
    }

    private void checkmate()
    {
        if(board[0][0] instanceof King ||
                board[0][getBoardSize() - 1] instanceof King ||
                board[getBoardSize() - 1][0] instanceof King ||
                board[getBoardSize() - 1][getBoardSize() - 1] instanceof King)
        {
            this.defender.setWins(this.defender.getWins() + 1);
            setFinished(true);
        }

        boolean noAttackerPawns = true;
        for (int i = 0; i < getBoardSize() && noAttackerPawns; i++)
            for (int j = 0; j < getBoardSize() && noAttackerPawns; j++)
                if (board[i][j] instanceof Pawn && !board[i][j].getOwner().isPlayerOne())
                    noAttackerPawns = false;

        if(noAttackerPawns)
        {
            this.defender.setWins(this.defender.getWins() + 1);
            setFinished(true);
        }

        if(isKingCaptured())
        {
            this.attacker.setWins(this.attacker.getWins() + 1);
            setFinished(true);
        }
    }

    private boolean moveValid(Position a, Position b)
    {
        /*
            valid move:
            - horizontal or vertical move
            - move to null and all the places in between has to be null as well
            - not a corner
         */

        if (a.getX() != b.getX() && a.getY() != b.getY())
            return false;
        if(getPieceAtPosition(b) != null || getPieceAtPosition(a) == null)
            return false;
        int minX = Math.min(a.getX(),b.getX());
        int maxX = Math.max(a.getX(), b.getX());
        int minY = Math.min(a.getY(),b.getY());
        int maxY = Math.max(a.getY(), b.getY());
        if (a.getY() == b.getY())
            for (int i = minX + 1; i < maxX; i++)
                if(this.board[i][a.getY()] != null)
                    return false;

        if (a.getX() == b.getX())
            for (int i = minY + 1; i < maxY; i++)
                if(this.board[a.getX()][i] != null)
                    return false;


        if (getPieceAtPosition(a) instanceof Pawn && (b.getX() == getBoardSize() - 1 && b.getY() == getBoardSize() - 1 ||
                b.getX() == 0 && b.getY() == 0 ||
                b.getX() == getBoardSize() - 1 && b.getY() == 0 ||
                b.getX() == 0 && b.getY() == getBoardSize() - 1))
            return false;

        if(getPieceAtPosition(a).getOwner().isPlayerOne())
            return !isSecondPlayerTurn();
        return isSecondPlayerTurn();
    }

    @Override
    public Piece getPieceAtPosition(Position position)
    {
        return this.board[position.getX()][position.getY()];
    }

    @Override
    public Player getFirstPlayer()
    {
        return this.defender;
    }

    @Override
    public Player getSecondPlayer()
    {
        return this.attacker;
    }

    @Override
    public boolean isGameFinished()
    {
        return this.finished;
    }

    @Override
    public boolean isSecondPlayerTurn()
    {
        return this.attackerTurn;
    }

    @Override
    public void reset()
    {
        this.board = new ConcretePiece[this.BOARD_SIZE][this.BOARD_SIZE];
        this.positions = new Position[this.BOARD_SIZE][this.BOARD_SIZE];
        this.setFinished(false);
        this.setAttackerTurn(true);
        this.attacker.getPieces().clear();
        this.defender.getPieces().clear();

        for (int i = 3; i < 8; i++)
        {
            this.board[i][0] = new Pawn(this.attacker);
            this.board[i][0].setName("A" + (i - 2));
            this.board[0][i] = new Pawn(this.attacker);
            this.board[0][i].setName("A" + (2 * i + 1));
            this.board[i][10] = new Pawn(this.attacker);
            this.board[i][10].setName("A" + (i + 1) * 2);
            this.board[10][i] = new Pawn(this.attacker);
            this.board[10][i].setName("A" + (i + 17));
        }

        for (int i = 0; i <= 2; i++)
            for (int k = -i; k <= i; k++)
            {
                this.board[this.BOARD_SIZE / 2 + k][3 + i] = new Pawn(this.defender);
                this.board[this.BOARD_SIZE / 2 + k][7 - i] = new Pawn(this.defender);
            }
        this.board[5][3].setName("D1");
        this.board[4][4].setName("D2");
        this.board[5][4].setName("D3");
        this.board[6][4].setName("D4");
        this.board[3][5].setName("D5");
        this.board[4][5].setName("D6");
        this.board[6][5].setName("D8");
        this.board[7][5].setName("D9");
        this.board[4][6].setName("D10");
        this.board[5][6].setName("D11");
        this.board[6][6].setName("D12");
        this.board[5][7].setName("D13");

        this.board[this.BOARD_SIZE / 2][this.BOARD_SIZE / 2] = new King(this.defender);
        this.board[this.BOARD_SIZE / 2][this.BOARD_SIZE / 2].setName("K7");
        this.board[1][this.BOARD_SIZE / 2] = new Pawn(this.attacker);
        this.board[1][this.BOARD_SIZE / 2].setName("A12");
        this.board[this.BOARD_SIZE / 2][1] = new Pawn(this.attacker);
        this.board[this.BOARD_SIZE / 2][1].setName("A6");
        this.board[this.BOARD_SIZE - 2][this.BOARD_SIZE / 2] = new Pawn(this.attacker);
        this.board[this.BOARD_SIZE - 2][this.BOARD_SIZE / 2].setName("A13");
        this.board[this.BOARD_SIZE / 2][this.BOARD_SIZE - 2] = new Pawn(this.attacker);
        this.board[this.BOARD_SIZE / 2][this.BOARD_SIZE - 2].setName("A19");

        for (int i = 0; i < this.getBoardSize(); i++)
            for (int j = 0; j < this.getBoardSize(); j++)
            {
                this.positions[i][j] = new Position(i, j);
                if (this.board[i][j] != null)
                {
                    this.board[i][j].addMove(this.positions[i][j]);
                    if (this.board[i][j].getOwner().isPlayerOne())
                        this.defender.addPiece(this.board[i][j]);
                    else
                        this.attacker.addPiece((this.board[i][j]));
                }
            }
        this.pieces.addAll(this.attacker.getPieces());
        this.pieces.addAll(this.defender.getPieces());
        for (ConcretePiece piece : this.pieces)
        {
            piece.setDistance(0);
            if (piece instanceof King)
                continue;
            ((Pawn) piece).setKills(0);
        }
    }

    @Override
    public void undoLastMove()
    {

    }

    @Override
    public int getBoardSize()
    {
        return this.BOARD_SIZE;
    }

    private void setFinished(boolean finished)
    {
        this.finished = finished;
    }

    public void setAttackerTurn(boolean attackerTurn) {
        this.attackerTurn = attackerTurn;
    }
}
