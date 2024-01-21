package game;

import java.awt.event.KeyEvent;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;

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
        try {
            boolean reset = false;
            doInitialPositioning();
            Thread.sleep(2000);
            while (getBoard().getGoalValue() < 10) {

                try {

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

    public void setDirection(String c) {
        BoardPosition newHeadPosition = null;
        switch (c) {
            case "UP":
                newHeadPosition = new BoardPosition(this.getCells().getFirst().getPosition().getCellAbove().getX(),
                        this.getCells().getFirst().getPosition().getCellAbove().getY());
                // this.setFuture(this.getBoard().getCell(above));
                break;
            case "DOWN":
                newHeadPosition = new BoardPosition(this.getCells().getFirst().getPosition().getCellBelow().getX(),
                        this.getCells().getFirst().getPosition().getCellBelow().getY());
                // this.setFuture(getBoard().getCell(below));
                break;
            case "LEFT":
                newHeadPosition = new BoardPosition(this.getCells().getFirst().getPosition().getCellLeft().getX(),
                        this.getCells().getFirst().getPosition().getCellLeft().getY());
                // this.setFuture(getBoard().getCell(left));
                break;
            case "RIGHT":
                newHeadPosition = new BoardPosition(this.getCells().getFirst().getPosition().getCellRight().getX(),
                        this.getCells().getFirst().getPosition().getCellRight().getY());
                // this.setFuture(getBoard().getCell(right));
                break;
        }

        if (newHeadPosition != null && isMoveValid(newHeadPosition)) {
            // Update the snake's position with the new head position
            this.setFuture(this.getBoard().getCell(newHeadPosition));
        }

    }

    private boolean isMoveValid(BoardPosition newPosition) {
        // Check bounds
        if (newPosition.getX() < 0 || newPosition.getX() >= Board.NUM_COLUMNS ||
                newPosition.getY() < 0 || newPosition.getY() >= Board.NUM_ROWS) {
            return false; // New position is out of bounds
        }

        // Check collision with snake's body
        for (Cell bodyPart : this.getCells()) {
            if (newPosition.equals(bodyPart.getPosition())) {
                return false;
            }
        }

        if (this.getBoard().isObstacleAt(newPosition)) {
            return false;
        }

        if (this.getBoard().isSnakeAt(newPosition)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isHumanSnake() {
        return true;
    }

}