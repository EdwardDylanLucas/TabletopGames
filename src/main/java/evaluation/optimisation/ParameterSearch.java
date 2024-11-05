package evaluation.optimisation;

import evaluation.RunArg;
import games.GameType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static evaluation.RunArg.game;
import static evaluation.RunArg.parseConfig;
import static utilities.Utils.getArg;

public class ParameterSearch {

    public static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);
        if (argsList.contains("--help") || argsList.contains("-h")) {
            RunArg.printHelp(RunArg.Usage.ParameterSearch);
            return;
        }

        // Config
        Map<RunArg, Object> config = parseConfig(args, Collections.singletonList(RunArg.Usage.ParameterSearch));

        String setupFile = config.getOrDefault(RunArg.config, "").toString();
        if (!setupFile.isEmpty()) {
            // Read from file instead
            try {
                FileReader reader = new FileReader(setupFile);
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(reader);
                config = parseConfig(json, RunArg.Usage.ParameterSearch);
            } catch (FileNotFoundException ignored) {
                throw new AssertionError("Config file not found : " + setupFile);
                //    parseConfig(runGames, args);
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (config.get(RunArg.game).equals("all")) {
            System.out.println("No game provided. Please provide a game.");
            return;
        }
        GameType game = GameType.valueOf(config.get(RunArg.game).toString());
        if (game == GameType.GameTemplate) {
            System.out.println("No game provided. Please provide a game.");
            return;
        }
        int nPlayers = (int) config.get(RunArg.nPlayers);
        if (nPlayers < game.getMinPlayers() || nPlayers > game.getMaxPlayers()) {
            System.out.println("Invalid number of players for game " + game + ". Please provide a valid number of players.");
            return;
        }
        String searchSpaceFile = config.get(RunArg.searchSpace).toString();
        if (searchSpaceFile.isEmpty()) {
            System.out.println("No search space file provided. Please provide a search space file.");
            return;
        }

        NTBEAParameters params = new NTBEAParameters(config);
        params.printSearchSpaceDetails();

        System.out.println("Running NTBEA with the following parameters:");
        System.out.println(params);

        params.verbose = true;
        params.budget = 30;
        params.iterationsPerRun = 30;
        params.repeats = 3;
        params.evalGames = 20;
        params.evalMethod = "Win";
        params.neighbourhoodSize = 5;
        params.kExplore = 0.2;

        switch (params.mode) {
            case NTBEA:
            case CoopNTBEA:
            case StableNTBEA:
                NTBEA singleNTBEA = new NTBEA(params, game, nPlayers);
                singleNTBEA.run();
                break;
            case MultiNTBEA:
                MultiNTBEA multiNTBEA = new MultiNTBEA(params, game, nPlayers);
                multiNTBEA.run();
                break;
        }
        // loop over the key value pairs and print them out
        for (Map.Entry<RunArg, Object> entry : config.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }

    }


}
