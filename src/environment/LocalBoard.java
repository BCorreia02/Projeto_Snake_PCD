package environment;

import game.Obstacle;
import game.ObstacleMover;
import game.Snake;

import java.util.LinkedList;

import game.AutomaticSnake;

/**
 * Class representing the state of a game running locally
 * 
 * @author luismota
 *
 */
public class LocalBoard extends Board {

	private static final int NUM_SNAKES = 2;
	private static final int NUM_OBSTACLES = 10;
	private static final int NUM_SIMULTANEOUS_MOVING_OBSTACLES = 5;

	public LocalBoard() {

		for (int i = 0; i < NUM_SNAKES; i++) {
			AutomaticSnake snake = new AutomaticSnake(i, this);
			snakes.add(snake);
		}

		addObstacles(NUM_OBSTACLES);

		obstacleMover = new ObstacleMover(this, NUM_SIMULTANEOUS_MOVING_OBSTACLES);

		for (Obstacle obstacle : obstacles) { // Assuming 'obstacles' is a List of Obstacle
			obstacleMover.getService().execute(new ObstacleMover.Task(obstacle));
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

	public Obstacle getObstacle(Obstacle obstacle) {
		for (Obstacle a : obstacles)
			if (obstacle.equals(a))
				return a;
		return null;
	}

}
