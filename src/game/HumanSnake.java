package game;

import environment.Board;

/**
 * Class for a remote snake, controlled by a human
 * 
 * @author luismota
 *
 */
public abstract class HumanSnake extends Snake {

	public HumanSnake(int id, Board board) {
		super(id, board);
	}

	@Override
	public void run() {

		System.err.println("initial size:" + cells.size());
		try {
			doInitialPositioning();
			cells.getLast().request(this);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
