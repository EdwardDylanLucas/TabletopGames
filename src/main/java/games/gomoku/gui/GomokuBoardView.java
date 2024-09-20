package games.gomoku.gui;

import core.components.GridBoard;
import core.components.Token;
import games.gomoku.GomokuConstants;
import gui.IScreenHighlight;
import gui.views.ComponentView;
import utilities.Pair;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;


public class GomokuBoardView extends ComponentView implements IScreenHighlight {

    Rectangle[] rects;  // Used for highlights + action trimming
    ArrayList<Rectangle> highlight;
    LinkedList<Pair<Integer, Integer>> winningCells;

    static final int defaultItemSize = gui.GUI.defaultItemSize / 2;

    public GomokuBoardView(GridBoard<Token> gridBoard) {
        super(gridBoard, gridBoard.getWidth() * defaultItemSize, gridBoard.getHeight() * defaultItemSize);
        rects = new Rectangle[gridBoard.getWidth()];
        highlight = new ArrayList<>();
        winningCells = new LinkedList<Pair<Integer, Integer>>();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // Left click, highlight cell
                    for (Rectangle r : rects) {
                        if (r != null && r.contains(e.getPoint())) {
                            highlight.clear();
                            highlight.add(r);
                            break;
                        }
                    }
                } else {
                    // Remove highlight
                    highlight.clear();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        drawGridBoard((Graphics2D) g, (GridBoard<Token>) component, 0, 0);

        if (highlight.size() > 0) {
            g.setColor(Color.green);
            Stroke s = ((Graphics2D) g).getStroke();
            ((Graphics2D) g).setStroke(new BasicStroke(3));

            Rectangle r = highlight.get(0);
            g.drawRect(r.x, r.y, r.width, r.height);
            ((Graphics2D) g).setStroke(s);
        }

        if (winningCells.size() > 0)
            drawWinningCells((Graphics2D) g);
    }

    public void drawGridBoard(Graphics2D g, GridBoard<Token> gridBoard, int x, int y) {
        int width = gridBoard.getWidth() * defaultItemSize;
        int height = gridBoard.getHeight() * defaultItemSize;

        //Draw cells background
        g.setColor(Color.lightGray);
        g.fillRect(x, y, width - 1, height - 1);
        g.setColor(Color.black);


        // Draw main cells
        for (int i = 0; i < gridBoard.getHeight(); i++) {
            for (int j = 0; j < gridBoard.getWidth(); j++) {
                int xC = x + j * defaultItemSize;
                int yC = y + i * defaultItemSize;
                drawCell(g, gridBoard.getElement(j, i), xC, yC);

            }
        }
        System.out.println();
    }

    private void drawCell(Graphics2D g, Token element, int x, int y) {


        // Paint element in cell
        if (element != null) {

            // Paint cell background
            g.setColor(Color.green);
            g.fillRect(x, y, defaultItemSize, defaultItemSize);
            g.setColor(Color.black);
            g.drawRect(x, y, defaultItemSize, defaultItemSize);

            Font f = g.getFont();
            g.setFont(new Font(f.getName(), Font.BOLD, defaultItemSize * 4 / 4));
            if (!element.toString().equals(GomokuConstants.emptyCell)) {
                g.drawString(element.toString(), x + defaultItemSize / 16, y + defaultItemSize - defaultItemSize / 16);
                System.out.println("Drawing element: " + element.toString());
            }

            g.setFont(f);
        } else {
            // Paint cell background
            g.setColor(Color.pink);
            g.fillRect(x, y, defaultItemSize, defaultItemSize);
            g.setColor(Color.black);
            g.drawRect(x, y, defaultItemSize, defaultItemSize);

        }

    }

    public ArrayList<Rectangle> getHighlight() {
        return highlight;
    }

    public void drawWinningCells(Graphics2D g) {
        g.setColor(Color.cyan);
        for (Pair<Integer, Integer> wC : winningCells)
            g.drawRect(wC.a * defaultItemSize, wC.b * defaultItemSize, defaultItemSize, defaultItemSize);
        g.setColor(Color.black);
    }

    public void setWinningCells(LinkedList<Pair<Integer, Integer>> winningCells) {
        this.winningCells = winningCells;
    }

    @Override
    public void clearHighlights() {
        highlight.clear();
    }

    private int cellSize() {
        // todo: make this dynamic based on the size of the board
        // currently it is unused
        return defaultItemSize / 2;
    }
}
