package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import environment.Board;
import environment.BoardPosition;
import environment.CellContent;
import environment.LocalBoard;
import gui.SnakeGui;
import remote.Mensagem;

/**
 * 
 *
 * 
 * @author bcorreia02
 *
 */

public class Server {

	public static final int port = 9081;
	private ServerSocket ss;
	private LocalBoard board;
	private SnakeGui gui;

	private int id = 100;
	private List<ObjectOutputStream> outs = Collections.synchronizedList(new ArrayList<ObjectOutputStream>());

	public Server() throws IOException {
		this.ss = new ServerSocket(port);
		this.board = new LocalBoard();
		this.gui = new SnakeGui(board, 600, 0, false);
		gui.init();

	}

	public Board getBoard() {
		return board;
	}

	public SnakeGui getGame() {
		return gui;
	}

	public void startServer() throws IOException, ClassNotFoundException, InterruptedException {

		try {
			while (!ss.isClosed()) {
				Socket s = ss.accept();
				ClientHandler ch = new ClientHandler(s);
				GameUpdater gu = new GameUpdater(s);
				gu.start();
				ch.start();
			}
		} finally {
			closeServerSocket();
		}
	}

	public void closeServerSocket() {
		try {
			if (ss != null) {
				ss.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Server server = new Server();
		server.startServer();

	}

	// -------------------------------------

	public class ClientHandler extends Thread {

		private Socket clientSocket;
		private ObjectInputStream in;

		ClientHandler(Socket socket) throws ClassNotFoundException, IOException, InterruptedException {
			this.clientSocket = socket;
			in = new ObjectInputStream(this.clientSocket.getInputStream());
			HumanSnake hs = new HumanSnake(id, board);
			id++;
			board.addPlayer(hs);
			hs.doInitialPositioning();
			// System.out.println("Inicio client handler " + hs.toString());
		}

		public void handleIn() throws ClassNotFoundException, IOException, InterruptedException {
			while (!clientSocket.isClosed() && !Thread.currentThread().isInterrupted()) {
				try {
					Object received = in.readObject();
					if (received != null) {
						String c = (String) received;
						processCommand(c);
					}
				} catch (IOException e) {
					System.err.println("IO Exception: " + e.getMessage());
					break;
				} catch (ClassNotFoundException e) {
					System.err.println("Class not found: " + e.getMessage());

				} catch (ClassCastException e) {
					System.err.println("Received object is not a String: " + e.getMessage());

				}
			}

		}

		private void processCommand(String command) {
			LinkedList<Snake> snakes = board.getSnakes();
			for (Snake s : snakes) {
				if (s instanceof HumanSnake) {
					HumanSnake humanSnake = (HumanSnake) s;
					humanSnake.setDirection(command);
					try {
						humanSnake.move();
						board.setChanged();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt(); // Set the interrupt flag.
						System.err.println("Thread was interrupted: " + e.getMessage());
						break; // Optional: Decide whether you want to stop processing after an interrupt.
					}
				}
			}
			// Optionally, log the received command at a debug or trace level, not info or
			// error.
			// logger.debug("Key received: {}", command); // Use a proper logging framework.
		}

		@Override
		public void run() {
			try {
				handleIn();
			} catch (ClassNotFoundException | IOException | InterruptedException e) {
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// --------------------------------------------------------

	public class GameUpdater extends Thread {
		private Socket clientSocket;
		private ObjectOutputStream out;
		private HumanSnake jogador;

		GameUpdater(Socket socket) throws IOException, InterruptedException, ClassNotFoundException {
			this.clientSocket = socket;
			this.initializeOut(this.clientSocket);
		}

		public HumanSnake getHumanPlayer() {
			return jogador;
		}

		public void initializeOut(Socket sock) throws IOException, ClassNotFoundException {
			out = new ObjectOutputStream(sock.getOutputStream());
			outs.add(out);
		}

		private void updateMaps(Mensagem m) {
			synchronized (outs) {
				for (ObjectOutputStream out : outs)
					try {
						out.reset();
						out.writeObject(m);
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}

		public void handleOut() throws ClassNotFoundException, IOException, InterruptedException {
			ConcurrentHashMap<BoardPosition, CellContent> map = board.getHashMap();
			Mensagem m = new Mensagem(map, board.getSnakes());
			updateMaps(m);
			out.flush();
			Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);
		}

		public void handleConnection() throws IOException, ClassNotFoundException, InterruptedException {
			while (!Thread.interrupted()) {
				handleOut();
			}
		}

		@Override
		public void run() {
			try {
				handleConnection();
			} catch (ClassNotFoundException | IOException | InterruptedException e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
