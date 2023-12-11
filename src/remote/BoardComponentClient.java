package remote;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import environment.BoardPosition;
import environment.Cell;
import environment.CellContent;
import game.Goal;
import game.HumanSnake;
import game.Obstacle;
import game.Snake;
import game.GameElement;
import utils.Direction;

public class BoardComponentClient extends JComponent implements KeyListener, Serializable {

	private static final long serialVersionUID = 1L;
	private Image obstacleImage;
	public static final int NUM_ROWS = 30;
	public static final int NUM_COLUMNS = 30;
	private RemoteBoard cgui;
	private GameState gs;
	private boolean method;
	private List<Pacotev1> pacotes;
	private String lastDirection;

	public BoardComponentClient(Boolean method) {
		obstacleImage = new ImageIcon(getClass().getResource("/obstacle.png")).getImage();
		// Necessary for key listener
		setFocusable(true);
		addKeyListener(this);
	}

	public void setNewList(List<Pacotev1> p) {
		this.pacotes = p;
		repaint();
	}

	// MODIFICAR paintComponent de maneira a que em vez de ele ir buscar Cells e
	// outros dados à board
	// vai busca-los a List<pacote> pacotes - tenho de passar apenas a posicao das
	// cobras
	// ou todo o tabuleiro (Cells e Obstacles também)?

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final double CELL_WIDTH = getHeight() / (double) NUM_COLUMNS;

		if (this.pacotes != null) {
			for (Pacotev1 p : pacotes) {

				HashMap<BoardPosition, CellContent> boardMap = p.getBoardMap();

				for (Map.Entry<BoardPosition, CellContent> entry : boardMap.entrySet()) {

					BoardPosition key = entry.getKey();
					CellContent value = entry.getValue();
					Image image = null;
					Snake snake = null;
					GameElement el = null;
					if (value.getGameElement() != null) {
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

					if (value.getSnake() != null) {
						snake = (Snake) entry;
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
					g.drawLine((int) Math.round(key.getX() * CELL_WIDTH), 0, (int) Math.round(key.getX() * CELL_WIDTH),
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

	// Only for remote clients: 2. part of the project
	// Methods keyPressed and keyReleased will react to user pressing and
	// releasing keys on the keyboard.
	@Override
	public void keyPressed(KeyEvent e) {

		// if(!this.method){
		// }

		System.out.println("Got key pressed.");
		if (e.getKeyCode() != KeyEvent.VK_LEFT && e.getKeyCode() != KeyEvent.VK_RIGHT &&
				e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN)
			return; // ignore
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				lastDirection = "LEFT";
				break;
			case KeyEvent.VK_RIGHT:
				lastDirection = "RIGHT";
				break;
			case KeyEvent.VK_UP:
				lastDirection = "UP";
				break;
			case KeyEvent.VK_DOWN:
				lastDirection = "DOWN";
				break;
		}
		cgui.handleKeyPress(e.getKeyCode());

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() != KeyEvent.VK_LEFT && e.getKeyCode() != KeyEvent.VK_RIGHT &&
				e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN)
			return; // ignore

		System.out.println("Got key released.");
		cgui.handleKeyRelease();
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// ignore
	}

	public String getLastPressedDirection() {
		return lastDirection;
	}
}
