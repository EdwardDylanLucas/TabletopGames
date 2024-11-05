package tempo;

import evaluation.RunArg;
import evaluation.optimisation.NTBEAParameters;

import java.io.File;
import java.util.Map;

public class PlayWithNTBEA {
    static void playWithParams() {
        Map<RunArg, Object> config = Map.of(
                RunArg.budget, 1000,
                RunArg.finalPercent, 0.2,
                RunArg.nPlayers, 2,
                RunArg.tuneGame, false,
                RunArg.iterations, 1000,
                RunArg.repeats, 10,
                RunArg.matchups, 200,
                RunArg.evalGames, -1,
                RunArg.kExplore, 0.1,
                RunArg.neighbourhood, 10

        );
        // set up the budget
        int budget = (int) config.get(RunArg.tuningBudget);
        double NTBEABudgetOnTournament = (double) config.get(RunArg.finalPercent); // the complement will be spent on NTBEA runs

        NTBEAParameters ntbeaParameters = new NTBEAParameters(config);
        int gameBudget = (int) config.get(RunArg.tuningBudget);
        // then we override the parameters we want to change

        int nPlayers = (int) config.get(RunArg.nPlayers);

        ntbeaParameters.destDir = ntbeaParameters.destDir + File.separator + "Budget_" + budget + File.separator + "NTBEA";
        ntbeaParameters.repeats = Math.max(nPlayers, ntbeaParameters.repeats);
        ntbeaParameters.budget = budget;

        ntbeaParameters.tournamentGames = (int) (gameBudget * NTBEABudgetOnTournament);
        ntbeaParameters.iterationsPerRun = (gameBudget - ntbeaParameters.tournamentGames) / ntbeaParameters.repeats;
        if (ntbeaParameters.mode == NTBEAParameters.Mode.StableNTBEA) {
            ntbeaParameters.iterationsPerRun /= nPlayers;
        }
        ntbeaParameters.evalGames = 0;
        ntbeaParameters.logFile = "NTBEA_Runs.log";

        System.out.println("NTBEA Parameters: " + ntbeaParameters);

    }

    public static void main(String[] args) {
        playWithParams();
    }
}
