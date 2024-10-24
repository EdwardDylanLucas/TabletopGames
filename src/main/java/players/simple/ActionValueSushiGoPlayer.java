package players.simple;

import core.AbstractGameState;
import core.actions.AbstractAction;
import players.heuristics.ActionValueSushiGoHeuristic;

import java.util.List;

public class ActionValueSushiGoPlayer extends BoltzmannActionPlayer {
    public ActionValueSushiGoPlayer() {
        super(new ActionValueSushiGoHeuristic(), 0.0001, 0.0001);
    }

    @Override
    public AbstractAction _getAction(AbstractGameState gameState, List<AbstractAction> possibleActions) {
        // throw a runtime exception if the game state is not a SushiGoGameState
        if (!(gameState instanceof games.sushigo.SGGameState)) {
            throw new RuntimeException("ActionValueSushiGoPlayer requires a SushiGoGameState");
        }
        return super._getAction(gameState, possibleActions);
    }
}
