package environment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

import game.GameElement;
import game.Goal;
import game.Obstacle;
import game.ObstacleMover;
import game.Snake;

public abstract class Board extends Observable implements Serializable {

	protected Cell[][] cells;
	private BoardPosition goalPosition;
	public static final long PLAYER_PLAY_INTERVAL = 100;
	public static final long REMOTE_REFRESH_INTERVAL = 50;
	public static final int NUM_COLUMNS = 30;
	public static final int NUM_ROWS = 30;
	protected LinkedList<Snake> snakes = new LinkedList<Snake>();
	protected LinkedList<Obstacle> obstacles = new LinkedList<Obstacle>();
	public ObstacleMover obstacleMover;
	public int goalValue = 1;

	public Board() {
		cells = new Cell[NUM_COLUMNS][NUM_ROWS];
		for (int x = 0; x < NUM_COLUMNS; x++) {
			for (int y = 0; y < NUM_ROWS; y++) {
				cells[x][y] = new Cell(new BoardPosition(x, y));
			}
		}

	}

	public boolean isObstacleAt(BoardPosition p) {
		LinkedList<Obstacle> obstacles = this.getObstacles();
		for (Obstacle o : obstacles) {
			if (o.getCurrent() == p) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean isSnakeAt(BoardPosition p) {
		LinkedList<Snake> snakes = this.getSnakes();
		for (Snake s : snakes) {
			LinkedList<Cell> snakecells = s.getCells();
			for (Cell c : snakecells) {
				if (c.getPosition() == p) {
					return true;
				}
			}
		}
		return false;
	}

	public List<BoardPosition> getBlockedCells() {
		List<BoardPosition> blocked = new ArrayList<>();
		for (int x = 0; x < NUM_COLUMNS; x++) {
			for (int y = 0; y < NUM_ROWS; y++) {
				BoardPosition pos = new BoardPosition(x, y);
				if (new Cell(pos).isBlocked())
					blocked.add(new BoardPosition(x, y));
			}
		}
		return blocked;
	}

	public ConcurrentHashMap<BoardPosition, CellContent> getHashMap() {
		ConcurrentHashMap<BoardPosition, CellContent> map = new ConcurrentHashMap<>();
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells.length; j++) {
				Cell cell = cells[i][j];
				GameElement gameElement = cell.getGameElement();
				Snake snake = cell.getOcuppyingSnake();
				map.put(new BoardPosition(i, j), new CellContent(gameElement, snake));
			}
		}
		return map;
	}

	public void endGame() {
		System.exit(0);
	}

	public Snake getSnake(Snake cobra) {
		for (Snake snake : snakes)
			if (cobra.equals(snake))
				return snake;
		return null;
	}

	public Cell getCell(BoardPosition cellCoord) {
		return cells[cellCoord.x][cellCoord.y];
	}

	public BoardPosition getRandomPosition() {
		return new BoardPosition((int) (Math.random() * NUM_ROWS), (int) (Math.random() * NUM_ROWS));
	}

	public BoardPosition getGoalPosition() {
		return goalPosition;
	}

	public ObstacleMover getObstacleMover() {
		return obstacleMover;
	}

	public void setGoalPosition(BoardPosition goalPosition) {
		this.goalPosition = goalPosition;
	}

	public void addGameElement(GameElement gameElement) {
		boolean placed = false;
		while (!placed) {
			BoardPosition pos = getRandomPosition();
			if (!getCell(pos).isOcupied() && !getCell(pos).isOcupiedByGoal()) {
				getCell(pos).setGameElement(gameElement);
				if (gameElement instanceof Goal) {
					setGoalPosition(pos);
				} else
					((Obstacle) gameElement).setCurrent(pos);

				placed = true;
			}
		}

		setChanged();
	}

