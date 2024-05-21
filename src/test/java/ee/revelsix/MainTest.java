package ee.revelsix;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    private GameConfig gameConfig;

    @BeforeEach
    public void setUp() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        gameConfig = mapper.readValue(new File("config.json"), GameConfig.class);
    }

    @Test
    public void testMatrixGeneration() {
        MatrixGenerator generator = new MatrixGenerator(gameConfig);
        String[][] matrix = generator.generateMatrix();

        assertEquals(3, matrix.length);
        assertEquals(3, matrix[0].length);
    }

    @Test
    public void testRewardCalculationWonGameLessLikely() {
        String[][] matrix = {
                {"A", "A", "B"},
                {"A", "+1000", "B"},
                {"A", "A", "B"}
        };

        RewardCalculator calculator = new RewardCalculator(gameConfig);
        RewardResult result = calculator.calculateReward(matrix, 100);

        assertEquals(16000, result.getReward());
        assertEquals(Arrays.asList("same_symbol_5_times", "vertically_linear_symbols"), result.getAppliedWinningCombinations().get("A"));
        assertEquals(Arrays.asList("same_symbol_3_times", "vertically_linear_symbols"), result.getAppliedWinningCombinations().get("B"));
        assertEquals("+1000", result.getAppliedBonusSymbol());
    }

    @Test
    public void testRewardCalculationWonGameMostLikely() {
        String[][] matrix = {
                {"A", "B", "C"},
                {"E", "B", "10x"},
                {"F", "D", "B"}
        };

        RewardCalculator calculator = new RewardCalculator(gameConfig);
        RewardResult result = calculator.calculateReward(matrix, 100);

        assertEquals(25000, result.getReward());
        assertEquals(Collections.singletonList("same_symbol_3_times"), result.getAppliedWinningCombinations().get("B"));
        assertEquals("10x", result.getAppliedBonusSymbol());
    }

    @Test
    public void testRewardCalculationWonGameDiagonal() {
        String[][] matrix = {
                {"C", "B", "A"},
                {"E", "C", "F"},
                {"+500", "D", "C"}
        };

        RewardCalculator calculator = new RewardCalculator(gameConfig);
        RewardResult result = calculator.calculateReward(matrix, 100);

        assertEquals(5500, result.getReward());
        assertEquals(Arrays.asList("same_symbol_3_times", "ltr_diagonally_linear_symbols"), result.getAppliedWinningCombinations().get("C"));
        assertEquals("+500", result.getAppliedBonusSymbol());
    }

    @Test
    public void testRewardCalculationLostGame() {
        String[][] matrix = {
                {"A", "B", "C"},
                {"E", "B", "5x"},
                {"F", "D", "C"}
        };

        RewardCalculator calculator = new RewardCalculator(gameConfig);
        RewardResult result = calculator.calculateReward(matrix, 100);

        assertEquals(0, result.getReward());
        assertEquals(0, result.getAppliedWinningCombinations().size());
        assertEquals("5x", result.getAppliedBonusSymbol());
    }

    @Test
    public void testRewardCalculationLostGameMiss() {
        String[][] matrix = {
                {"C", "MISS", "D"},
                {"F", "D", "D"},
                {"F", "D", "B"}
        };

        RewardCalculator calculator = new RewardCalculator(gameConfig);
        RewardResult result = calculator.calculateReward(matrix, 100);

        assertEquals(0, result.getReward());
        assertEquals(1, result.getAppliedWinningCombinations().size());
        assertEquals("MISS", result.getAppliedBonusSymbol());
    }

}
