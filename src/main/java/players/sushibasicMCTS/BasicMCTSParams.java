package players.sushibasicMCTS;

import core.AbstractGameState;
import core.interfaces.IStateHeuristic;
import players.PlayerParameters;

import java.util.Arrays;


public class BasicMCTSParams extends PlayerParameters {

    public double K = Math.sqrt(2);
    public int rolloutLength = 10; // assuming we have a good heuristic
    public int maxTreeDepth = 100; // effectively no limit
    public double epsilon = 1e-6;
    public IStateHeuristic heuristic = AbstractGameState::getHeuristicScore;

    // hack for now: add in the card values as params here
    public double puddingValue = 5.0/2;
    public double tempuraValue = 5.0/2;
    public double sashimiValue = 10.0/3;
    public double dumplingValue = 1.0;
    public double makiValuePerRoll = 3.0;
    public double squidNigiriValue = 3.0;
    public double salmonNigiriValue = 2.0;
    public double eggNigiriValue = 1.0;
    public double wasabiMultiplier = 1.0;
    public double chopsticksValue = 1.0;

    public double heuristicWeight = 0.1;

    public BasicMCTSParams() {
//        addTunableParameter("K", Math.sqrt(2), Arrays.asList(0.0, 0.1, 1.0, Math.sqrt(2), 3.0, 10.0));
//        addTunableParameter("rolloutLength", 10, Arrays.asList(0, 3, 10, 30, 100));
//        addTunableParameter("maxTreeDepth", 100, Arrays.asList(1, 3, 10, 30, 100));
//        addTunableParameter("epsilon", 1e-6);
//        addTunableParameter("heuristic", (IStateHeuristic) AbstractGameState::getHeuristicScore);

//        addTunableParameter("puddingValue", 5.0/2, Arrays.asList(-5, -3, -1, 0, 1, 3.0, 5));
//        addTunableParameter("tempuraValue", 5.0/2, Arrays.asList(-5, -3, -1, 0, 1, 3.0, 5));
//        addTunableParameter("sashimiValue", 10.0/3, Arrays.asList(-5, -3, -1, 0, 1, 3.0, 5));
//        addTunableParameter("dumplingValue", 1.0, Arrays.asList(-5, -3, -1, 0, 1, 3.0, 5));
//        addTunableParameter("makiValuePerRoll", 3.0, Arrays.asList(-5, -3, -1, 0, 1, 3.0, 5));
//        addTunableParameter("squidNigiriValue", 3.0, Arrays.asList(-5, -3, -1, 0, 1, 3.0, 5));
//        addTunableParameter("salmonNigiriValue", 2.0, Arrays.asList(-5, -3, -1, 0, 1, 3.0, 5));
//        addTunableParameter("eggNigiriValue", 1.0, Arrays.asList(-5, -3, -1, 0, 1, 3.0, 5));
//        addTunableParameter("wasabiMultiplier", 1.0, Arrays.asList(-5, -3, -1, 0, 1, 3.0, 5));
//        addTunableParameter("chopsticksValue", 1.0, Arrays.asList(-5, -3, -1, 0, 1, 3.0, 5));

        addTunableParameter("heuristicWeight", 0.1, Arrays.asList(0.0, 0.1, 0.001, 0.0001, 0.00001, -0.001));
    }

    @Override
    public void _reset() {
        super._reset();
//        K = (double) getParameterValue("K");
//        rolloutLength = (int) getParameterValue("rolloutLength");
//        maxTreeDepth = (int) getParameterValue("maxTreeDepth");
//        epsilon = (double) getParameterValue("epsilon");
//        heuristic = (IStateHeuristic) getParameterValue("heuristic");

//        puddingValue = (double) getParameterValue("puddingValue");
//        tempuraValue = (double) getParameterValue("tempuraValue");
//        sashimiValue = (double) getParameterValue("sashimiValue");
//        dumplingValue = (double) getParameterValue("dumplingValue");
//        makiValuePerRoll = (double) getParameterValue("makiValuePerRoll");
//        squidNigiriValue = (double) getParameterValue("squidNigiriValue");
//        salmonNigiriValue = (double) getParameterValue("salmonNigiriValue");
//        eggNigiriValue = (double) getParameterValue("eggNigiriValue");
//        wasabiMultiplier = (double) getParameterValue("wasabiMultiplier");
//        chopsticksValue = (double) getParameterValue("chopsticksValue");

            heuristicWeight = (double) getParameterValue("heuristicWeight");
    }

    @Override
    protected BasicMCTSParams _copy() {
        // All the copying is done in TunableParameters.copy()
        // Note that any *local* changes of parameters will not be copied
        // unless they have been 'registered' with setParameterValue("name", value)
        return new BasicMCTSParams();
    }

    public IStateHeuristic getHeuristic() {
        return heuristic;
    }

    @Override
    public BasicMCTSPlayer instantiate() {
        return new BasicMCTSPlayer((BasicMCTSParams) this.copy());
    }

}
