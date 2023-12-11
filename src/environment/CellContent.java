package environment;

import java.io.Serializable;

import game.GameElement;
import game.Snake;

public class CellContent implements Serializable {
    private GameElement gameElement;
    private Snake snake;

    public CellContent(GameElement gameElement, Snake snake) {
        this.gameElement = gameElement;
        this.snake = snake;
    }

    // Getters and possibly some utility methods
    public boolean hasGameElement() {
        return gameElement != null;
    }

    public boolean hasSnake() {
        return snake != null;
    }

    public Snake getSnake() {
        if (snake != null)
            return snake;
        return null;
    }

    public GameElement getGameElement() {
        if (gameElement != null)
            return gameElement;
        return null;
    }

    // Additional methods as needed
}