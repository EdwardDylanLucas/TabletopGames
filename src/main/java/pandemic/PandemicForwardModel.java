package pandemic;

import actions.*;
import components.*;
import content.*;
import core.Area;
import core.ForwardModel;
import core.Game;
import core.GameState;
import pandemic.actions.AddCardToDeck;
import pandemic.actions.AddResearchStation;
import pandemic.actions.DrawCard;
import pandemic.actions.InfectCity;
import utilities.Hash;

import java.util.Random;

import static pandemic.actions.MovePlayer.placePlayer;
import static pandemic.PandemicGameState.*;

public class PandemicForwardModel implements ForwardModel {
    int[] infectionRate = new int[]{2, 2, 2, 3, 3, 4, 4};  // TODO: json
    private Game game;

    @Override
    public void setup(GameState firstState, Game game) {
        PandemicGameState state = (PandemicGameState) firstState;
        this.game = game;

        // 1 research station in Atlanta
        new AddResearchStation("Atlanta").execute(state);  // TODO maybe static

        // init counters
        game.findCounter("Outbreaks").setValue(0);
        game.findCounter("Infection Rate").setValue(0);
        for (String color : colors) {
            game.findCounter("Disease " + color).setValue(0);
            int hash = Hash.GetInstance().hash("Disease " + color);
        }

        // infection
        Deck infectionDeck = game.findDeck("Infections");
        Deck infectionDiscard = game.findDeck("Infection Discard");
        infectionDeck.shuffle();
        int nCards = 3;  // TODO json
        int nTimes = 3;  // TODO json
        for (int j = 0; j < nTimes; j++) {
            for (int i = 0; i < nCards; i++) {
                Card c = infectionDeck.draw();

                // Place matching color (nTimes - j) cubes and place on matching city
                new InfectCity(c, nTimes - j).execute(state);

                // Discard card
                new DrawCard(infectionDeck, infectionDiscard).execute(state);
            }
        }

        // give players cards
        String playerDeckStr = "Cities";
        Deck playerCards = game.findDeck("Player Roles");
        Deck playerDeck = game.findDeck(playerDeckStr);
        playerDeck.add(game.findDeck("Events")); // contains city & event cards
        playerCards.shuffle();
        int nCardsPlayer = 6 - state.nPlayers();  // TODO: params
        long maxPop = 0;
        int startingPlayer = -1;

        for (int i = 0; i < state.nPlayers(); i++) {
            // Draw a player card
            Card c = playerCards.draw();

            // Give the card to this player
            Area playerArea = state.getAreas().get(i);
            playerArea.setComponent(Constants.playerCardHash, c);

            // Also add this player in Atlanta
            placePlayer(state, "Atlanta", i);

            // Give players cards
            Deck playerHandDeck = (Deck) playerArea.getComponent(Constants.playerHandHash);

            playerDeck.shuffle();
            for (int j = 0; j < nCardsPlayer; j++) {
                new DrawCard(game.findDeck(playerDeckStr), playerHandDeck).execute(state);
            }

            for (Card card: playerHandDeck.getCards()) {
                Property property = card.getProperty(Hash.GetInstance().hash("population"));
                if (property != null){
                    long pop = ((PropertyLong) property).value;
                    if (pop > maxPop) {
                        startingPlayer = i;
                        maxPop = pop;
                    }
                }
            }
        }

        // Epidemic cards
        playerDeck.shuffle();
        int noCards = playerDeck.getCards().size();
        int noEpidemicCards = 4;  // TODO: json or game params
        int range = noCards / noEpidemicCards;
        for (int i = 0; i < noEpidemicCards; i++) {
            int index = i * range + i + new Random().nextInt(range);  // TODO seed

            Card card = new Card();
            card.addProperty(Hash.GetInstance().hash("name"), new PropertyString("epidemic"));
            new AddCardToDeck(card, playerDeck, index).execute(state);

        }

        // Player with highest population starts
        state.setActivePlayer(startingPlayer);
    }

