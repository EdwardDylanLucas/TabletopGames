package players.simple;

import core.AbstractGameState;
import core.AbstractPlayer;
import core.actions.AbstractAction;
import core.components.Deck;
import core.interfaces.IStateHeuristic;
import games.sushigo.SGGameState;
import games.sushigo.actions.ChooseCard;
import games.sushigo.cards.SGCard;
import games.sushigo.cards.SGCard.SGCardType;
import players.heuristics.StateValueSushiGoHeuristic;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static games.sushigo.cards.SGCard.SGCardType.*;

import static utilities.Utils.noise;

public class OSLAPlayer extends AbstractPlayer {

    // Heuristic used for the agent
    IStateHeuristic heuristic;

    public OSLAPlayer(Random random) {
        super(null, "SuperOSLA");
        this.rnd = random;
    }

    public OSLAPlayer() {
        this(new Random());
    }

    public OSLAPlayer(IStateHeuristic heuristic) {
        this(heuristic, new Random());
    }

    public OSLAPlayer(IStateHeuristic heuristic, Random random) {
        this(random);
        this.heuristic = heuristic;
        setName("OSLA: " + heuristic.getClass().getSimpleName());
    }

    @Override
    public AbstractAction _getAction(AbstractGameState gs, List<AbstractAction> actions) {
        double maxQ = Double.NEGATIVE_INFINITY;
        AbstractAction bestAction = null;
        double[] valState = new double[actions.size()];
        int playerID = gs.getCurrentPlayer();

//        System.out.println("OSLAPlayer: " + heuristic + " : " + heuristic.getClass().getSimpleName());

        for (int actionIndex = 0; actionIndex < actions.size(); actionIndex++) {
            AbstractAction action = actions.get(actionIndex);
            AbstractGameState gsCopy = gs.copy();
            getForwardModel().next(gsCopy, action);

            if (heuristic != null) {
                // find out the card corresponding to each action
                // then print it out instead of the meaningless action
//                System.out.println("ActionIndex: " + actionIndex + " : " + action);

                ChooseCard chooseCard = (ChooseCard) action;
                int index = chooseCard.cardIdx;
                Deck<SGCard> playerHand = ((SGGameState) gsCopy).getPlayerHands().get(playerID);
                SGCard card = playerHand.get(index);

                int value = 0;

//                System.out.println("Returning value: " + value + " for card: " + card.type);
//                System.out.println("OSLAPlayer Heuristic: " + heuristic.evaluateState(gsCopy, playerID));
                if (heuristic instanceof StateValueSushiGoHeuristic) {
                    ((StateValueSushiGoHeuristic) heuristic).currentCard = card;
                }
                valState[actionIndex] = heuristic.evaluateState(gsCopy, playerID);
            } else {
                valState[actionIndex] = gsCopy.getHeuristicScore(playerID);
            }

            double Q = noise(valState[actionIndex], getParameters().noiseEpsilon, rnd.nextDouble());

            if (Q > maxQ) {
                maxQ = Q;
                bestAction = action;
            }
//            System.out.println("OSLAPlayer: " + action + " : " + Q);
        }
//        System.out.println("OSLAPlayer: " + bestAction + " : " + maxQ);
//        System.out.println("Heuristic: " + heuristic + " : " + heuristic.getClass().getSimpleName());
//        System.out.println();
        return bestAction;
    }

    @Override
    public OSLAPlayer copy() {
        OSLAPlayer retValue = new OSLAPlayer(heuristic, new Random(rnd.nextInt()));
        retValue.setForwardModel(getForwardModel().copy());
        return retValue;
    }

}
