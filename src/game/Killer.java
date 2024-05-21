package game;

import environment.Board;
import environment.BoardPosition;

public class Killer extends GameElement {
    private Board board;
	private BoardPosition current;

    public Killer(Board board){
        this.board=board;
    }

    public Board getBoard() {
        return board;
    }

    public void setCurrent(BoardPosition pos) {
		this.current = pos;
	}

	public BoardPosition getCurrent() {
		return current;
	}

}