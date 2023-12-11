package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;

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
			getNewObstaclePos();

		return possible;
	}

	public void move() throws InterruptedException {
		if (remainingMoves != 0) {
			BoardPosition future = getNewObstaclePos();

			// System.out.println(
			// "OBS: " + this.toString() + "Current: " + current.toString() + "Future: " +
			// future.toString());

			board.getCell(current).removeObstacle();
			board.getCell(future).setGameElement(this);
			board.setChanged();
			this.current = future;
			remainingMoves--;

			
		}
	}
}
