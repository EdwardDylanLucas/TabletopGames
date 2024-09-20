package games.gomoku;

import core.AbstractGameState;
import core.AbstractParameters;
import core.components.Component;
import core.components.Token;
import core.components.GridBoard;
import core.interfaces.IGridGameState;
import core.interfaces.IPrintable;
import games.GameType;
import utilities.Pair;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class GomokuGameState extends AbstractGameState implements IPrintable, IGridGameState<Token> {

    GridBoard<Token> gridBoard;
    LinkedList<Pair<Integer, Integer>> winnerCells;

    public GomokuGameState(AbstractParameters gameParameters, int nPlayers) {
        super(gameParameters, nPlayers);
        winnerCells = new LinkedList<>();
        gridBoard = null;
    }

    @Override
    protected GameType _getGameType() { return GameType.Gomoku; }

    @Override
    protected List<Component> _getAllComponents() {
        return new ArrayList<Component>() {{
            add(gridBoard);
        }};
    }

    @Override
    protected AbstractGameState _copy(int playerId) {
        GomokuGameState s = new GomokuGameState(gameParameters.copy(), getNPlayers());
        s.gridBoard = gridBoard.copy();

        s.winnerCells.clear();
        for (Pair<Integer, Integer> wC : this.winnerCells)
            s.winnerCells.add(wC.copy());

        return s;
    }

    @Override
    protected double _getHeuristicScore(int playerId) { return new GomokuHeuristic().evaluateState(this, playerId); }

    @Override
    public double getGameScore(int playerId) { return playerResults[playerId].value; }

    @Override
    protected boolean _equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GomokuGameState)) return false;
        if (!super.equals(o)) return false;
        GomokuGameState that = (GomokuGameState) o;
        return Objects.equals(gridBoard, that.gridBoard);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        for (int y = 0; y < gridBoard.getHeight(); y++) {
            for (int x = 0; x < gridBoard.getWidth(); x++) {
                if (y != 0 || x != 0) {
                    sb.append(",");
                }
                Token t = gridBoard.getElement(x, y);
                sb.append("\"").append("Grid_").append(x).append('_').append(y).append("\":\"").append(t.getTokenType()).append("\"");
            }
        }

        sb.append("}");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gridBoard);
    }

    @Override
    public GridBoard<Token> getGridBoard() {
        return gridBoard;
    }

    @Override
    public void printToConsole() {
        System.out.println(gridBoard.toString());
    }

    void registerWinningCells(LinkedList<Pair<Integer, Integer>> winnerCells) {
        this.winnerCells = winnerCells;
    }

    public LinkedList<Pair<Integer, Integer>> getWinningCells() {
        return winnerCells;
    }
}
