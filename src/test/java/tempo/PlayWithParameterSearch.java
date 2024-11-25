package tempo;

import evaluation.optimisation.ParameterSearch;

public class PlayWithParameterSearch {
    public static void main(String[] args) {

        // play with calling the parameter search code in various ways

        var paramSearchArgs = new String[] {
//                "--help",
                "searchSpace=optimization/sushiSearchSpace.json",
//                "searchSpace=players.sushibasicMCTS.BasicMCTSParams",
                "game=SushiGo",
                "nPlayers=2",
                "opponent=mcts"
//                "nEvals=10",
//                "NTBEAMode=StableNTBEA",
//                "repeats=3",
//                "evalMethod=Score",
//                "evalGames=20",
//                "budget=30",
//                "verbose=true",
//                "iterations=30"
        };

        // this will run the parameter search with the given arguments
        ParameterSearch.main(paramSearchArgs);

    }
}
