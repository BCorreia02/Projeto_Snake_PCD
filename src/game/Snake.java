package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;

/**
 * 
 *
 * 
 * @author bcorreia02
 *
 */

public abstract class Snake extends Thread implements Serializable {

	private static final int DELTA_SIZE = 10;
	protected LinkedList<Cell> cells = new LinkedList<>();
	private transient int id;
	private transient Board board;
	protected transient int toIncrease = 0;
	private transient Cell future;

	public Snake(int id, Board board) {
		this.id = id;
		this.board = board;
	}

	public Cell getFuture() {
		return future;
	}

	public void setFuture(Cell cell) {
		future = cell;
	}

	public int getSize() {
		return cells.size();
	}

	public int getIdentification() {
		return id;
	}

	public LinkedList<Cell> getCells() {
		return cells;
	}

	public int getLength() {
		return cells.size();
	}

	public LinkedList<BoardPosition> getPath() {
		LinkedList<BoardPosition> path = new LinkedList<>();
		for (Cell cell : cells) {
			path.add(cell.getPosition());
		}
		return path;
	}

	protected void move() throws InterruptedException {
		if (future != null) {
			future.request(this);
			if (this instanceof HumanSnake)
				System.out.println("Passei o request");

			cells.addFirst(future);

			if (toIncrease == 0 || cells.size() == DELTA_SIZE) {
				Cell tail = cells.removeLast();
				tail.release();
			}

			if (toIncrease != 0)
				toIncrease--;

		}

	}

	public abstract boolean isHumanSnake();

	public BoardPosition getNewTargetedPos(boolean reset) {

		BoardPosition goalPosition = getBoard().getGoalPosition();
		Cell head = getCells().getFirst();

		BoardPosition closestPosition = null;

		List<BoardPosition> possibles = new ArrayList<>(getBoard().getNeighboringPositions(head));

		if (reset)
			possibles.removeIf(pos -> (board.getCell(pos)).isOcupied());

		double minDistance = Double.MAX_VALUE;
		for (BoardPosition pos : possibles) {
			double distance = pos.distanceTo(goalPosition);
			if ((distance < minDistance) && !(cells.contains(board.getCell(pos)))) {
				closestPosition = pos;
				minDistance = distance;
			}
		}

		return closestPosition;

	}

	public void consumeGoal(Cell cell) throws InterruptedException {

		int value = cell.getGoal().captureGoal();

		// Agora usa o valor retornado pelo captureGoal
		if (getCells().size() < DELTA_SIZE) {
			toIncrease += value;
		}
		board.removeGameElement(cell.getGameElement());
		cell.setGameElement(null);
		board.setChanged();

		if (board.shouldEndGame(value)) {
			board.endGame();
		}

	}

	// public boolean canMoveTo(LinkedList<Cell> c, Cell next) {
	// Cell head = c.getLast();

	// if(head.getPosition().x == 0 && next.getPosition() ==
	// head.getPosition().getCellLeft())
	// return false;
	// if(head.getPosition().x == board.NUM_COLUMNS-1 && next.getPosition() ==
	// head.getPosition().getCellRight())
	// return false;
	// if(head.getPosition().y == 0 && next.getPosition() ==
	// head.getPosition().getCellAbove())
	// return false;
	// if(head.getPosition().y == board.NUM_ROWS-1 && next.getPosition() ==
	// head.getPosition().getCellBelow())
	// return false;
	// return true;
	// }

	protected void doInitialPositioning() throws InterruptedException {
		// Random position on the first column, ensure it's unoccupied
		int posY;
		Cell start;
		do {
			posY = (int) (Math.random() * Board.NUM_ROWS);
			start = board.getCell(new BoardPosition(0, posY));
		} while (start.isOcupied());

		start.request(this);
		cells.add(start);
		getBoard().setChanged();
		System.err.println("Snake " + getIdentification() + " starting at:" + start.getPosition());
	}

	public Board getBoard() {
		return board;
	}

}