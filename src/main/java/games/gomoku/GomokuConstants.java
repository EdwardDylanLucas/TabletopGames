package games.gomoku;
import core.components.Token;

import java.util.ArrayList;

public class GomokuConstants {
    public static final ArrayList<Token> playerMapping = new ArrayList<Token>() {{
        add(new Token("⚫"));
        add(new Token("⚪"));
    }};
    public static final String emptyCell = ".";
}
