package games.gomoku;

import core.AbstractGameState;
import core.interfaces.IStateFeatureVector;
import core.components.Token;
import core.interfaces.IStateKey;

import java.util.Arrays;
import java.util.stream.IntStream;

public class GomokuStateVector implements IStateFeatureVector, IStateKey{
    // assume the grid is 15x15 ... if not, write a new StateVector
    private final String[] names = (String[]) IntStream.range(0, 15).boxed().flatMap(row ->
            IntStream.range(0, 3).mapToObj(col -> String.format("%d:%d", row, col))
    ).toArray(String[]::new);

    @Override
    public double[] featureVector(AbstractGameState gs, int playerID) {
        GomokuGameState state = (GomokuGameState) gs;
        String playerChar = GomokuConstants.playerMapping.get(playerID).getTokenType();

        return Arrays.stream(state.gridBoard.flattenGrid()).mapToDouble(c -> {
            String pos = ((Token) c).getTokenType();
            if (pos.equals(playerChar)) {
                return 1.0;
            } else if (pos.equals(GomokuConstants.emptyCell)) {
                return 0.0;
            } else { // opponent's piece
                return -1.0;
            }
        }).toArray();

    }

    @Override
    public String[] names() {
        return names;
    }

    @Override
    public String getKey(AbstractGameState state) {
        double[] retValue = featureVector(state, state.getCurrentPlayer());
        return String.format("%d-%s", state.getCurrentPlayer(), Arrays.toString(retValue));
    }
}
