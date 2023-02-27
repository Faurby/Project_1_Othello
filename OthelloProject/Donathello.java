import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.print.attribute.standard.RequestingUserName;

public class Donathello implements IOthelloAI {

    public class Pair<T1, T2> {
        T1 val1;
        T2 val2;

        public Pair(T1 val1, T2 val2) {
            this.val1 = val1;
            this.val2 = val2;
        }
    }

    private double[][] weightedBoard;

    @Override
    public Position decideMove(GameState s) {
        if (weightedBoard == null) {
            weightedBoard = buildWeightedGameBoard(s.getBoard().length);
        }

        var p = minimax(s, 4, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println("Utility for best move is " + p.val2);
        return p.val1;
    }

    public Pair<Position, Double> minimax(GameState s, int depth, boolean maximizingPlayer, double alpha, double beta) {
        if (depth == 0 || s.legalMoves().size() == 0) {
            return new Pair<>(new Position(-1, -1), getUtility(s));
        }

        Position bestMove = null;
        double value;

        if (maximizingPlayer) {
            value = Double.MIN_VALUE;
            for (var possibleState : getChildGameStates(s)) {
                var p = minimax(possibleState.val1, depth - 1, false, alpha, beta);

                if (value < p.val2) {
                    value = p.val2;
                    bestMove = possibleState.val2;
                }

                if (value > beta) {
                    break;
                }

                alpha = Math.max(alpha, value);
            }
        } else {
            value = Double.MAX_VALUE;
            for (var possibleState : getChildGameStates(s)) {
                var p = minimax(possibleState.val1, depth - 1, true, alpha, beta);

                if (value > p.val2) {
                    value = p.val2;
                    bestMove = possibleState.val2;
                }

                if (value < alpha) {
                    break;
                }

                beta = Math.min(beta, value);
            }
        }

        return new Pair<>(bestMove, value);
    }

    // Returns the gamestates for alle legal moves in the provided gamestate
    public List<Pair<GameState, Position>> getChildGameStates(GameState s) {
        return s.legalMoves().stream().map(move -> {
            var gameState = new GameState(s.getBoard(), s.getPlayerInTurn());
            gameState.insertToken(move);
            return new Pair<>(gameState, move);
        }).collect(Collectors.toList());
    }

    public double getUtility(GameState s) {
        return heuristic(s);
    }

    /**
     * Counts weighted tokens of the player 1 (black) and player 2 (white),
     * respectively, and returns an array
     * with the numbers in that order.
     */
    public double[] countWeightedTokens(GameState s) {
        double tokens1 = 0;
        double tokens2 = 0;
        int size = s.getBoard().length;
        int[][] board = s.getBoard();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 1)
                    tokens1 += getWeightedTile(i, j);
                else if (board[i][j] == 2)
                    tokens2 += getWeightedTile(i, j);
            }
        }
        return new double[] { tokens1, tokens2 };
    }

    public double[][] buildWeightedGameBoard(int size) {
        double[][] weightedBoard = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Corners
                if ((i == 0 || i == size - 1) && (j == 0 || j == size - 1)) {
                    weightedBoard[i][j] = 1;
                    // Adjacent to corners (on edges).
                } else if (((i == 1 || i == size - 2) && (j == 0 || j == size - 1))
                        || ((i == 0 || i == size - 1) && (j == 1 || j == size - 2))) {
                    weightedBoard[i][j] = -0.5;
                    // Diagonally adjacent to corners.
                } else if ((i == 1 || i == size - 2) && (j == 1 || j == size - 2)) {
                    weightedBoard[i][j] = -1;
                    // Edges except special cases above.
                } else if (((j == 0 || j == size - 1) && (i > 2 && i < size - 3)
                        || i == 0 || i == size - 1) && (j > 2 && j < size - 3)) {
                    weightedBoard[i][j] = 0.8;
                } else if (((j == 1 || j == size - 2) && (i > 1 && i < size - 3)
                        || i == 1 || i == size - 2) && (j > 1 && j < size - 3)) {
                    weightedBoard[i][j] = -0.5;
                } else {
                    weightedBoard[i][j] = 0.3;
                }
            }
        }

        return weightedBoard;
    }

    private static double EDGE_MULTIPLIER = 0.3;
    private static double heuristicWeight = 0.5;

    public double getWeightedTile(int col, int row) {
        return weightedBoard[col][row];
    }

    public double[] getOccupiedPercentage(GameState s) {
        var tiles = s.countTokens();
        var black = tiles[0];
        var white = tiles[1];
        var occupied = white + black;
        var total = Math.pow(s.getBoard().length, 2);

        return new double[] {occupied / total, black / occupied, white / occupied, black, white};
    }
    
    public double heuristic(GameState s) {
        var occupation = getOccupiedPercentage(s);
        var occupationHeuristic = - Math.abs(occupation[0] - occupation[s.getPlayerInTurn()]);
        // var positionHeuristic = countWeightedTokens(s)[s.getPlayerInTurn()-1] / (occupation[s.getPlayerInTurn()+2]);
        var positionHeuristic = countWeightedTokens(s)[s.getPlayerInTurn()-1];
        return heuristicWeight * positionHeuristic + (1.0 - heuristicWeight) * occupationHeuristic;
    }

    public double fieldHeuristic(int col, int row, int size) {
        return fieldHeuristic(col, size)
                + fieldHeuristic(row, size);
    }

    public double fieldHeuristic(int pos, int size) {
        var r = Math.pow(1 + (pos - (size / 2.0)), 2) * EDGE_MULTIPLIER;
        return r;
    }
}
