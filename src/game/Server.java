package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import environment.CellContent;
import environment.LocalBoard;
import gui.SnakeGui;
import java.awt.event.KeyEvent;

public class Server {
	private ServerSocket server;

	public static final int port = 9081;
	private ServerSocket ss;
	private ObjectInputStream in; // deixou de estar = null
	private ObjectOutputStream out; // deixou de estar = null
	private LocalBoard board;
	private SnakeGui gui;

	private int id = 100;
	private List<ObjectOutputStream> outs = Collections.synchronizedList(new ArrayList<ObjectOutputStream>());

	public Server() throws IOException {
		this.ss = new ServerSocket(port);
		this.board = new LocalBoard();
		this.gui = new SnakeGui(board, 600, 0);
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
		private HumanSnake hs;

		ClientHandler(Socket socket) throws ClassNotFoundException, IOException, InterruptedException {

			this.clientSocket = socket;
			in = new ObjectInputStream(this.clientSocket.getInputStream());
			HumanSnake hs = new HumanSnake(id, board); /// while (true) englobava as a criacao de um HumanPlayer -errado
														/// pq por cada cliente que se liga ao servidor apenas � criado
														/// um humanplayer
			id++;
			board.addSnake(hs);
			System.out.println("Inicio client handler " + hs.toString());
			this.hs = hs;
		}

		public void handleIn() throws ClassNotFoundException, IOException, InterruptedException {
			while (!clientSocket.isClosed()) {
				Object received = in.readObject();
				if (received != null) {
					String c = (String) received;
					System.out.println("Key received " + c);

					/*
					 * BoardPosition head = hs.getCells().getLast().getPosition();
					 * BoardPosition newPos = null;
					 * 
					 * switch (c) {
					 * case "LEFT":
					 * newPos = head.getCellLeft();
					 * break;
					 * case "RIGHT":
					 * newPos = head.getCellRight();
					 * break;
					 * case "UP":
					 * newPos = head.getCellAbove();
					 * break;
					 * case "DOWN":
					 * newPos = head.getCellBelow();
					 * break;
					 * }
					 * 
					 * hs.setFuture(new Cell(newPos));
					 */
				}
			}
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

		private void updateMaps(ConcurrentHashMap<BoardPosition, CellContent> map) {
			synchronized (outs) {
				for (ObjectOutputStream out : outs)
					try {
						// System.out.println("mapa -> cliente ");
						out.writeObject(map);
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}

		public void handleOut() throws ClassNotFoundException, IOException, InterruptedException {
			updateMaps(board.getHashMap());
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
