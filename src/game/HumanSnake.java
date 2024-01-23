package game;

import environment.Board;
import environment.BoardPosition;

/**
 * 
 *
 * 
 * @author bcorreia02
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

    @Override
    public boolean isHumanSnake() {
        return true;
    }

}