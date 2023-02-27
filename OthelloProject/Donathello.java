import java.util.ArrayList;

public class Donathello implements IOthelloAI {


    @Override
    public Position decideMove(GameState s) {
        var tileColor = s.getPlayerInTurn();
        var legalMoves = s.legalMoves();

        var maxTokens = 0;
        Position bestMove = null;

        for (var move : legalMoves) {
            var gameState = new GameState(s.getBoard(), s.getPlayerInTurn());
            gameState.insertToken(move);

            var tokens = gameState.countTokens();
            if (tokens[tileColor - 1] > maxTokens) {
                maxTokens = tokens[tileColor-1];
                bestMove = move;
            }
        }

        return bestMove;
    }
}
