package gui;

import java.io.Console;
import java.io.IOException;

import javax.net.ssl.StandardConstants;

import environment.LocalBoard;
import game.Server;

public class Main {
	public static void main(String[] args) {
		LocalBoard board = new LocalBoard();
        
        // Declare the server variable as final
        final Server[] server = {null};
        
        try {
            server[0] = new Server();
            
            // Start the server in a separate thread
            Thread serverThread = new Thread(() -> {
                try {
                    server[0].startServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
            serverThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize and start the GUI
        SnakeGui game = new SnakeGui(board, 600, 0);
        game.init();
	}
}
