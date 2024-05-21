package ee.revelsix;

import java.util.*;
import java.util.stream.Collectors;

class RewardCalculator {
    private final GameConfig config;
    private final String SAME_SYMBOLS = "same_symbols";
    private final String LINEAR_SYMBOLS = "linear_symbols";
    private final String BONUS = "bonus";
    private final String MULTIPLY_REWARD = "multiply_reward";
    private final String EXTRA_BONUS = "extra_bonus";
    private final String MISS = "miss";

    public RewardCalculator(GameConfig config) {
        this.config = config;
    }

    private Map<String, Integer> getSymbolCounts(String[][] matrix) {
        Map<String, Integer> symbolCounts = new HashMap<>();
        for (String[] row : matrix) {
            for (String symbol : row) {
                symbolCounts.put(symbol, symbolCounts.getOrDefault(symbol, 0) + 1);
            }
        }
        return symbolCounts;
    }

    public RewardResult calculateReward(String[][] matrix, double betAmount) {
        Map<String, List<String>> appliedWinningCombinations = new HashMap<>();
        Map<String, Integer> symbolCounts = getSymbolCounts(matrix);
        Map<String, Double> rewards = new HashMap<>();

        // Check for winning combinations
        int wcCount = config.getWin_combinations().values().stream().filter(a -> a.getWhen().equals(SAME_SYMBOLS))
                .min(Comparator.comparing(WinningCombination::getCount)).map(WinningCombination::getCount).orElse(0);
        for (Map.Entry<String, Integer> symbolCount : symbolCounts.entrySet()) {
            int count = symbolCount.getValue();
            String symbol = symbolCount.getKey();
            if (count >= wcCount) {
                rewards.put(symbol, betAmount * config.getSymbols().get(symbol).getReward_multiplier());
                String combination = config.getWin_combinations().entrySet().stream().filter(a -> a.getValue().getCount() == count).findFirst().get().getKey();
                List<String> list = appliedWinningCombinations.getOrDefault(symbol, new ArrayList<>());
                list.add(combination);
                appliedWinningCombinations.put(symbol, list);
            }
        }

        List<WinningCombination> winningCombinationsLinear = config.getWin_combinations().values().stream()
                .filter(a -> a.getWhen().equals(LINEAR_SYMBOLS)).collect(Collectors.toList());
        for (WinningCombination wc : winningCombinationsLinear) {
            for (String[] area : wc.getCovered_areas()) {
                String symbol = isLinearWin(matrix, area);
                if (symbol != null) {
                    rewards.put(symbol, rewards.get(symbol) * wc.getReward_multiplier());
                    List<String> list = appliedWinningCombinations.getOrDefault(symbol, new ArrayList<>());
                    list.add(wc.getGroup());
                    appliedWinningCombinations.put(symbol, list);
                }
            }
        }

        List<Map.Entry<String, Symbol>> bonuses = config.getSymbols().entrySet().stream()
                .filter(v -> v.getValue().getType().equals(BONUS) && symbolCounts.containsKey(v.getKey()))
                .collect(Collectors.toList());
        double reward = getReward(rewards, bonuses);

        return new RewardResult(matrix, reward, appliedWinningCombinations, bonuses.stream().findFirst().get().getKey());
    }

    private double getReward(Map<String, Double> rewards, List<Map.Entry<String, Symbol>> bonuses) {
        double reward = 0d;
        for (Double value : rewards.values()) {
            reward = reward + value;
        }

        for (Map.Entry<String, Symbol> entry : bonuses) {
            switch (entry.getValue().getImpact()) {
                case MULTIPLY_REWARD:
                    reward = reward * entry.getValue().getReward_multiplier();
                    break;
                case EXTRA_BONUS:
                    reward = reward + entry.getValue().getExtra();
                    break;
                case MISS:
                    reward = reward + entry.getValue().getReward_multiplier();
                    break;
            }
        }
        return reward;
    }

    private String isLinearWin(String[][] matrix, String[] area) {
        String symbol = null;
        for (String pos : area) {
            String[] parts = pos.split(":");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            if (symbol == null) {
                symbol = matrix[row][col];
            } else if (!symbol.equals(matrix[row][col])) {
                return null;
            }
        }
        return symbol;
    }
}

class RewardResult {
    private final String[][] matrix;
    private final double reward;
    private final Map<String, List<String>> appliedWinningCombinations;
    private final String appliedBonusSymbol;

    public RewardResult(String[][] matrix, double reward, Map<String, List<String>> appliedWinningCombinations, String appliedBonusSymbol) {
        this.matrix = matrix;
        this.reward = reward;
        this.appliedWinningCombinations = appliedWinningCombinations;
        this.appliedBonusSymbol = appliedBonusSymbol;
    }

    public String[][] getMatrix() {
        return matrix;
    }

    public double getReward() {
        return reward;
    }

    public Map<String, List<String>> getAppliedWinningCombinations() {
        return appliedWinningCombinations;
    }

    public String getAppliedBonusSymbol() {
        return appliedBonusSymbol;
    }
}
