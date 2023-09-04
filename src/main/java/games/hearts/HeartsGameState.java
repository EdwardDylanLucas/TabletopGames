package games.hearts;

import core.AbstractGameState;
import core.AbstractParameters;
import core.CoreConstants;
import core.components.Component;
import core.components.Deck;
import core.components.FrenchCard;
import core.interfaces.IGamePhase;
import games.GameType;
import games.hearts.heuristics.HeartsHeuristic;

import java.util.ArrayList;
import java.util.*;

import java.util.function.*;


/**
 * <p>The game state encapsulates all game information. It is a data-only class, with game functionality present
 * in the Forward Model or actions modifying the state of the game.</p>
 * <p>Most variables held here should be {@link Component} subclasses as much as possible.</p>
 * <p>No initialisation or game logic should be included here (not in the constructor either). This is all handled externally.</p>
 * <p>Computation may be included in functions here for ease of access, but only if this is querying the game state information.
 * Functions on the game state should never <b>change</b> the state of the game.</p>
 */
public class HeartsGameState extends AbstractGameState {
    List<Deck<FrenchCard>> playerDecks;
    Deck<FrenchCard> drawDeck;
    public List<Deck<FrenchCard>> trickDecks;
    public boolean heartsBroken;
    public int[] playerTricksTaken;
    public List<List<FrenchCard>> pendingPasses;
    public Map<Integer, Integer> playerPoints;
    public List<Map.Entry<Integer, FrenchCard>> currentPlayedCards = new ArrayList<>();
    public FrenchCard.Suite firstCardSuit;
    Random rnd;

    public HeartsGameState(AbstractParameters gameParameters, int nPlayers) {
        super(gameParameters, nPlayers);
        rnd = new Random(gameParameters.getRandomSeed());
    }

    @Override
    protected GameType _getGameType() {
        return GameType.Hearts;
    }

    @Override
    protected List<Component> _getAllComponents() {
        return new ArrayList<Component>() {{
            addAll(playerDecks);
            add(drawDeck);
        }};
    }

    public enum Phase implements IGamePhase {
        PASSING,
        PLAYING
    }

    public Deck<FrenchCard> getDrawDeck() {
        return drawDeck;
    }

    public List<Deck<FrenchCard>> getPlayerDecks() {
        return playerDecks;
    }

    public void scorePointsAtEndOfRound() {
        HeartsParameters params = (HeartsParameters) getGameParameters();
        for (int playerId = 0; playerId < getNPlayers(); playerId++) {

            // Get the trick deck for the player
            Deck<FrenchCard> trickDeck = trickDecks.get(playerId);
            if (trickDeck != null) {
                int points = 0;

                // Iterate over all cards in the trick deck
                for (FrenchCard card : trickDeck.getComponents()) {
                    if (card.suite == FrenchCard.Suite.Hearts) {
                        points += params.heartCard;
                    }
                    // The queen of spades is worth 13 points
                    else if (card.suite == FrenchCard.Suite.Spades && card.number == 12) {
                        points += params.queenOfSpades;
                    }
                }

                if (points == params.shootTheMoon) {
                    // all other players get the points instead
                    for (int i = 0; i < getNPlayers(); i++) {
                        if (i != playerId) {
                            playerPoints.put(i, playerPoints.getOrDefault(i, 0) + points);
                        }
                    }
                }

                // Check if the playerID exists in the map
                if (playerPoints.containsKey(playerId)) {
                    // If it does, add the points
                    playerPoints.put(playerId, playerPoints.get(playerId) + points);
                } else {
                    // If not, add the playerID to the map with the calculated points
                    playerPoints.put(playerId, points);
                }

                // Clear the trick deck after its points have been added
                trickDeck.clear();
            }
        }
    }

    public int getPlayerPoints(int playerID) {
        return playerPoints.getOrDefault(playerID, 0);
    }

    public Map<Integer, Integer> getPlayerPointsMap() {
        return playerPoints;
    }

