package remote;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import environment.BoardPosition;
import environment.CellContent;
import game.Snake;

public class Mensagem implements Serializable {
    private LinkedList<Snake> snakeList;
    ConcurrentHashMap<BoardPosition, CellContent> map;

    public Mensagem(ConcurrentHashMap<BoardPosition, CellContent> map, LinkedList<Snake> snakeList) {
        this.snakeList = snakeList;
        this.map = map;
    }

    public LinkedList<Snake> getSnakeList() {
        return snakeList;
    }

    public void setSnakeList(LinkedList<Snake> snakeList) {
        this.snakeList = snakeList;
    }

    public ConcurrentHashMap<BoardPosition, CellContent> getMap() {
        return map;
    }

    public void setMap(ConcurrentHashMap<BoardPosition, CellContent> map) {
        this.map = map;
    }

}