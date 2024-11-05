package tempo;

import games.sushigo.SGForwardModel;
import games.sushigo.SGGameState;
import games.sushigo.SGParameters;

import java.util.Random;

public class PlayWithSushiHands {

    public static void playWithSushiActions(int nPlayers) {
        SGForwardModel fm = new SGForwardModel();
        SGParameters params = new SGParameters();
        params.setRandomSeed(4902);
        SGGameState state = new SGGameState(params, nPlayers);
        fm.setup(state);

        // now run check the hand deals
        for (int i = 0; i < nPlayers; i++) {
            var hand = state.getPlayerHands().get(i);
            System.out.println("hand = " + hand);
            // now experiment with playing a card
            // we'll just play the first card in the hand
            var availableActions = fm.computeAvailableActions(state);
            var action = availableActions.get(0);
            fm.next(state, action);
            System.out.println("state = " + state);
            var tempHand = state.getPlayerHands().get(i);
            System.out.println("hand = " + tempHand);
            System.out.println();
        }
    }

    public static void scoreActionsTest(int nPlayers) {
        SGForwardModel fm = new SGForwardModel();
        SGParameters params = new SGParameters();
        params.setRandomSeed(4902);
        SGGameState state = new SGGameState(params, nPlayers);
        fm.setup(state);
        params.setRandomSeed(123);
        var randomFinish = randomRollout(fm, state);
        System.out.println("randomFinish = " + randomFinish);
        // print the state score
        System.out.println("state.getScore() = " + state.getGameScore(2));
    }

    public static SGGameState randomRollout(SGForwardModel fm, SGGameState state) {
        Random myRandom = new Random(123);
        while (state.isNotTerminal()) {
            var availableActions = fm.computeAvailableActions(state);
            var action = availableActions.get(myRandom.nextInt(availableActions.size()));
            System.out.println("Taking action: " + action);
            fm.next(state, action);
        }
        return state;
    }

    public static void main(String[] args) {
//        playWithSushiActions(2);
        scoreActionsTest(2);
    }
}
