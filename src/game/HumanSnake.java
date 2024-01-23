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

    public void setDirection(String c) {
        BoardPosition newHeadPosition = null;
        switch (c) {
            case "UP":
                newHeadPosition = new BoardPosition(this.getCells().getFirst().getPosition().getCellAbove().getX(),
                        this.getCells().getFirst().getPosition().getCellAbove().getY());
                break;
            case "DOWN":
                newHeadPosition = new BoardPosition(this.getCells().getFirst().getPosition().getCellBelow().getX(),
                        this.getCells().getFirst().getPosition().getCellBelow().getY());
                break;
            case "LEFT":
                newHeadPosition = new BoardPosition(this.getCells().getFirst().getPosition().getCellLeft().getX(),
                        this.getCells().getFirst().getPosition().getCellLeft().getY());
                break;
            case "RIGHT":
                newHeadPosition = new BoardPosition(this.getCells().getFirst().getPosition().getCellRight().getX(),
                        this.getCells().getFirst().getPosition().getCellRight().getY());
                break;
        }

        // Update the snake's position with the new head position
        this.setFuture(this.getBoard().getCell(newHeadPosition));

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