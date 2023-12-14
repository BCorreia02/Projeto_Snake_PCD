package remote;

import java.util.concurrent.ConcurrentHashMap;
import environment.Board;
import environment.BoardPosition;
import environment.CellContent;
import java.awt.event.KeyEvent;

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

	public RemoteBoard(BoardComponentClient boardComponentClient, boolean cliente) {
		this.boardComponentClient = boardComponentClient;
	}

	@Override
	public void init() {
		setChanged();
	}

	@Override
	public void handleKeyPress(int keyCode) {
		if (boardComponentClient == null) {
			throw new IllegalStateException("BoardComponentClient is not initialized");
		}
		String direction = null;
		switch (keyCode) {
			case KeyEvent.VK_LEFT:
				direction = "LEFT";
				break;
			case KeyEvent.VK_RIGHT:
				direction = "RIGHT";
				break;
			case KeyEvent.VK_UP:
				direction = "UP";
				break;
			case KeyEvent.VK_DOWN:
				direction = "DOWN";
				break;
		}
		if (direction != null) {
			boardComponentClient.setLastPressedDirection(direction);
			// Optionally, you can send the direction to the server here
			// Or you might have another method to periodically send the direction to the
			// server
		}
	}

	@Override
	public void handleKeyRelease() {
		if (boardComponentClient == null) {
			throw new IllegalStateException("BoardComponentClient is not initialized");
		}
		boardComponentClient.setLastPressedDirection(null);
		// If you need to notify the server about the key release, you can do it here
	}

	public BoardComponentClient getBoardComponentClient() {
		return boardComponentClient;
	}

	public void atualiza(ConcurrentHashMap<BoardPosition, CellContent> mapa) {
		this.boardComponentClient.setNewMap(mapa);

	}

}
