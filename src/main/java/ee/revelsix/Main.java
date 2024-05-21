package ee.revelsix;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 4 || !args[0].equals("--config") || !args[2].equals("--betting-amount")) {
            System.err.println("Usage: java -jar scratch-game-jar-with-dependencies.jar --config <config.json> --betting-amount <betAmount>");
            System.exit(1);
        }

        String configPath = args[1];
        double betAmount = Double.parseDouble(args[3]);

        ObjectMapper mapper = new ObjectMapper();
        GameConfig config = mapper.readValue(new File(configPath), GameConfig.class);

        MatrixGenerator generator = new MatrixGenerator(config);
        String[][] matrix = generator.generateMatrix();

        RewardCalculator calculator = new RewardCalculator(config);
        RewardResult result = calculator.calculateReward(matrix, betAmount);

        System.out.println("Generated Matrix:");
        for (String[] row : result.getMatrix()) {
            for (String cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }

        System.out.println("Total Reward: " + result.getReward());
        if (result.getAppliedBonusSymbol() != null) {
            System.out.println("Applied Bonus Symbol: " + result.getAppliedBonusSymbol());
        }
        if (!result.getAppliedWinningCombinations().isEmpty()) {
            System.out.println("Applied Winning Combinations:");
            result.getAppliedWinningCombinations().forEach((symbol, combinations) -> {
                System.out.println("Symbol: " + symbol + " Combinations: " + combinations);
            });
        }
    }
}
