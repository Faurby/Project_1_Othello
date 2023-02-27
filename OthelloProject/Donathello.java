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
        System.out.println("The turn is ours");
        System.out.println("Utility for current state: " + getUtility(s));

        var p = minimax(s, 7, true, Integer.MIN_VALUE, Integer.MAX_VALUE);

        System.out.println("\n\n=========");
        System.out.println("Best move has been selected as " + p.val1.col + " " + p.val1.row);
        System.out.println("Utility for best move is " + p.val2);
        return p.val1;
    }

    public Pair<Position, Integer> minimax(GameState s, int depth, boolean maximizingPlayer, int alpha, int beta) {
        if (depth == 0 || s.legalMoves().size() == 0) {
            System.out.println("depth at: " + depth + "\nUtility is: " + getUtility(s));
            return new Pair<>(null, getUtility(s));
        }

        Position bestMove = null;
        int value;

        if (maximizingPlayer) {
            value = Integer.MIN_VALUE;
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
            value = Integer.MAX_VALUE;
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

    public int getUtility(GameState s) {
        System.out.println("---- tokens on board ----");
        System.out.println("Black tokens: " + s.countTokens()[0]);
        System.out.println("White tokens: " + s.countTokens()[1]);
        System.out.println("---- END  ----");

        return s.countTokens()[s.getPlayerInTurn() - 1];
    }
}
