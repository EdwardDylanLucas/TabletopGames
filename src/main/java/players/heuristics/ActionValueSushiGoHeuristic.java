package players.heuristics;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.components.Deck;
import core.interfaces.IActionHeuristic;
import games.sushigo.SGGameState;
import games.sushigo.actions.ChooseCard;
import games.sushigo.cards.SGCard;
import games.sushigo.cards.SGCard.SGCardType;


import java.util.HashMap;
import java.util.List;

import static games.sushigo.cards.SGCard.SGCardType.*;

public class ActionValueSushiGoHeuristic implements IActionHeuristic {

    private final double tempuraValue = 5.0/2;
    private final double sashimiValue = 10.0/3;
    private final double dumplingValue = 3.0/2;
    private final double squidNigiriValue = 3.0;
    private final double salmonNigiriValue = 2.0;
    private final double eggNigiriValue = 1.0;
    private final double wasabiMultiplier = 2.0;
    private final double makiValuePerRoll = 1.0;
    private final double chopsticksValue = 2.0;
    private final double puddingValue = 4.0;

    private HashMap<SGCardType, Double> actionValueMap = new HashMap<SGCardType, Double>();

    public ActionValueSushiGoHeuristic() {
        actionValueMap.put(Tempura, tempuraValue);
        actionValueMap.put(Sashimi, sashimiValue);
        actionValueMap.put(Dumpling, dumplingValue);
        actionValueMap.put(SquidNigiri, squidNigiriValue);
        actionValueMap.put(SalmonNigiri, salmonNigiriValue);
        actionValueMap.put(EggNigiri, eggNigiriValue);
        actionValueMap.put(Wasabi, wasabiMultiplier);
        actionValueMap.put(Maki, makiValuePerRoll);
        actionValueMap.put(Chopsticks, chopsticksValue);
        actionValueMap.put(Pudding, puddingValue);
    }

    @Override
    public double evaluateAction(AbstractAction action, AbstractGameState state, List<AbstractAction> contextActions) {
//        System.out.println("ActionValueSushiGoHeuristic.evaluateAction");
//        // and get the class
//        System.out.println("action.getClass() = " + action.getClass());
//        System.out.println("action = " + action);
        ChooseCard chooseCard = (ChooseCard) action;
        int playerID = chooseCard.playerId;
        int index = chooseCard.cardIdx;
        Deck<SGCard> playerHand = ((SGGameState) state).getPlayerHands().get(playerID);
        SGCard card = playerHand.get(index);
        Double value = actionValueMap.get(card.type);
        if (value == null) {
            throw new RuntimeException("Unknown card type: " + card.type);
        }
//        System.out.println("Returning value: " + value + " for card: " + card.type);
//        System.out.println();
        return value;
    }


    @Override
    public double[] evaluateAllActions(List<AbstractAction> actions, AbstractGameState state) {
        return IActionHeuristic.super.evaluateAllActions(actions, state);
    }

}
