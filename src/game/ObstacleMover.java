package game;

import java.io.Serializable;
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

	private transient Board board;
	private transient ExecutorService service;

	public ObstacleMover(Board board, int num) {
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
					System.out.println("movi");
				}
			} catch (InterruptedException e) {
				// Thread interrupted
			}
		}
	}

	@Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
				System.out.println("entrei");
				this.board.getBarrier().await();
				System.out.println("passei");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
