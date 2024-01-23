package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;

import environment.Board;
import environment.LocalBoard;
import game.AutomaticSnake;
import game.Snake;

/**
 * 
 *
 * 
 * @author bcorreia02
 *
 */
public class SnakeGui extends JFrame implements Observer {
	public static final int BOARD_WIDTH = 800;
	public static final int BOARD_HEIGHT = 800;
	public static final int NUM_COLUMNS = 40;
	public static final int NUM_ROWS = 30;
	private JFrame frame;
	private BoardComponent boardGui;
	private Board board;
	private boolean isRemote;

	public SnakeGui(Board board, int x, int y, boolean a) {
		super();
		if (board == null) {
			throw new IllegalArgumentException("Board cannot be null");
		}
		isRemote = a;
		this.board = board;
		frame = new JFrame("The Snake Game: " + (board instanceof LocalBoard ? "Local" : "Remote"));
		frame.setLocation(x, y);
		buildGui();
	}

	public BoardComponent getBoardComponent() {
		return boardGui;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public void showGui() {
		frame.setVisible(true);
	}

	private void buildGui() {
		frame.setLayout(new BorderLayout());

		boardGui = new BoardComponent(board, isRemote);
		boardGui.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
		frame.add(boardGui, BorderLayout.CENTER);
		if (board instanceof LocalBoard) {
			JButton resetObstaclesButton = new JButton("Reset snakes' directions");
			resetObstaclesButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					for (Snake snake : board.getSnakes()) {
						if (snake instanceof AutomaticSnake)
							snake.interrupt();
					}
				}

			});
			frame.add(resetObstaclesButton, BorderLayout.SOUTH);
		}

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void init() {
		if (board == null) {
			throw new IllegalStateException("Board is not initialized");
		}
		frame.setVisible(true);
		board.addObserver(this);
		board.init();
	}

	public void run() {
		init();
	}

	@Override
	public void update(Observable o, Object arg) {
		boardGui.repaint();
	}
}
