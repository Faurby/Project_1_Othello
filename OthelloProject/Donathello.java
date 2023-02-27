import java.util.ArrayList;
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

    @Override
    public Position decideMove(GameState s) {
        var p = minimax(s, 4, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println("Utility for best move is " + p.val2);
        return p.val1;
    }

    public Pair<Position, Double> minimax(GameState s, int depth, boolean maximizingPlayer, double alpha, double beta) {
        if (depth == 0 || s.legalMoves().size() == 0) {
            return new Pair<>(null, getUtility(s));
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
        return countWeightedTokens(s)[s.getPlayerInTurn() - 1];
    }

    /**
	 * Counts weighted tokens of the player 1 (black) and player 2 (white), respectively, and returns an array
	 * with the numbers in that order.
	 */
	public double[] countWeightedTokens(GameState s){
    	double tokens1 = 0;
    	double tokens2 = 0;
        int size = s.getBoard().length;
        int[][] board = s.getBoard();
    	for (int i = 0; i < size; i++){
    		for (int j = 0; j < size; j++){
    			if ( board[i][j] == 1 )
    				tokens1 += fieldHeuristic(i, j, size);
    			else if ( board[i][j] == 2 )
    				tokens2 += fieldHeuristic(i, j, size);
    		}
    	}
    	return new double[]{tokens1, tokens2};
	}


    private static double EDGE_MULTIPLIER = 0.3;

    public double fieldHeuristic(int col, int row, int size){
        return fieldHeuristic(col, size)
            + fieldHeuristic(row, size);
    }
    
    public double fieldHeuristic(int pos, int size){
        var r = Math.pow(1+(pos-(size/2.0)), 2) * EDGE_MULTIPLIER;
        System.out.println("H is: " + r);
        return r;
    }
}
