package games.gomoku.gui;

import core.AbstractGameState;
import core.AbstractPlayer;
import core.CoreConstants;
import core.Game;
import core.actions.AbstractAction;
import core.actions.SetGridValueAction;
import core.components.Token;
import games.connect4.Connect4Constants;
import games.connect4.Connect4GameState;
import games.connect4.gui.Connect4BoardView;
import games.gomoku.GomokuGameState;
import games.gomoku.GomokuConstants;
import gui.AbstractGUIManager;
import gui.GamePanel;
import gui.IScreenHighlight;
import players.human.ActionController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GomokuGUIManager extends AbstractGUIManager {

    GomokuBoardView view;

    public GomokuGUIManager(GamePanel parent, Game game, ActionController ac, Set<Integer> humanId) {
        super(parent, game, ac, humanId);
        if (game == null) return;

        GomokuGameState gameState = (GomokuGameState) game.getGameState();
        view = new GomokuBoardView(gameState.getGridBoard());

        // Set width/height of display
        this.width = Math.max(defaultDisplayWidth, defaultItemSize * gameState.getGridBoard().getWidth());
        this.height = defaultItemSize * gameState.getGridBoard().getHeight();

        JPanel infoPanel = createGameStateInfoPanel("Gomoku", gameState, width, defaultInfoPanelHeight);
        JComponent actionPanel = createActionPanel(new IScreenHighlight[]{view},
                width, defaultActionPanelHeight);

        parent.setLayout(new BorderLayout());
        parent.add(view, BorderLayout.CENTER);
        parent.add(infoPanel, BorderLayout.NORTH);
        parent.add(actionPanel, BorderLayout.SOUTH);
        parent.setPreferredSize(new Dimension(width, height + defaultActionPanelHeight + defaultInfoPanelHeight + defaultCardHeight + 20));
        parent.revalidate();
        parent.setVisible(true);
        parent.repaint();
    }

    @Override
    public int getMaxActionSpace() { return 1; }

    @Override
    protected void updateActionButtons(AbstractPlayer player, AbstractGameState gameState) {
        if (gameState.getGameStatus() == CoreConstants.GameResult.GAME_ONGOING) {
            List<core.actions.AbstractAction> actions = player.getForwardModel().computeAvailableActions(gameState);
            ArrayList<Rectangle> highlight = view.getHighlight();

            int start = actions.size();
            if (highlight.size() > 0) {
                Rectangle r = highlight.get(0);
                for (AbstractAction abstractAction : actions) {
                    SetGridValueAction<Token> action = (SetGridValueAction<Token>) abstractAction;
                    if (action.getX() == r.x/defaultItemSize) { // && action.getY() == r.y/defaultItemSize) {
                        actionButtons[0].setVisible(true);
                        actionButtons[0].setButtonAction(action, "Play " + Connect4Constants.playerMapping.get(player.getPlayerID())
                                + " in column " + (action.getX() + 1));
                        break;
                    }
                }
            } else {
                actionButtons[0].setVisible(false);
                actionButtons[0].setButtonAction(null, "");
            }
        }
    }

    @Override
    protected void _update(AbstractPlayer player, AbstractGameState gameState) {
        if (gameState != null) {
            view.updateComponent(((GomokuGameState) gameState).getGridBoard());
            view.setWinningCells(((GomokuGameState) gameState).getWinningCells());
        }

    }
}