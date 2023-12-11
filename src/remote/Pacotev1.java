package remote;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

import environment.BoardPosition;
import environment.CellContent;
import game.Obstacle;

public class Pacotev1 implements Serializable {

    private HashMap<BoardPosition, CellContent> boardMap;

    public Pacotev1(HashMap<BoardPosition, CellContent> boardMap) {
        this.boardMap = boardMap;
    }

    public HashMap<BoardPosition, CellContent> getBoardMap() {
        return boardMap;
    }

    public BoardPosition getGoalPosition() {
        return null;
    }

    public LinkedList<Obstacle> getObstacles() {
        return null;
    }

}
