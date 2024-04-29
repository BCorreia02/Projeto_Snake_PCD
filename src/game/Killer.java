package game;

import environment.Board;
import environment.BoardPosition;

public class Killer extends GameElement {

    private BoardPosition boardpos;

    public Killer(BoardPosition boardpos){
        this.boardpos=boardpos;
    }

    public BoardPosition getBoard() {
        return boardpos;
    }

}