package players.heuristics;

import core.AbstractGameState;
import core.components.Deck;
import core.interfaces.IStateHeuristic;
import games.sushigo.SGGameState;
import games.sushigo.cards.SGCard;

import java.util.HashMap;
import java.util.List;

import static games.sushigo.cards.SGCard.SGCardType.*;
import static games.sushigo.cards.SGCard.SGCardType.Pudding;

public class StateValueSushiGoHeuristic implements IStateHeuristic {

    public SGCard currentCard = null;

    private final double tempuraValue = 5.0 / 2;
    private final double sashimiValue = 10.0 / 3;
    private final double dumplingValue = 3.0 / 2;
    private final double squidNigiriValue = 3.0;
    private final double salmonNigiriValue = 2.0;
    private final double eggNigiriValue = 1.0;
    private final double wasabiMultiplier = 2.0;
    private final double makiValuePerRoll = 1.0;
    private final double chopsticksValue = 2.0;
    private final double puddingValue = 4.0;

    private HashMap<SGCard.SGCardType, Double> cardValueMap = new HashMap<SGCard.SGCardType, Double>();

    public StateValueSushiGoHeuristic() {
        cardValueMap.put(Tempura, tempuraValue);
        cardValueMap.put(Sashimi, sashimiValue);
        cardValueMap.put(Dumpling, dumplingValue);
        cardValueMap.put(SquidNigiri, squidNigiriValue);
        cardValueMap.put(SalmonNigiri, salmonNigiriValue);
        cardValueMap.put(EggNigiri, eggNigiriValue);
        cardValueMap.put(Wasabi, wasabiMultiplier);
        cardValueMap.put(Maki, makiValuePerRoll);
        cardValueMap.put(Chopsticks, chopsticksValue);
        cardValueMap.put(Pudding, puddingValue);
    }

    @Override
    public double evaluateState(AbstractGameState state, int playerId) {
//        if (true) {
//            throw new RuntimeException("Should not be called");
//        }
        double score = 0;
        if (currentCard != null) {
            score = cardValueMap.get(currentCard.type);
            return score;
        }
        SGGameState gs = (SGGameState) state;
        // get all the cards that have been played by this player
        List<Deck<SGCard>> playedCards = gs.getPlayedCards();
        for (SGCard card : playedCards.get(playerId)) {
            score += cardValueMap.get(card.type);
  //          System.out.println("Card: " + card.type + " Value: " + cardValueMap.get(card.type));
        }
   //     System.out.println("-----");
        return score;
    }

}