    @Override
    public void next(GameState currentState, Action action) {
        playerActions(currentState, action);
        if (currentState.roundStep >= currentState.nInputActions()) {
            currentState.roundStep = 0;
            drawCards(currentState);
            infectCities(currentState);

            // Set the next player as active
            ((PandemicGameState) currentState).setActivePlayer((currentState.getActivePlayer() + 1) % currentState.nPlayers());
        }
    }

    private void playerActions(GameState currentState, Action action) {

        currentState.roundStep += 1;
        action.execute(currentState);
    }

    // TODO: create new temporary decks, add to gamestate, remove afterwards.
    private void drawCards(GameState currentState) {
        // drawCards(2);

        int noCardsDrawn = 2;
        int activePlayer = currentState.getActivePlayer();

        String tempDeckID = currentState.tempDeck();
        DrawCard action = new DrawCard("Cities", tempDeckID);  // TODO player deck
        for (int i = 0; i < noCardsDrawn; i++) {  // Draw cards for active player from player deck into a new deck
            Deck cityDeck = currentState.findDeck("Cities");
            boolean canDraw = cityDeck.getCards().size() > 0;

            // if player cannot draw it means that the deck is empty -> GAME OVER
            if (!canDraw){
                game.gameOver();
                System.out.println("No more cards to draw");
            }
            action.execute(currentState);

        }
        Deck tempDeck = currentState.findDeck(tempDeckID);
        for (Card c : tempDeck.getCards()) {  // Check the drawn cards

            if (((PropertyString)c.getProperty(Hash.GetInstance().hash("name"))).value.hashCode() == Constants.epidemicCard) {  // If epidemic card, do epidemic  // TODO: if 2 in a row, reshuffle second
                epidemic(currentState);
            } else

            {  // Otherwise, give card to player
                Area area = currentState.getAreas().get(activePlayer);
                Deck deck = (Deck) area.getComponent(Constants.playerHandHash);
                if (deck != null) {
                    // deck size doesn't go beyond 7
//                    new AddCardToDeck(c, deck).execute(currentState);
                    if (!new AddCardToDeck(c, deck).execute(currentState)){
                        // player needs to discard a card
                        game.getPlayers().get(activePlayer).getAction(currentState);
                    }
                }
            }
        }
        currentState.clearTempDeck();
    }

    private void epidemic(GameState currentState) {
        // 1. infection counter idx ++
        currentState.findCounter("Infection Rate").increment(1);

        // 2. 3 cubes on bottom card in infection deck, then add this card on top of infection discard
        Card c = currentState.findDeck("Infections").pickLast();
        if (c == null){
            // cannot draw card
            game.gameOver();
            System.out.println("No more cards to draw");
            return;
        }
        new InfectCity(c, 3).execute(currentState);
        new AddCardToDeck(c, currentState.findDeck("Infection Discard")).execute(currentState);

        // TODO: wanna play event card?

        // 3. shuffle infection discard deck, add back on top of infection deck
        Deck infectionDiscard = currentState.findDeck("Infection Discard");
        infectionDiscard.shuffle();
        for (Card card: infectionDiscard.getCards()) {
            new AddCardToDeck(card, currentState.findDeck("Infections")).execute(currentState);
        }
    }

    private void infectCities(GameState currentState) {
        Counter infectionCounter = currentState.findCounter("Infection Rate");
        int noCardsDrawn = infectionRate[infectionCounter.getValue()];
        String tempDeckID = currentState.tempDeck();
        DrawCard action = new DrawCard("Infections", tempDeckID);
        for (int i = 0; i < noCardsDrawn; i++) {  // Draw cards for active player from player deck into a new deck
            action.execute(currentState);
        }
        Deck tempDeck = new Deck(); //TODO: This new deck object should be replaced by an actual, consisitent temp deck.
        for (Card c : tempDeck.getCards()) {  // Check the drawn cards
            new InfectCity(c, 1).execute(currentState);
            new AddCardToDeck(c, currentState.findDeck("Infection Discard")).execute(currentState);
        }
        currentState.clearTempDeck();
    }
}