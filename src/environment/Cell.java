package environment;

import java.io.Serializable;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.midi.SysexMessage;

import game.GameElement;
import game.Goal;
import game.Obstacle;
import game.Snake;

/**
 * Main class for game representation.
 * 
 * @author luismota
 *
 */
public class Cell {

	private Lock lock = new ReentrantLock();
	private Condition cellOccupied = lock.newCondition();
	private Condition cellEmpty = lock.newCondition();
	private BoardPosition position;
	private Snake ocuppyingSnake = null;
	private GameElement gameElement = null;
	private Boolean blocked = false;

	public GameElement getGameElement() {
		return gameElement;
	}

	public void unblock() {
		blocked = false;
	}

	public void block() {
		blocked = true;
	}

	public boolean isBlocked() {
		return blocked;
	}

	@Override
	public String toString() {
		return "Cell " + position.toString();
	}

	public Cell(BoardPosition position) {
		super();
		this.position = position;
	}

	public BoardPosition getPosition() {
		return position;
	}

	public void request(Snake snake) throws InterruptedException {
		lock.lock();
		try {

			/*
			 * if (gameElement instanceof Obstacle && ((Obstacle) gameElement).isMovable())
			 * {
			 * block();
			 * }
			 */

			while (this.isOcupied()) {
				cellEmpty.await();
			}

			block();
			cellOccupied.signalAll();

			if (snake != null) {
				ocuppyingSnake = snake;
				if (this.isOcupiedByGoal())
					snake.consumeGoal(this);
			}

		} catch (InterruptedException e) {
			cellEmpty.signalAll();
			throw e;
		} finally {
			lock.unlock();
			unblock();
		}
	}

	public void release() throws InterruptedException {
		lock.lock();
		try {
			block();
			ocuppyingSnake = null;
			if (gameElement instanceof Obstacle)
				gameElement = null;
			cellEmpty.signalAll();
		} finally {
			lock.unlock();
			unblock();
		}
	}

	// metodo para mover obstaculos

	public boolean isOcupiedBySnake() {
		return ocuppyingSnake != null;
	}

	public void setGameElement(GameElement element) {
		lock.lock();
		try {
			gameElement = element;
		} finally {
			lock.unlock();
		}

	}

	public boolean isOcupied() {
		return isOcupiedBySnake() || (gameElement != null && gameElement instanceof Obstacle);
	}

	public Snake getOcuppyingSnake() {
		return ocuppyingSnake;
	}

	public void setOcuppyingSnake(Snake snake) {
		ocuppyingSnake = snake;
	}

	public Goal removeGoal() {
		if (this.gameElement instanceof Goal) {
			Goal goal = (Goal) this.gameElement;
			this.gameElement = null; // Remove o prêmio da célula.
			return goal;
		}
		return null;
	}

	public void removeObstacle() throws InterruptedException {
		if (gameElement instanceof Obstacle) {
			this.release();
			gameElement = null;
		}
	}

	public Goal getGoal() {
		return (Goal) gameElement;
	}

	public boolean isOcupiedByGoal() {
		return (gameElement != null && gameElement instanceof Goal);
	}

}