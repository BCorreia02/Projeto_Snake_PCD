package environment;

import game.Obstacle;
import game.ObstacleMover;
import game.Snake;
import game.Killer;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

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
	private static final int NUM_OBSTACLES = 5;
	private static final int NUM_SIMULTANEOUS_MOVING_OBSTACLES = 3;
	private static final int BARRIER_PARTIES = 3; // Número de obstáculos que devem completar antes de adicionar um Killer
	private CyclicBarrier barrier;
	private AtomicInteger activeObstacles; // Contador de obstáculos ativos

	public LocalBoard() {
		activeObstacles = new AtomicInteger(NUM_OBSTACLES);

		for (int i = 0; i < NUM_SNAKES; i++) {
			AutomaticSnake snake = new AutomaticSnake(i, this);
			snakes.add(snake);
		}

		this.barrier = new CyclicBarrier(BARRIER_PARTIES, new BarrierAction());

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
		Killer killer = new Killer(this);
		getCell(getNewObstaclePos()).setGameElement(killer);
		setChanged();
	}

	private BoardPosition getNewObstaclePos() {
		BoardPosition possible = getRandomPosition();
		while (getCell(possible).isOcupied() || getCell(possible).isOcupiedByGoal()) {
			possible = getRandomPosition();
		}
		return possible;
	}

	public void decrementActiveObstacles() {
		activeObstacles.decrementAndGet();
	}

	class BarrierAction implements Runnable {
		@Override
		public void run() {
			if (activeObstacles.get() > 0) {
				addKiller();
				System.out.println("CRIEI UM KILLER");
			}
		}
	}
}