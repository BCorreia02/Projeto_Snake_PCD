package remote;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import environment.BoardPosition;
import environment.Cell;
import environment.CellContent;
import game.Server;
import gui.BoardComponent;
import gui.SnakeGui;
import utils.Direction;

/**
 * Remore client, only for part II
 * 
 * @author luismota
 *
 */

public class Client {

	// public static final int PORTO = 9081;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket cliente;
	private RemoteBoard cgui;
	private InetAddress ip;
	private int porto;
	private SnakeGui gui;
	private BoardComponentClient bcc;

	Client(InetAddress ip, int porto, BoardComponentClient bcc) throws IOException {
		this.ip = ip;
		this.porto = porto;
		this.bcc = bcc;
		this.cgui = new RemoteBoard(bcc, true);
		this.gui = new SnakeGui(cgui, 0, 0);

	}

	public static void main(String[] args) throws IOException {
		new Client(InetAddress.getByName(null), Server.port, new BoardComponentClient(null, true)).runClient();
	}

	public void runClient() throws IOException {
		try {
			connectToServer();
			gui.setVisible(true); // Make the GUI visible
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
		System.out.println("IN OUT CREATED");
	}

	public void handleConnection() throws IOException, ClassNotFoundException {
		while (true) { // resolver exercicio exame da 1a epoca separar dados do client em duas threads
			handleRcvPacote();
			handleSend();
		}
	}

	public void handleRcvPacote() throws ClassNotFoundException, IOException {
		Object received = in.readObject();
		if (received != null && received instanceof ConcurrentHashMap) {
			System.out.println("Novo map");
			ConcurrentHashMap<BoardPosition, CellContent> mapa = (ConcurrentHashMap<BoardPosition, CellContent>) received;
			bcc.setNewMap(mapa);
			this.cgui = new RemoteBoard(bcc, true);
		}
	}

	public void handleSend() throws IOException { // envio numa thread separada
		String c = cgui.getBoardClient().getLastPressedDirection();
		if (c != null) {
			out.writeObject(c);
			out.flush();
			cgui.getBoardClient().getLastPressedDirection();
			System.out.println("Tecla enviada");
		}

	}

}
