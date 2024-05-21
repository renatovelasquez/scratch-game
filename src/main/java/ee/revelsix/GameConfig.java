package ee.revelsix;

import java.util.Map;

public class GameConfig {
    private int columns;
    private int rows;
    private Map<String, Symbol> symbols;
    private Probabilities probabilities;
    private Map<String, WinningCombination> win_combinations;

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public Probabilities getProbabilities() {
        return probabilities;
    }

    public Map<String, WinningCombination> getWin_combinations() {
        return win_combinations;
    }
}

class Symbol {
    private String type;
    private double reward_multiplier;
    private Double extra;
    private String impact;

    public String getType() {
        return type;
    }

    public double getReward_multiplier() {
        return reward_multiplier;
    }

    public Double getExtra() {
        return extra;
    }

    public String getImpact() {
        return impact;
    }
}

class Probabilities {
    private StandardSymbolProbability[] standard_symbols;
    private BonusSymbolProbability bonus_symbols;

    public StandardSymbolProbability[] getStandard_symbols() {
        return standard_symbols;
    }

    public BonusSymbolProbability getBonus_symbols() {
        return bonus_symbols;
    }
}

class StandardSymbolProbability {
    private int column;
    private int row;
    private Map<String, Integer> symbols;

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public Map<String, Integer> getSymbols() {
        return symbols;
    }
}

class BonusSymbolProbability {
    private Map<String, Integer> symbols;

    public Map<String, Integer> getSymbols() {
        return symbols;
    }
}

class WinningCombination {
    private double reward_multiplier;
    private String when;
    private int count;
    private String group;
    private String[][] covered_areas;

    public double getReward_multiplier() {
        return reward_multiplier;
    }

    public String getWhen() {
        return when;
    }

    public int getCount() {
        return count;
    }

    public String getGroup() {
        return group;
    }

    public String[][] getCovered_areas() {
        return covered_areas;
    }
}
