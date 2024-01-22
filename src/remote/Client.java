package remote;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import environment.BoardPosition;
import environment.Cell;
import environment.CellContent;
import game.Server;
import game.Snake;
import gui.SnakeGui;

/**
 * Remore client, only for part II
 * 
 * @author luismota
 *
 */

public class Client {

	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket cliente;
	private RemoteBoard board;
	private InetAddress ip;
	private int porto;
	private SnakeGui gui;
	private BoardComponentClient bcc;

	Client(InetAddress ip, int porto) throws IOException {
		this.ip = ip;

		this.porto = porto;
		this.bcc = new BoardComponentClient(null);
		this.board = new RemoteBoard(bcc, true);
		this.gui = new SnakeGui(board, 600, 0, true);
		gui.init();
	}

	public void setBoardComponentClient(BoardComponentClient bcc) {
		this.bcc = bcc;
		this.board = new RemoteBoard(bcc, true);
		// Update GUI
		this.gui.setBoard(board);
	}

	public RemoteBoard getBoard() {
		return board;
	}

	public BoardComponentClient getBoardComponentClient() {
		return bcc;
	}

	public static void main(String[] args) throws IOException {
		Client client = new Client(InetAddress.getByName(null), Server.port);
		client.runClient();
		client.setBoardComponentClient(client.getBoardComponentClient());
	}

	public void runClient() throws IOException {
		try {
			connectToServer();
			handleConnection();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} finally {
			in.close();
			out.close();
			this.cliente.close();
		}
	}

	public void connectToServer() throws IOException {
		cliente = new Socket(ip, porto);
		out = new ObjectOutputStream(cliente.getOutputStream()); // trocar para mandar strings
		out.flush();
		in = new ObjectInputStream(cliente.getInputStream());
		System.out.println("CONNECTION CREATED");
	}

	public void handleConnection() throws IOException, ClassNotFoundException {
		while (!cliente.isClosed()) { // resolver exercicio exame da 1a epoca separar dados do client em duas threads
			handleRcvPacote();
			handleSend();
		}
	}

	public void handleRcvPacote() throws ClassNotFoundException, IOException {
		Object received = in.readObject();
		if (received != null && received instanceof Mensagem) {
			Mensagem mapa = (Mensagem) received;
			gui.getBoardComponent().setNewMap(mapa.getMap(), mapa.getSnakeList());
			
			for (Snake s : mapa.getSnakeList()) {
				for (Cell c : s.getCells()) {
					System.out.println(s + "" + c);
				}
		}
			gui.getBoardComponent().repaint();
		}
	}

	public void handleSend() throws IOException { // envio numa thread separada
		String c = board.getBoardComponentClient().getLastPressedDirection();
		if (c != null) {
			out.writeObject(c);
			out.flush();
			System.out.println("Tecla enviada");
		}

	}

}
