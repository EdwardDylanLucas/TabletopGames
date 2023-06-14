package players.rl;

import java.util.Arrays;

import core.AbstractParameters;
import players.PlayerParameters;

public class RLParams extends PlayerParameters {

    enum Solver {
        Q_LEARNING,
        SARSA
    }

    // TODO Choose good default values
    public Solver solver = Solver.Q_LEARNING;
    public float alpha = 0.5f;
    public float gamma = 0.5f;
    public float epsilon = 0.5f;

    public final RLFeatureVector features;

    public RLParams(RLFeatureVector features) {
        this(features, System.currentTimeMillis());
    }

    public RLParams(RLFeatureVector features, long seed) {
        super(seed);
        this.features = features;
        addTunableParameter("solver", Solver.Q_LEARNING, Arrays.asList(Solver.values()));
        addTunableParameter("alpha", 0.5f, Arrays.asList(0f, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, .9f, 1f));
        addTunableParameter("gamma", 0.5f, Arrays.asList(0f, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, .9f, 1f));
        addTunableParameter("epsilon", 0.5f, Arrays.asList(0f, .1f, .2f, .3f, .4f, .5f, .6f, .7f, .8f, .9f, 1f));
    }

    @Override
    public RLPlayer instantiate() {
        return new RLPlayer(this);
    }

    @Override
    protected AbstractParameters _copy() {
        RLParams retValue = new RLParams(features, System.currentTimeMillis());
        retValue.solver = solver;
        retValue.alpha = alpha;
        retValue.gamma = gamma;
        retValue.epsilon = epsilon;
        return retValue;
    }

}
