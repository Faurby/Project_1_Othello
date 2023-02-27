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
        return minimax(s, 5, true).val1;
    }

    public Pair<Position, Integer> minimax(GameState s, int depth, boolean maximizingPlayer) {
        System.out.println("Calling minimax, looking at depth: " + depth);
        if (depth == 0 || s.legalMoves().size() == 0) {
            return new Pair<>(null, getUtility(s));
        }
        
        Position bestMove = null;
        int value;

        if (maximizingPlayer) {
            value = Integer.MIN_VALUE;
            for (var possibleState : getChildGameStates(s)) {
                var p = minimax(possibleState.val1, depth-1, false);

                if (value < p.val2) {
                    value = p.val2;
                    bestMove = possibleState.val2;
                }
            }
        } else {
            value = Integer.MAX_VALUE;
            for (var possibleState : getChildGameStates(s)) {
                var p = minimax(possibleState.val1, depth-1, true);

                if (value > p.val2) {
                    value = p.val2;
                    bestMove = possibleState.val2;
                }
            }
        }

        return new Pair<>(bestMove, value);
    }

    // Returns the gamestates for alle legal moves in the provided gamestate
    public List<Pair<GameState, Position>> getChildGameStates(GameState s) {
        return s.legalMoves().stream().map(move -> {
            var gameState = new GameState(s.getBoard(), s.getPlayerInTurn());
            return new Pair<>(gameState, move);
        }).collect(Collectors.toList());
    }

    public int getUtility(GameState s) {
        return s.countTokens()[s.getPlayerInTurn() - 1];
    }
}
