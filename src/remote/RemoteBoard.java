package remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import environment.LocalBoard;
import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import game.Goal;
import game.Obstacle;
import game.Snake;

/**
 * Remote representation of the game, no local threads involved.
 * Game state will be changed when updated info is received from Srver.
 * Only for part II of the project.
 * 
 * @author luismota
 *
 */
public class RemoteBoard extends Board {

	private BoardComponentClient boardComponentClient;

	public RemoteBoard(BoardComponentClient boardComponentClient) {
		this.boardComponentClient = boardComponentClient;
	}

	@Override
	public void handleKeyPress(int keyCode) {
		// TODO
	}

	@Override
	public void handleKeyRelease() {
		// TODO
	}

	@Override
	public void init() {
		// TODO
	}

	public BoardComponentClient getBoardClient() {
		return boardComponentClient;
	}

	public void atualiza(List<Pacote> recebido) {
	}

}
