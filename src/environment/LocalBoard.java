package environment;

import game.Obstacle;
import game.ObstacleMover;
import game.Snake;
import game.Killer;
import java.util.concurrent.CyclicBarrier;

import game.AutomaticSnake;

/**
 * 
 *
 * 
 * @author bcorreia02
 *
 */
public class LocalBoard extends Board {

	private static final int NUM_SNAKES = 2;
	private static final int NUM_OBSTACLES = 10;
	private static final int NUM_SIMULTANEOUS_MOVING_OBSTACLES = 5;
	private CyclicBarrier barrier;

	public LocalBoard() {

		for (int i = 0; i < NUM_SNAKES; i++) {
			AutomaticSnake snake = new AutomaticSnake(i, this);
			snakes.add(snake);
		}

		this.barrier = new CyclicBarrier(3, new BarrierAction());

		addObstacles(NUM_OBSTACLES);

		obstacleMover = new ObstacleMover(this, NUM_SIMULTANEOUS_MOVING_OBSTACLES);
		for (Obstacle obstacle : obstacles) {
			obstacleMover.getService().submit(new ObstacleMover.Task(obstacle));
		}
		addGoal();
	}

	public void addPlayer(Snake a) {
		snakes.add(a);
		setChanged();
	}

	public void init() {
		for (Snake s : snakes)
			s.start();

		obstacleMover.start();
		setChanged();
	}

	@Override
	public void handleKeyPress(int keyCode) {
		// do nothing... No keys relevant in local game
	}

	@Override
	public void handleKeyRelease() {
		// do nothing... No keys relevant in local game
	}

	public CyclicBarrier getBarrier() {
		return barrier;
	}

	public Obstacle getObstacle(Obstacle obstacle) {
		for (Obstacle a : obstacles)
			if (obstacle.equals(a))
				return a;
		return null;
	}

	public void addKiller() {
		Killer killer = new Killer();
		getCell(getNewObstaclePos()).setGameElement(killer);
		setChanged();
	}

	private BoardPosition getNewObstaclePos() {

		BoardPosition possible = getRandomPosition();
		if (getCell(possible).isOcupied() || getCell(possible).isOcupiedByGoal())
			getNewObstaclePos();

		return possible;
	}

	private class BarrierAction implements Runnable {
		@Override
		public void run() {
			addKiller();
			System.out.println("CRIEI UM KILLER");

		}
	}

}
