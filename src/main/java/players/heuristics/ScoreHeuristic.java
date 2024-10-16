package players.heuristics;

import core.AbstractGameState;
import core.CoreConstants;
import core.interfaces.IStateHeuristic;

public class ScoreHeuristic implements IStateHeuristic {

    // todo: implement a sushi go heuristic, call it StateValueSushiGoHeuristic.
    // This will be similar to the action value heuristic except that
    // now we are adding the values of all the cards that we have played on the table.
    // i.e. a for loop over each card played by this player on the table, and adding the value of that card.
    // todo: specify the use of this heuristic in a new statevalueheuristicosla.json file
    // todo: set up and run the tournament with three players: SGosla, osla, and random

    // todo: implement a second state heuristic that also considers the score contributed by this card.

    /**
     * This cares mostly about the raw game score - but will treat winning as a 50% bonus
     * and losing as halving it
     *
     * @param gs       - game state to evaluate and score.
     * @param playerId - player id
     * @return
     */
    @Override
    public double evaluateState(AbstractGameState gs, int playerId) {
//        if (true)
//            throw new RuntimeException("Should not be called");
        double score = gs.getGameScore(playerId);
//        System.out.println("evaluateStateScore: " + score);
        if (gs.getPlayerResults()[playerId] == CoreConstants.GameResult.WIN_GAME)
            return score * 1.5;
        if (gs.getPlayerResults()[playerId] == CoreConstants.GameResult.LOSE_GAME)
            return score * 0.5;
        return score;
    }
    @Override
    public double minValue() {
        return Double.NEGATIVE_INFINITY;
    }
    @Override
    public double maxValue() {
        return Double.POSITIVE_INFINITY;
    }
}
