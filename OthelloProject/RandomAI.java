import java.util.ArrayList;
import java.util.Random;

/**
 * A simple OthelloAI-implementation. The method to decide the next move just
 * returns a random legal move that it finds.
 *
 * @author Mai Ajspur
 * @version 9.2.2018
 */
public class RandomAI implements IOthelloAI {

	/**
	 * Returns a random legal move
	 */
	public Position decideMove(GameState s) {
		ArrayList<Position> moves = s.legalMoves();
		if (!moves.isEmpty()) {

			Random ran = new Random();
			int x = ran.nextInt(moves.size());
			return moves.get(x);
		} else
			return new Position(-1, -1);
	}

}
