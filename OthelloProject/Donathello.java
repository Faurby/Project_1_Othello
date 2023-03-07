import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Implements the decideMove function from the IOthelloAI.
     * 
     * @param s GameState.
     * @return Position object with best move.
     */
    @Override
    public Position decideMove(GameState s) {
        if (weightedBoard == null) {
            weightedBoard = buildWeightedGameBoard(s.getBoard().length);
        }

        var p = minimax(s, 4, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println("Utility for best move is " + p.val2);
        return p.val1;
    }

    /**
     * Minimax algorithm with alpha beta pruning.
     * 
     * If max depth is reached, or there is no other legal moves, return utility of
     * GameState. Otherwise use the minimax algorithm with alpha beta pruning for
     * finding best move. Recursively calls it self.
     * 
     * @param s                GameState
     * @param depth            Desired depth to look at
     * @param maximizingPlayer Whether it is max's turn or min
     * @param alpha            Alpha value used for pruning
     * @param beta             Beta value used for pruning
     * @return Pair of position (best move) and utility (as a double)
     */
    public Pair<Position, Double> minimax(GameState s, int depth, boolean maximizingPlayer, double alpha, double beta) {
        if (depth == 0 || s.legalMoves().size() == 0) {
            return new Pair<>(new Position(-1, -1), getUtility(s));
        }

        Position bestMove = null;
        double value = 0.0;

        for (var possibleState : getChildGameStates(s)) {
            if (maximizingPlayer) {
                value = Double.MIN_VALUE;
                var p = minimax(possibleState.val1, depth - 1, false, alpha, beta);

                if (value < p.val2) {
                    value = p.val2;
                    bestMove = possibleState.val2;
                }

                if (value > beta) {
                    break;
                }

                alpha = Math.max(alpha, value);

            } else {
                value = Double.MAX_VALUE;
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

    /**
     * For all legal moves, create a GameState object after that move has been
     * inserted.
     * 
     * @param s GameState
     * @return List of all GameStates from all LegalMoves.
     */
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
     * Counts weighted tokens of the player 1 (black) and player 2 (white). Uses
     * method getWeightedTile to get correct weight of tile.
     * 
     * @param s GameState
     * @return double[] with player 1 (black) as [0] and player 2 (white) as [1].
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
        return new double[] {tokens1, tokens2};
    }

    /**
     * Build the "heatmap" of the game board. This is, where it is wise to put the
     * tiles. E.g. corners good, but tiles adjacent to corners bad.
     * 
     * @param size Size of the gameboard.
     * @return double[][] of the game board, where each position has a weight. E.g.
     *         [0][0] = 1
     */
    public double[][] buildWeightedGameBoard(int size) {
        double[][] weightedBoard = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Corners
                // 0
                // 0 0
                // x 0 0
                if ((i == 0 || i == size - 1) && (j == 0 || j == size - 1)) {
                    weightedBoard[i][j] = 1;

                    // Adjacent to corners (on edges).
                    // 0
                    // x 0
                    // 0 x 0
                } else if (((i == 1 || i == size - 2) && (j == 0 || j == size - 1))
                        || ((i == 0 || i == size - 1) && (j == 1 || j == size - 2))) {
                    weightedBoard[i][j] = -0.5;

                    // Diagonally adjacent to corners.
                    // 0
                    // 0 x
                    // 0 0 0
                } else if ((i == 1 || i == size - 2) && (j == 1 || j == size - 2)) {
                    weightedBoard[i][j] = -1;

                    // Edges except special cases above.
                    // x
                    // x
                    // 0
                    // 0 0 x x
                } else if (((j == 0 || j == size - 1) && (i > 2 && i < size - 3) || i == 0 || i == size - 1)
                        && (j > 2 && j < size - 3)) {
                    weightedBoard[i][j] = 0.8;

                    // Edges between diagonally adjacent corners
                    // 0 x
                    // 0 x
                    // 0 0 x x
                    // 0 0 0 0
                } else if (((j == 1 || j == size - 2) && (i > 1 && i < size - 3) || i == 1 || i == size - 2)
                        && (j > 1 && j < size - 3)) {
                    weightedBoard[i][j] = -0.5;

                    // Middle of board
                    // 0 0 x x
                    // 0 0 x x
                    // 0 0 0 0
                    // 0 0 0 0
                } else {
                    weightedBoard[i][j] = 0.3;
                }
            }
        }

        return weightedBoard;
    }

    private static double heuristicWeight = 0.5;

    public double getWeightedTile(int col, int row) {
        return weightedBoard[col][row];
    }

    /**
     * Calculate occupation numbers.
     * 
     * @param s GameState
     * @return returns occupied percentage, black occupation percentage, white
     *         occupation percentage, black tiles and white tiles.
     */
    public double[] getOccupiedPercentage(GameState s) {
        var tiles = s.countTokens();
        var black = tiles[0];
        var white = tiles[1];
        var occupied = white + black;
        var total = Math.pow(s.getBoard().length, 2);

        return new double[] {occupied / total, black / occupied, white / occupied, black, white};
    }

    /**
     * Calculate heuristic for GameState. The thought is to compare the heatmap
     * (position heuristic) for the player with the occupation heuristic. Occupation
     * heuristic is meant to signal when it is smart to flip tiles. Less flipped
     * tiles in start of the game is better.
     * 
     * @param s
     * @return returns the heuristic value of this GameState.
     */
    public double heuristic(GameState s) {
        var occupation = getOccupiedPercentage(s);
        var occupationHeuristic = -Math.abs(occupation[0] - occupation[s.getPlayerInTurn()]);
        // --- Does not work properly ----
        // var positionHeuristic = countWeightedTokens(s)[s.getPlayerInTurn()-1] /
        // (occupation[s.getPlayerInTurn()+2]);
        var positionHeuristic = countWeightedTokens(s)[s.getPlayerInTurn() - 1];
        return heuristicWeight * positionHeuristic + (1.0 - heuristicWeight) * occupationHeuristic;
    }

}
