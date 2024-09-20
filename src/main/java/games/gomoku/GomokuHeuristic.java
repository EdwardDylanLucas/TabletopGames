package games.gomoku;
import core.AbstractGameState;
import core.CoreConstants;
import core.interfaces.IStateHeuristic;
import evaluation.optimisation.TunableParameters;

public class GomokuHeuristic extends TunableParameters implements IStateHeuristic {

    public GomokuHeuristic() {
    }

    @Override
    public void _reset() {
    }

    @Override
    public double evaluateState(AbstractGameState gs, int playerId) {
        //This is a simple win-lose heuristic.

        CoreConstants.GameResult playerResult = gs.getPlayerResults()[playerId];

        if(playerResult == CoreConstants.GameResult.LOSE_GAME) {
            return -1;
        }
        if(playerResult == CoreConstants.GameResult.WIN_GAME) {
            return 1;
        }

        return 0;
    }

    /**
     * Return a copy of this game parameters object, with the same parameters as in the original.
     *
     * @return - new game parameters object.
     */
    @Override
    protected GomokuHeuristic _copy() {
        return new GomokuHeuristic();
    }

    /**
     * Checks if the given object is the same as the current.
     *
     * @param o - other object to test equals for.
     * @return true if the two objects are equal, false otherwise
     */
    @Override
    protected boolean _equals(Object o) {
        return o instanceof GomokuHeuristic;
    }

    /**
     * @return Returns Tuned Parameters corresponding to the current settings
     * (will use all defaults if setParameterValue has not been called at all)
     */
    @Override
    public GomokuHeuristic instantiate() {
        return this._copy();
    }
}