	public void removeGameElement(GameElement gameElement) {
		for (int x = 0; x < NUM_COLUMNS; x++) {
			for (int y = 0; y < NUM_ROWS; y++) {
				if (cells[x][y].getGameElement() == gameElement) {
					cells[x][y].setGameElement(null);
					return;
				}
			}
		}
	}

	public boolean hasActiveGoal() {
		for (int x = 0; x < NUM_COLUMNS; x++) {
			for (int y = 0; y < NUM_ROWS; y++) {
				if (cells[x][y].getGameElement() instanceof Goal) {
					return true;
				}
			}
		}
		return false;
	}

	public Goal getActiveGoal() {
		for (int x = 0; x < NUM_COLUMNS; x++) {
			for (int y = 0; y < NUM_ROWS; y++) {
				if (cells[x][y].getGameElement() instanceof Goal) {
					return (Goal) cells[x][y].getGameElement();
				}
			}
		}
		return null;
	}

	public List<BoardPosition> getNeighboringPositions(Cell cell) {
		ArrayList<BoardPosition> possiblePos = new ArrayList<BoardPosition>();
		BoardPosition pos = cell.getPosition();
		if (pos.x > 0)
			possiblePos.add(pos.getCellLeft());
		if (pos.x < NUM_COLUMNS - 1)
			possiblePos.add(pos.getCellRight());
		if (pos.y > 0)
			possiblePos.add(pos.getCellAbove());
		if (pos.y < NUM_ROWS - 1)
			possiblePos.add(pos.getCellBelow());
		return possiblePos;
	}

	public int getGoalValue() {
		return this.goalValue;
	}

	protected Goal addGoal() {
		if (goalValue == 12)
			endGame();
		Goal goal = new Goal(this);
		addGameElement(goal);
		return goal;
	}

	protected void addObstacles(int numberObstacles) {
		// clear obstacle list , necessary when resetting obstacles.
		getObstacles().clear();
		while (numberObstacles > 0) {
			Obstacle obs = new Obstacle(this);
			addGameElement(obs);
			getObstacles().add(obs);
			numberObstacles--;
		}
	}

	public LinkedList<Snake> getSnakes() {
		return snakes;
	}

	// Método para calcular a posição vizinha mais próxima de uma posição dada
	public BoardPosition getClosestNeighborPosition(BoardPosition current, BoardPosition target) {
		List<BoardPosition> neighbors = getNeighboringPositions(getCell(current));
		BoardPosition closest = null;
		double minDistance = Double.MAX_VALUE;
		for (BoardPosition neighbor : neighbors) {
			double distance = neighbor.distanceTo(target);
			if (distance < minDistance) {
				closest = neighbor;
				minDistance = distance;
			}
		}
		return closest;
	}

	public ConcurrentHashMap<BoardPosition, CellContent> getGameState() {
		// List<Pacotev1> infoPlayers = new ArrayList<Pacotev1>();
		ConcurrentHashMap<BoardPosition, CellContent> map = getHashMap();
		// Pacotev1 pacote = new Pacotev1(map);
		// infoPlayers.add(pacote);
		// GameState estado = new GameState(infoPlayers);
		// se um jogador morrer, remover da lista
		return map;
	}

	// Método para calcular a distância entre duas posições
	public double distanceBetween(BoardPosition a, BoardPosition b) {
		int dx = a.getX() - b.getX();
		int dy = a.getY() - b.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		notifyObservers();
	}

	public LinkedList<Obstacle> getObstacles() {
		return obstacles;
	}

	public synchronized void removeSnake(Snake snake) {
		snakes.removeIf(s -> s.getIdentification() == snake.getIdentification());
	}

	public abstract void init();

	public abstract void handleKeyPress(int keyCode);

	public abstract void handleKeyRelease();

	public void setGoalValue(int i) {
		this.goalValue += i;
	}

	public synchronized void addSnake(Snake snake) {
		snakes.add(snake);
	}

	public boolean shouldEndGame(int goalValue) {
		return goalValue == 9;
	}

}