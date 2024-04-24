package game;

import java.io.Serializable;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import environment.Board;
import environment.LocalBoard;

/**
 * 
 *
 * 
 * @author bcorreia02
 *
 */

public class ObstacleMover extends Thread implements Serializable {

	private transient static LocalBoard board;
	private transient ExecutorService service;

	public ObstacleMover(LocalBoard board, int num) {
		super();
		ObstacleMover.board = board;
		service = Executors.newFixedThreadPool(num);
	}

	public ExecutorService getService() {
		return service;
	}

	public static Board getBoard() {
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
				while (obstacle.getRemainingMoves() != 0) {
					obstacle.move();
					Thread.sleep(Obstacle.OBSTACLE_MOVE_INTERVAL);
				}

				try {
					System.out.println(board.getBarrier());
					board.getBarrier().await();
				} catch (InterruptedException | BrokenBarrierException e) {
				}

			} catch (InterruptedException e) {
				// Thread interrupted
			} catch (BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
