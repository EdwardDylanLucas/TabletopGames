package games.gomoku;

import core.AbstractParameters;
import evaluation.optimisation.TunableParameters;
import games.GameType;

import java.util.*;

public class GomokuGameParameters extends TunableParameters {


    public int gridSize = 15;
    public int winCount = 5;

    public GomokuGameParameters() {
        addTunableParameter("gridSize", 8, Arrays.asList(6, 8, 10, 12, 15));
        addTunableParameter("winCount", 5, Arrays.asList(3, 4, 5, 6));
        _reset();
    }

    @Override
    public void _reset() {
        gridSize = (int) getParameterValue("gridSize");
        winCount = (int) getParameterValue("winCount");
    }

    @Override
    protected AbstractParameters _copy() {
        GomokuGameParameters gp = new GomokuGameParameters();
        gp.gridSize = gridSize;
        gp.winCount = winCount;
        return gp;
    }

    @Override
    public boolean _equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GomokuGameParameters that = (GomokuGameParameters) o;
        return gridSize == that.gridSize && winCount == that.winCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), gridSize, winCount);
    }

    @Override
    public Object instantiate() { return GameType.Gomoku.createGameInstance(2, this); }


}
