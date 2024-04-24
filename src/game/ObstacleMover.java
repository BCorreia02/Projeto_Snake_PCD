package game;

import java.io.Serializable;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import environment.Board;

/**
 * 
 *
 * 
 * @author bcorreia02
 *
 */

public class ObstacleMover extends Thread implements Serializable {

	private transient static Board board;
	CyclicBarrier barrier;
	private transient ExecutorService service;

	public ObstacleMover(Board board, int num, CyclicBarrier barrier) {
		super();
		ObstacleMover.board = board;
		service = Executors.newFixedThreadPool(num);
		this.barrier = barrier;
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
				while (!Thread.interrupted()) {
					if (obstacle.getRemainingMoves() != 0) {
						obstacle.move();
					} else {
						try {
							System.out.println(board);
							board.getBarrier().await();
						} catch (InterruptedException | BrokenBarrierException e) {
						}
					}

					Thread.sleep(Obstacle.OBSTACLE_MOVE_INTERVAL);
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
