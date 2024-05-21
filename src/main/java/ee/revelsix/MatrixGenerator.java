package ee.revelsix;

import java.util.Map;
import java.util.Random;

public class MatrixGenerator {
    private final GameConfig config;

    public MatrixGenerator(GameConfig config) {
        this.config = config;
    }

    public String[][] generateMatrix() {
        String[][] matrix = new String[config.getRows()][config.getColumns()];
        Random random = new Random();

        for (StandardSymbolProbability ssp : config.getProbabilities().getStandard_symbols()) {
            int row = ssp.getRow();
            int col = ssp.getColumn();
            matrix[row][col] = getRandomSymbol(ssp.getSymbols(), random);
        }

        int randomRow = random.nextInt(config.getRows());
        int randomCol = random.nextInt(config.getColumns());
        matrix[randomRow][randomCol] = getRandomSymbol(config.getProbabilities().getBonus_symbols().getSymbols(), random);

        return matrix;
    }

    private String getRandomSymbol(Map<String, Integer> symbols, Random random) {
        int totalWeight = symbols.values().stream().mapToInt(i -> i).sum();
        int randomIndex = random.nextInt(totalWeight);

        int currentWeight = 0;
        for (Map.Entry<String, Integer> entry : symbols.entrySet()) {
            currentWeight += entry.getValue();
            if (currentWeight > randomIndex) {
                return entry.getKey();
            }
        }
        return null;
    }
}