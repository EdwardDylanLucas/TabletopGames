package games.gomoku;
import core.components.Token;

import java.util.ArrayList;

public class GomokuConstants {
    public static final ArrayList<Token> playerMapping = new ArrayList<Token>() {{
        add(new Token("b"));
        add(new Token("w"));
    }};
    public static final String emptyCell = ".";
}
