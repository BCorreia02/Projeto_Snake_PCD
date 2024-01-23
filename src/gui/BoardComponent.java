package gui;

import environment.LocalBoard;
import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import environment.CellContent;
import game.GameElement;
import game.Goal;
import game.HumanSnake;
import game.Obstacle;
import game.Snake;
import remote.RemoteBoard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * 
 *
 * 
 * @author bcorreia02
 *
 */
public class BoardComponent extends JComponent implements KeyListener {

	private Board board;
	private Image obstacleImage;
	private ConcurrentHashMap<BoardPosition, CellContent> boardMap;
	private boolean isRemote;
	private String lastDirection;

	public BoardComponent(Board board, boolean a) {
		this.board = board;
		this.isRemote = a;
		obstacleImage = new ImageIcon(getClass().getResource("/obstacle.png")).getImage();
		// Necessary for key listener
		setFocusable(true);
		addKeyListener(this);
	}

	public void setNewMap(ConcurrentHashMap<BoardPosition, CellContent> mapa, LinkedList<Snake> snakes) {
		this.boardMap = mapa;

		/*
		 * for (Snake s : snakes) {
		 * for (Cell c : s.getCells()) {
		 * System.out.println(s + "" + c);
		 * }
		 * }
		 */
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (!isRemote) {
			super.paintComponent(g);
			final double CELL_WIDTH = getHeight() / (double) SnakeGui.NUM_ROWS;
			// System.err.println("W:"+getWidth()+" H:"+getHeight());
			for (int x = 0; x < LocalBoard.NUM_COLUMNS; x++) {
				for (int y = 0; y < LocalBoard.NUM_ROWS; y++) {
					Cell cell = board.getCell(new BoardPosition(x, y));
					Image image = null;
					if (cell.getGameElement() != null)
						if (cell.getGameElement() instanceof Obstacle) {
							Obstacle obstacle = (Obstacle) cell.getGameElement();
							image = obstacleImage;
							g.setColor(Color.BLACK);
							g.drawImage(image, (int) Math.round(cell.getPosition().x * CELL_WIDTH),
									(int) Math.round(cell.getPosition().y * CELL_WIDTH),
									(int) Math.round(CELL_WIDTH), (int) Math.round(CELL_WIDTH), null);
							// write number of remaining moves
							g.setColor(Color.WHITE);
							g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) CELL_WIDTH));
							g.drawString(obstacle.getRemainingMoves() + "",
									(int) Math.round((cell.getPosition().x + 0.15) * CELL_WIDTH),
									(int) Math.round((cell.getPosition().y + 0.9) * CELL_WIDTH));
						} else if (cell.getGameElement() instanceof Goal) {
							Goal goal = (Goal) cell.getGameElement();
							g.setColor(Color.RED);
							g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) CELL_WIDTH));
							g.drawString(goal.getValue() + "",
									(int) Math.round((cell.getPosition().x + 0.15) * CELL_WIDTH),
									(int) Math.round((cell.getPosition().y + 0.9) * CELL_WIDTH));
						}
					if (cell.isOcupiedBySnake()) {
						// different color for human player...
						if (cell.getOcuppyingSnake() instanceof HumanSnake)
							g.setColor(Color.ORANGE);
						else
							g.setColor(Color.LIGHT_GRAY);
						g.fillRect((int) Math.round(cell.getPosition().x * CELL_WIDTH),
								(int) Math.round(cell.getPosition().y * CELL_WIDTH),
								(int) Math.round(CELL_WIDTH), (int) Math.round(CELL_WIDTH));

					}

					// }
				}
				g.setColor(Color.BLACK);
				g.drawLine((int) Math.round(x * CELL_WIDTH), 0, (int) Math.round(x * CELL_WIDTH),
						(int) Math.round(LocalBoard.NUM_ROWS * CELL_WIDTH));
			}
			for (int y = 1; y < LocalBoard.NUM_ROWS; y++) {
				g.drawLine(0, (int) Math.round(y * CELL_WIDTH), (int) Math.round(LocalBoard.NUM_COLUMNS * CELL_WIDTH),
						(int) Math.round(y * CELL_WIDTH));
			}
			for (Snake s : board.getSnakes()) {
				if (s.getLength() > 0) {
					g.setColor(new Color(s.getIdentification() * 1000));

					((Graphics2D) g).setStroke(new BasicStroke(5));
					BoardPosition prevPos = s.getPath().getFirst();
					for (BoardPosition coordinate : s.getPath()) {
						if (prevPos != null) {
							g.drawLine((int) Math.round((prevPos.x + .5) * CELL_WIDTH),
									(int) Math.round((prevPos.y + .5) * CELL_WIDTH),
									(int) Math.round((coordinate.x + .5) * CELL_WIDTH),
									(int) Math.round((coordinate.y + .5) * CELL_WIDTH));
						}
						prevPos = coordinate;
					}
					((Graphics2D) g).setStroke(new BasicStroke(1));
				}
			}
		} else {
			if (g != null)
				super.paintComponent(g);
			final double CELL_WIDTH = getHeight() / (double) LocalBoard.NUM_COLUMNS;
			if (boardMap.entrySet() != null) {
				for (Map.Entry<BoardPosition, CellContent> entry : boardMap.entrySet()) {
					if (entry != null) {
						BoardPosition key = entry.getKey();
						CellContent value = entry.getValue();
						Image image = null;
						Snake snake = null;
						GameElement el = null;
						if (value.hasGameElement()) {
							el = value.getGameElement();
							if (el instanceof Obstacle) {

								Obstacle obstacle = (Obstacle) el;
								int ObstacleX = obstacle.getCurrent().x;
								int ObstacleY = obstacle.getCurrent().y;
								image = obstacleImage;

								g.setColor(Color.BLACK);
								g.drawImage(image, (int) Math.round(ObstacleX * CELL_WIDTH),
										(int) Math.round(ObstacleY * CELL_WIDTH),
										(int) Math.round(CELL_WIDTH), (int) Math.round(CELL_WIDTH), null);
								// write number of remaining moves
								g.setColor(Color.WHITE);
								g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) CELL_WIDTH));
								g.drawString(obstacle.getRemainingMoves() + "",
										(int) Math.round((ObstacleX + 0.15) * CELL_WIDTH),
										(int) Math.round((ObstacleY + 0.9) * CELL_WIDTH));

							} else if (el instanceof Goal) {

								Goal goal = (Goal) el;

								int goalX = key.getX();
								int goalY = key.getY();

								g.setColor(Color.RED);
								g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, (int) CELL_WIDTH));
								g.drawString(goal.getValue() + "", (int) Math.round((goalX + 0.15) * CELL_WIDTH),
										(int) Math.round((goalY + 0.9) * CELL_WIDTH));
							}
						}

						if (value.hasSnake()) {
							snake = value.getSnake();
							// different color for human player...
							if (snake instanceof HumanSnake)
								g.setColor(Color.ORANGE);
							else
								g.setColor(Color.LIGHT_GRAY);
							LinkedList<Cell> cobra = snake.getCells();
							for (int i = 0; i != cobra.size(); i++) {

								Cell bloco = cobra.get(i);
								int blocoX = bloco.getPosition().x;
								int blocoY = bloco.getPosition().y;

								g.fillRect((int) Math.round(blocoX * CELL_WIDTH),
										(int) Math.round(blocoY * CELL_WIDTH),
										(int) Math.round(CELL_WIDTH), (int) Math.round(CELL_WIDTH));
							}
						}

						g.setColor(Color.BLACK);
						g.drawLine((int) Math.round(key.getX() * CELL_WIDTH), 0,
								(int) Math.round(key.getX() * CELL_WIDTH),
								(int) Math.round(RemoteBoard.NUM_ROWS * CELL_WIDTH));

						for (int y1 = 1; y1 < RemoteBoard.NUM_ROWS; y1++) {
							g.drawLine(0, (int) Math.round(y1 * CELL_WIDTH),
									(int) Math.round(RemoteBoard.NUM_COLUMNS * CELL_WIDTH),
									(int) Math.round(y1 * CELL_WIDTH));
						}

						if (snake != null) {
							LinkedList<Cell> pedacos = snake.getCells();
							if (pedacos.size() > 0) {
								g.setColor(new Color((int) (snake.getId() * 1000)));

								((Graphics2D) g).setStroke(new BasicStroke(5));
								BoardPosition prevPos = snake.getPath().getFirst();
								for (BoardPosition coordinate : snake.getPath()) {
									if (prevPos != null) {
										g.drawLine((int) Math.round((prevPos.x + .5) * CELL_WIDTH),
												(int) Math.round((prevPos.y + .5) * CELL_WIDTH),
												(int) Math.round((coordinate.x + .5) * CELL_WIDTH),
												(int) Math.round((coordinate.y + .5) * CELL_WIDTH));
									}
									prevPos = coordinate;
								}
								((Graphics2D) g).setStroke(new BasicStroke(1));
							}
						}
					}
				}
			}
		}
	}

	// Only for remote clients: 2. part of the project
	// Methods keyPressed and keyReleased will react to user pressing and
	// releasing keys on the keyboard.
	@Override
	public void keyPressed(KeyEvent e) {
		// System.out.println("Got key pressed.");
		if (e.getKeyCode() != KeyEvent.VK_LEFT && e.getKeyCode() != KeyEvent.VK_RIGHT &&
				e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN)
			return; // ignore
		board.handleKeyPress(e.getKeyCode());

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() != KeyEvent.VK_LEFT && e.getKeyCode() != KeyEvent.VK_RIGHT &&
				e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN)
			return; // ignore

		System.out.println("Got key released.");
		board.handleKeyRelease();
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// ignore
	}

	public String getLastPressedDirection() {
		return lastDirection;
	}

	public void setLastPressedDirection(String a) {
		lastDirection = a;
	}

}
