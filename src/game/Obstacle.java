package game;

import java.util.concurrent.BrokenBarrierException;

import environment.Board;
import environment.BoardPosition;
import environment.LocalBoard;

/**
 * 
 *
 * 
 * @author bcorreia02
 *
 */

/**
 * 
 *
 * 
 * @author bcorreia02
 *
 */

 public class Obstacle extends GameElement {

	private static final int NUM_MOVES = 2;
	static final int OBSTACLE_MOVE_INTERVAL = 4000;
	private int remainingMoves = NUM_MOVES;
	private Board board;
	private BoardPosition current;

	public Obstacle(Board board) {
		super();
		this.board = board;
	}

	public Board getBoard() {
		return board;
	}

	public void setCurrent(BoardPosition pos) {
		this.current = pos;
	}

	public BoardPosition getCurrent() {
		return current;
	}

	public int getRemainingMoves() {
		return remainingMoves;
	}

	private BoardPosition getNewObstaclePos() {

		BoardPosition possible = board.getRandomPosition();
		if (board.getCell(possible).isOcupied() || board.getCell(possible).isOcupiedByGoal())
			return getNewObstaclePos();

		return possible;
	}

	public void move() throws InterruptedException, BrokenBarrierException {

		BoardPosition future = getNewObstaclePos();

		board.getCell(current).removeObstacle();
		board.getCell(future).setGameElement(this);
		board.setChanged();
		this.current = future;
		remainingMoves--;

		if (remainingMoves == 0) {
			// Esperar na barreira quando os movimentos acabarem
			((LocalBoard) board).getBarrier().await();
			((LocalBoard) board).decrementActiveObstacles(); // Decrementa o contador de obstáculos ativos
		}
	}
}
