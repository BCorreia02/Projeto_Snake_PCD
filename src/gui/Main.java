package gui;

import environment.LocalBoard;

/**
 * 
 *
 * 
 * @author bcorreia02
 *
 */

public class Main {
    public static void main(String[] args) {
        LocalBoard board = new LocalBoard();

        // Initialize and start the GUI
        SnakeGui game = new SnakeGui(board, 600, 0, false);
        game.init();
    }
}
