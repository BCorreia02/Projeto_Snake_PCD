package game;

import java.io.Serializable;

import environment.Board;
import environment.BoardPosition;

public class Goal extends GameElement implements Serializable {
	private int value = 1;
	private Board board;
	private BoardPosition pos;

	public static final int MAX_VALUE = 10;

	public Goal(Board board) {
		this.board = board;
		this.value = board.getGoalValue();
	}

	public int getValue() {
		return value;
	}

	public void incrementValue() throws InterruptedException {
		if (board.getGoalValue() < 9)
			board.setGoalValue(1);
	}

	public int captureGoal() throws InterruptedException {
		incrementValue();
		if (this.value > MAX_VALUE) {
			this.value = 1; // Reset para 1 após atingir o valor máximo
		}
		board.addGameElement(new Goal(board)); // Adiciona um novo prêmio
		return this.value;
	}

}