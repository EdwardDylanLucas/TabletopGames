package games.gomoku;

import core.AbstractGameState;
import core.CoreConstants;
import core.actions.AbstractAction;
import core.actions.SetGridValueAction;
import core.components.GridBoard;
import core.components.Token;
import core.forwardModels.SequentialActionForwardModel;
import games.connect4.Connect4Constants;
import games.connect4.Connect4GameParameters;
import utilities.Pair;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;

public class GomokuForwardModel extends SequentialActionForwardModel {
    @Override
    protected void _setup(AbstractGameState firstState) {
        GomokuGameParameters ggp = (GomokuGameParameters) firstState.getGameParameters();
        int gridSize = ggp.gridSize;
        GomokuGameState state = (GomokuGameState) firstState;
        state.gridBoard = new GridBoard<>(gridSize, gridSize, new Token(GomokuConstants.emptyCell));
        state.winnerCells = new LinkedList<>();
    }

    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        GomokuGameState ggs = (GomokuGameState) gameState;
        ArrayList<AbstractAction> actions = new ArrayList<>();
        int player = ggs.getCurrentPlayer();

        if (gameState.isNotTerminal())
            for (int x = 0; x < ggs.gridBoard.getWidth(); x++) {
                for (int y = 0; y < ggs.gridBoard.getHeight(); y++) {
                    if (ggs.gridBoard.getElement(x, y).getTokenType().equals(GomokuConstants.emptyCell)) {
                        actions.add(new SetGridValueAction<>(ggs.gridBoard.getComponentID(), x, y, GomokuConstants.playerMapping.get(player)));
                    }
                }
            }
        return actions;
    }

    @Override
    protected void _afterAction(AbstractGameState currentState, AbstractAction action) {
        GomokuGameState ggs = (GomokuGameState) currentState;

        // game-specific check for end of game
        if (checkGameEnd(ggs)) {
            return;
        }
        super._afterAction(currentState, action);
    }

    /**
     * Checks if the game ended.
     *
     * @param gameState - game state to check game end.
     */
    private boolean checkGameEnd(GomokuGameState gameState) {
        GridBoard<Token> gridBoard = gameState.getGridBoard();
        GomokuGameParameters ggp = (GomokuGameParameters) gameState.getGameParameters();
        boolean gap = false;
        LinkedList<Pair<Integer, Integer>> winning = new LinkedList<>();;

        // Check columns
        for (int x = 0; x < gridBoard.getWidth(); x++) {
            int count = 0;
            String lastToken = null;
            winning.clear();
            for (int y = gridBoard.getHeight() - 1; y >= 0; y--) {
                Token c = gridBoard.getElement(x, y);
                if (c.getTokenType().equals(GomokuConstants.emptyCell)) {
                    count = 0;
                    lastToken = null;
                    winning.clear();
                    gap = true;
                } else if (lastToken == null || !lastToken.equals(c.getTokenType())) {
                    winning.clear();
                    count = 1;
                    lastToken = c.getTokenType();
                    winning.add(new Pair<>(x, y));
                } else {
                    {
                        count++;
                        winning.add(new Pair<>(x, y));
                        if (count == ggp.winCount) {
                            registerWinner(gameState, c, winning);
                            return true;
                        }
                    }
                    lastToken = c.getTokenType();
                }
            }
        }

        // Check rows
        for (int y = gridBoard.getHeight() - 1; y >= 0; y--) {
            int count = 0;
            String lastToken = null;
            winning.clear();
            for (int x = 0; x < gridBoard.getWidth(); x++) {
                Token c = gridBoard.getElement(x, y);
                if (c.getTokenType().equals(GomokuConstants.emptyCell)) {
                    count = 0;
                    lastToken = null;
                    winning.clear();
                } else if (lastToken == null || !lastToken.equals(c.getTokenType())) {
                    winning.clear();
                    count = 1;
                    lastToken = c.getTokenType();
                    winning.add(new Pair<>(x, y));
                } else {
                    {
                        count++;
                        winning.add(new Pair<>(x, y));
                        if (count == ggp.winCount) {
                            registerWinner(gameState, c, winning);
                            return true;
                        }
                    }
                    lastToken = c.getTokenType();
                }
            }
        }

        //Check main diagonals (from col 0)
        for (int y = gridBoard.getHeight() - 1; y >= 0; y--) {
            if (checkMainDiagonals(gameState, 0, y))
                return true;

        }

        //Check main and inverse diagonals (from row 0)
        for (int x = 1; x < gridBoard.getWidth(); x++) {
            if (checkMainDiagonals(gameState, x, gridBoard.getHeight() - 1))
                return true;
            if (checkInvDiagonals(gameState, x, gridBoard.getHeight() - 1))
                return true;
        }

        //Check inv diagonals (from last column)
        for (int y = gridBoard.getHeight() - 2; y >= 0; y--) { //height -1 is checked in previous loop
            if (checkInvDiagonals(gameState, gridBoard.getWidth()-1, y))
                return true;
        }

        if (!gap) { //tie
            gameState.setGameStatus(CoreConstants.GameResult.DRAW_GAME);
            Arrays.fill(gameState.getPlayerResults(), CoreConstants.GameResult.DRAW_GAME);
            return true;
        }

        return false;
    }


    private boolean checkMainDiagonals(GomokuGameState gameState, int xStart, int yStart)
    {
        GridBoard<Token> gridBoard = gameState.getGridBoard();
        GomokuGameParameters ggp = (GomokuGameParameters) gameState.getGameParameters();
        int count = 0;
        String lastToken = null;
        LinkedList<Pair<Integer, Integer>> winning = new LinkedList<>();

        for (int x = xStart, y = yStart; x < gridBoard.getWidth() && y >=0; x++, y--) {
            Token c = gridBoard.getElement(x, y);

            if (c.getTokenType().equals(GomokuConstants.emptyCell)) {
                count = 0;
                lastToken = null;
                winning.clear();
            } else if (lastToken == null || !lastToken.equals(c.getTokenType())) {
                winning.clear();
                count = 1;
                lastToken = c.getTokenType();
                winning.add(new Pair<>(x, y));
            } else {
                count++;
                winning.add(new Pair<>(x, y));
                if (count == ggp.winCount) {
                    registerWinner(gameState, c, winning);
                    return true;
                }
                lastToken = c.getTokenType();
            }
        }
        return false;
    }

    private boolean checkInvDiagonals(GomokuGameState gameState, int xStart, int yStart)
    {
        GridBoard<Token> gridBoard = gameState.getGridBoard();
        GomokuGameParameters ggp = (GomokuGameParameters) gameState.getGameParameters();
        int count = 0;
        String lastToken = null;
        LinkedList<Pair<Integer, Integer>> winning = new LinkedList<>();

        for (int x = xStart, y = yStart; x >= 0 && y >= 0; x--, y--) {
            Token c = gridBoard.getElement(x, y);

            if (c.getTokenType().equals(GomokuConstants.emptyCell)) {
                count = 0;
                lastToken = null;
                winning.clear();
            } else if (lastToken == null || !lastToken.equals(c.getTokenType())) {
                winning.clear();
                count = 1;
                lastToken = c.getTokenType();
                winning.add(new Pair<>(x, y));
            } else {
                count++;
                winning.add(new Pair<>(x, y));
                if (count == ggp.winCount) {
                    registerWinner(gameState, c, winning);
                    return true;
                }
                lastToken = c.getTokenType();
            }
        }
        return false;
    }

    /**
     * Inform the game this player has won.
     *
     * @param winnerSymbol - which player won.
     */
    private void registerWinner(GomokuGameState gameState, Token winnerSymbol, LinkedList<Pair<Integer, Integer>> winPos) {
        gameState.setGameStatus(CoreConstants.GameResult.GAME_END);
        int winningPlayer = GomokuConstants.playerMapping.indexOf(winnerSymbol);
        gameState.setPlayerResult(CoreConstants.GameResult.WIN_GAME, winningPlayer);
        gameState.setPlayerResult(CoreConstants.GameResult.LOSE_GAME, 1 - winningPlayer);
        gameState.registerWinningCells(winPos);
    }
}
