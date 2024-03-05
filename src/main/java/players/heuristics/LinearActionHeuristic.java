package players.heuristics;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IActionFeatureVector;
import core.interfaces.IActionHeuristic;
import core.interfaces.ICoefficients;
import core.interfaces.IStateFeatureVector;
import utilities.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;

/**
 * Provides a wrapper around an IStateFeatureVector and an array of coefficients
 */
public abstract class LinearActionHeuristic extends GLMHeuristic implements IActionHeuristic {

    protected IStateFeatureVector features;
    protected IActionFeatureVector actionFeatures;

    String[] names;

    @Override
    public String[] names() {
        return names;
    }


    /**
     * The coefficientsFile is a tab separated file with the first line being the names of the features
     * and the second line being the coefficients.
     * <p>
     * The convention required is that the State coefficients are first, followed by the Action coefficients.
     *
     * @param featureVector
     * @param actionFeatureVector
     * @param coefficientsFile
     */
    public LinearActionHeuristic(IActionFeatureVector actionFeatureVector, IStateFeatureVector featureVector, String coefficientsFile) {
        this.features = featureVector;
        this.actionFeatures = actionFeatureVector;
        // then add on the action feature names
        names = new String[features.names().length + actionFeatures.names().length];
        System.arraycopy(features.names(), 0, names, 0, features.names().length);
        System.arraycopy(actionFeatures.names(), 0, names, features.names().length, actionFeatures.names().length);
        Pair<double[], Map<int[], Double>> x = loadModel(coefficientsFile);
        this.coefficients = x.a;
        this.interactionCoefficients = x.b;
    }


    @Override
    public double evaluateAction(AbstractAction action, AbstractGameState state) {
        if (coefficients == null)
            throw new AssertionError("No coefficients found");
        double[] phi = features.featureVector(state, state.getCurrentPlayer());
        double[] psi = actionFeatures.featureVector(action, state, state.getCurrentPlayer());
        double[] combined = new double[phi.length + psi.length];
        System.arraycopy(phi, 0, combined, 0, phi.length);
        System.arraycopy(psi, 0, combined, phi.length, psi.length);

        return inverseLinkFunction.applyAsDouble(applyCoefficients(combined));
    }

}
