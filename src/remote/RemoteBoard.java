package remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;

import environment.LocalBoard;
import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import environment.CellContent;
import game.AutomaticSnake;
import game.Goal;
import game.Obstacle;
import game.ObstacleMover;
import game.Snake;

/**
 * Remote representation of the game, no local threads involved.
 * Game state will be changed when updated info is received from Srver.
 * Only for part II of the project.
 * 
 * @author luismota
 *
 */
public class RemoteBoard extends Board implements Serializable {

	private JFrame frame = new JFrame("cliente");
	private BoardComponentClient boardComponentClient;
	private static final int NUM_SNAKES = 0;
	private static final int NUM_OBSTACLES = 10;
	private static final int NUM_SIMULTANEOUS_MOVING_OBSTACLES = 5;

	public RemoteBoard(BoardComponentClient boardComponentClient, boolean cliente) {
		this.boardComponentClient = boardComponentClient;
		buildGui();

		// if (!cliente) {
		// 	for (int i = 0; i < NUM_SNAKES; i++) {
		// 		AutomaticSnake snake = new AutomaticSnake(i, this);
		// 		snakes.add(snake);
		// 	}

		// 	addObstacles(NUM_OBSTACLES);

		// 	obstacleMover = new ObstacleMover(this, NUM_SIMULTANEOUS_MOVING_OBSTACLES);

		// 	for (Obstacle obstacle : obstacles) { // Assuming 'obstacles' is a List of Obstacle
		// 		obstacleMover.getService().execute(new ObstacleMover.Task(obstacle));
		// 	}
		// }

		// addGoal();
	}

	private void buildGui() {

		frame.add(boardComponentClient);

		frame.setSize(800, 800);
		frame.setLocation(0, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	@Override
	public void init() { // startar RemoteBoard
		// for (Snake s : snakes)
		// 	s.start();

		// obstacleMover.start();
		setChanged();
	}

	// public Obstacle getObstacle(Obstacle obstacle) {
	// 	for (Obstacle a : obstacles)
	// 		if (obstacle.equals(a))
	// 			return a;
	// 	return null;
	// }

	@Override
	public void handleKeyPress(int keyCode) {
		// TODO - movimento do HumanSnake
	}

	@Override
	public void handleKeyRelease() {
		// TODO - idk
	}

	public BoardComponentClient getBoardClient() {
		return boardComponentClient;
	}

	public void atualiza(ConcurrentHashMap<BoardPosition, CellContent> mapa) {
		this.boardComponentClient.setNewMap(mapa);

	}

}
