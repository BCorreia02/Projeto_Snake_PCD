package game;

import environment.LocalBoard;
import environment.Board;
import environment.BoardPosition;

public class AutomaticSnake extends Snake {

    public AutomaticSnake(int id, LocalBoard board) {
        super(id, board);
    }

    @Override
    public void run() {

        try {

            boolean reset = false;
            doInitialPositioning();
            Thread.sleep(2000);
            while (getBoard().getGoalValue() < 10) {

                try {
                    BoardPosition nextPosition = getNewTargetedPos(reset);
                    setFuture(getBoard().getCell(nextPosition));
                    move();
                    getBoard().setChanged();
                    reset = false;

                    Thread.sleep(Board.PLAYER_PLAY_INTERVAL);

                } catch (InterruptedException e) {
                    reset = true;
                    continue;
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
}