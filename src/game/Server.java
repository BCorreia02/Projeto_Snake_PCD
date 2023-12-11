package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import environment.Board;
import environment.BoardPosition;
import environment.Cell;
import gui.BoardComponent;
import gui.SnakeGui;
import javafx.scene.input.KeyEvent;
import remote.BoardComponentClient;
import remote.GameState;
import remote.RemoteBoard;

public class Server {
	private ServerSocket server;

	public static final int port = 9081;
	private ServerSocket ss;
	private ObjectInputStream in; // deixou de estar = null
	private ObjectOutputStream out; // deixou de estar = null
	private Board board;
	private SnakeGui gui;
	private BoardComponentClient bcc;

	private int id = 100;
	private List<ObjectOutputStream> outs = Collections.synchronizedList(new ArrayList<ObjectOutputStream>());
	private static Server instance;

	public Server() throws IOException {
		this.ss = new ServerSocket(port);
		this.bcc = new BoardComponentClient(null);
		this.board = new RemoteBoard(bcc, false);
		this.gui = new SnakeGui(board, 600, 0);
		gui.start(); // a SnakeGui e uma thread portanto executa o que esta dentro do run(init)

	}

	public static Server getInstance() throws IOException {
		if (instance == null) {
			instance = new Server();
		}
		return instance;
	}

	public Board getBoard() {
		return board;
	}

	public void setInstance(Server s) {
		instance = s;
	}

	public SnakeGui getGame() {
		return gui;
	}

	public void startServer() throws IOException, ClassNotFoundException, InterruptedException {

		try {

			while (!ss.isClosed()) {
				System.out.println("entrei no ciclo");
				Socket s = ss.accept();

				System.out.println("iniciei o servidor");

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
		System.out.println("criei o servidor");
		server.setInstance(server);
		System.out.println(instance);
		server.startServer();

	}

	// -------------------------------------

	public class ClientHandler extends Thread {

		private Socket clientSocket;
		private ObjectInputStream in;
		private HumanSnake hs;

		ClientHandler(Socket socket) throws ClassNotFoundException, IOException, InterruptedException {
			this.clientSocket = socket;
			this.initializeChannels(this.clientSocket);
			HumanSnake hs = new HumanSnake(id, board); /// while (true) englobava as a criacao de um HumanPlayer -errado
														/// pq por cada cliente que se liga ao servidor apenas � criado
														/// um humanplayer
			id++;
			board.addSnake(hs);
			this.hs = hs;
		}

		public void initializeChannels(Socket sock) throws IOException, ClassNotFoundException {
			in = new ObjectInputStream(sock.getInputStream());
		}

		public void handleIn() throws ClassNotFoundException, IOException, InterruptedException {
			while (!clientSocket.isClosed()) {
				String c = (String) in.readObject(); // bufferedReader como input, transformar o texto em direcao depois
														// de ler, usar ENUM
				BoardPosition head = hs.getCells().getLast().getPosition();
				BoardPosition newPos = null;
				switch (c) {
					case "LEFT":
						newPos = head.getCellLeft();
						break;
					case "RIGHT":
						newPos = head.getCellRight();
						break;
					case "UP":
						newPos = head.getCellAbove();
						break;
					case "DOWN":
						newPos = head.getCellBelow();
						break;
				}

				Cell newCell = new Cell(newPos);
				hs.setFuture(newCell);

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
			this.clientSocket = socket; // a socket de cada cliente � final e � recebida como argumento do ClientHandler
			this.initializeOut(this.clientSocket); // inicializacao dos canais de input e output do cliente com a socket
													// do cliente como parametro

		}

		public HumanSnake getHumanPlayer() {
			return jogador;
		}

		public void initializeOut(Socket sock) throws IOException, ClassNotFoundException {
			out = new ObjectOutputStream(sock.getOutputStream());
			outs.add(out);
		}

		private void updateGameState(GameState games) {
			for (ObjectOutputStream out : outs)
				try {
					out.writeObject(games);
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		public void handleOuts() throws ClassNotFoundException, IOException, InterruptedException {

			while (!Thread.interrupted()) {
				GameState estado = board.EstadoJogo();
				updateGameState(estado);
				board.setChanged();
				out.flush();
				Thread.sleep(Board.REMOTE_REFRESH_INTERVAL);
			}
		}

		@Override
		public void run() {
			try {
				handleOuts();

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
