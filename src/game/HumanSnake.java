package game;

import environment.Board;
import environment.BoardPosition;

/**
 * Class for a remote snake, controlled by a human
 * 
 * @author luismota
 *
 */
public class HumanSnake extends Snake {

	public HumanSnake(int id, Board board) {
		super(id, board);
	}

	@Override
	public void run() {

		System.err.println("initial size:" + cells.size());
		try {
			
            doInitialPositioning();
            Thread.sleep(2000);
            while (getBoard().getGoalValue() < 10) {

                try {
                  
                    move();
                    getBoard().setChanged();
                    

                    Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);

				} catch (Exception e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

	@Override
	public boolean isHumanSnake() {
		return true;
	}

}
