package games.diamant.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IPrintable;
import games.diamant.DiamantGameState;
import games.diamant.DiamantTurnOrder;

public class ExitFromCave extends AbstractAction implements IPrintable {
    @Override
    public boolean execute(AbstractGameState gs) {
        DiamantGameState dgs = (DiamantGameState) gs;
        DiamantTurnOrder to = (DiamantTurnOrder) dgs.getTurnOrder();
        to.play(dgs.getCurrentPlayer(), this);
        to.endPlayerTurn(dgs);
        return true;
    }

    @Override
    public AbstractAction copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        return obj instanceof ExitFromCave;
    }


    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "Exit";
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return toString();
    }
}
