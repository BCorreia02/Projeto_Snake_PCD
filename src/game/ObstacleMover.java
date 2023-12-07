package game;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import environment.Board;
import environment.LocalBoard;

public class ObstacleMover extends Thread {

	private static LocalBoard board;
	private ExecutorService service;

	public ObstacleMover(LocalBoard board, int num) {
		super();
		this.board = board;
		service = Executors.newFixedThreadPool(num);
	}

	public ExecutorService getService() {
		return service;
	}

	public Board getBoard() {
		return board;
	}

	public static class Task implements Runnable {

		private Obstacle obstacle;

		public Task(Obstacle obstacle) {
			this.obstacle = obstacle;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(4000);
				while (!Thread.interrupted()) {
					obstacle.move();
					Thread.sleep(Obstacle.OBSTACLE_MOVE_INTERVAL);
				}
			} catch (InterruptedException e) {
				// Thread interrupted
			}
		}
	}
}