    @Override
    protected AbstractGameState _copy(int playerId) {
        HeartsGameState copy = new HeartsGameState(gameParameters.copy(), getNPlayers());

        // Deep Copy player decks
        copy.playerDecks = new ArrayList<>();
        for (Deck<FrenchCard> d : playerDecks) {
            copy.playerDecks.add(d.copy());
        }

        // Deep Copy draw deck
        copy.drawDeck = drawDeck.copy();

        // Deep Copy trickDecks
        copy.trickDecks = new ArrayList<>();
        for (Deck<FrenchCard> d : trickDecks) {
            copy.trickDecks.add(d.copy());
        }

        copy.heartsBroken = heartsBroken;

        // Deep Copy playerTricksTaken
        copy.playerTricksTaken = Arrays.copyOf(playerTricksTaken, playerTricksTaken.length);

        // Deep Copy pendingPasses
        copy.pendingPasses = new ArrayList<>();
        for (List<FrenchCard> list : pendingPasses) {
            copy.pendingPasses.add(new ArrayList<>(list));
        }

        // Deep Copy playerPoints
        copy.playerPoints = new HashMap<>(playerPoints);


        // Deep Copy currentRoundCards
        copy.currentPlayedCards = new ArrayList<>();
        for (Map.Entry<Integer, FrenchCard> entry : currentPlayedCards) {
            copy.currentPlayedCards.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().copy()));
        }

        copy.firstCardSuit = firstCardSuit;

        if (getCoreGameParameters().partialObservable && playerId != -1) {
            // Now we need to blank out the passed cards that the player cannot see
            // and these need to go into the draw deck for shuffling
            for (int i = 0; i < getNPlayers(); i++) {
                if (i != playerId) {
                    copy.drawDeck.add(copy.pendingPasses.get(i));
                    copy.drawDeck.add(copy.playerDecks.get(i));
                    copy.playerDecks.get(i).clear();
                    copy.pendingPasses.get(i).clear();
                }
            }
            copy.drawDeck.shuffle(rnd);

            for (int i = 0; i < getNPlayers(); i++) {
                if (i != playerId) {
                    for (int j = 0; j < playerDecks.get(i).getSize(); j++) {
                        copy.playerDecks.get(i).add(copy.drawDeck.draw());
                    }
                    for (int j = 0; j < pendingPasses.get(i).size(); j++) {
                        copy.pendingPasses.get(i).add(copy.drawDeck.draw());
                    }
                }
            }
        }

        return copy;
    }

    @Override
    protected double _getHeuristicScore(int playerId) {
        return new HeartsHeuristic().evaluateState(this, playerId);
    }

    @Override
    public double getGameScore(int playerId) {
        return playerPoints.getOrDefault(playerId, 0);
    }

    public void setPlayerPoints(int playerId, int points) {
        playerPoints.put(playerId, points);
    }

    public CoreConstants.GameResult getPlayerResult(int playerIdx) {
        return playerResults[playerIdx];
    }

    @Override
    protected ArrayList<Integer> _getUnknownComponentsIds(int playerId) {
        return new ArrayList<Integer>() {{
            add(drawDeck.getComponentID());
            for (Component c : drawDeck.getComponents()) {
                add(c.getComponentID());
            }
        }};
    }

    @Override
    public boolean _equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HeartsGameState)) return false;
        if (!super.equals(o)) return false;
        HeartsGameState that = (HeartsGameState) o;
        return heartsBroken == that.heartsBroken &&
                Arrays.equals(playerTricksTaken, that.playerTricksTaken) &&
                Objects.equals(playerDecks, that.playerDecks) &&
                Objects.equals(drawDeck, that.drawDeck) &&
                Objects.equals(trickDecks, that.trickDecks) &&
                Objects.equals(pendingPasses, that.pendingPasses) &&
                Objects.equals(playerPoints, that.playerPoints) &&
                Objects.equals(currentPlayedCards, that.currentPlayedCards) &&
                Objects.equals(firstCardSuit, that.firstCardSuit);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), playerDecks, drawDeck, heartsBroken,
                firstCardSuit, trickDecks,
                pendingPasses, playerPoints, currentPlayedCards);
        result = 31 * result + Arrays.hashCode(playerTricksTaken);
        return result;
    }

    public List<Deck<FrenchCard>> getPlayerTrickDecks() {
        return trickDecks;
    }

    @Override
    public int getOrdinalPosition(int playerId, Function<Integer, Double> scoreFunction, BiFunction<Integer, Integer, Double> tiebreakFunction) {
        int ordinal = 1;
        double playerScore = scoreFunction.apply(playerId);
        for (int i = 0, n = getNPlayers(); i < n; i++) {
            double otherScore = scoreFunction.apply(i);
            if (otherScore < playerScore) // Changed to < because lower score is better
                ordinal++;
            else if (otherScore == playerScore && tiebreakFunction != null && tiebreakFunction.apply(i, 1) != Double.MAX_VALUE) {
                if (getOrdinalPositionTiebreak(i, tiebreakFunction, 1) > getOrdinalPositionTiebreak(playerId, tiebreakFunction, 1))
                    ordinal++;
            }
        }
        return ordinal;
    }


}








