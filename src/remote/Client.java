package remote;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import environment.Cell;
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

	public static final int PORTO = 9081;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket cliente;
	private RemoteBoard cgui;
	private InetAddress ip;
	private boolean method;
	private SnakeGui gui;

	Client(InetAddress ip, boolean method) throws IOException {
		this.ip = ip;
		this.method = method;
		// this.cgui = new RemoteBoard(new BoardComponentClient(method),true);
		// this.gui= Server.getInstance().getGame();
		// this.gui = new SnakeGui(cgui,600 , 0);
		// gui.start();
	}

	public void runClient() throws IOException {
		try {
			connectToServer();
			cgui.init();
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
		cliente = new Socket(ip, PORTO);
		in = new ObjectInputStream(cliente.getInputStream());
		out = new ObjectOutputStream(cliente.getOutputStream());
	}

	public void handleConnection() throws IOException, ClassNotFoundException {
		System.out.println("handleConnection");
		while (true) { // resolver exercicio exame da 1a epoca separar dados do client em duas threads
			handleRcvPacote();
			System.out.println("GameState tratado");
			handleSend();
			System.out.println("Direcao enviada");
		}
	}

	public void handleRcvPacote() throws ClassNotFoundException, IOException {

		GameState estado = (GameState) in.readObject(); // le GameState recebido e atualiza a gui consoante os dados de
														// cada Pacote incluidos na GameState recebida
		List<Pacotev1> recebido = estado.getPacotes();
		cgui.atualiza(recebido);
		System.out.println("atualizei");

	}

	public void handleSend() throws IOException { // envio numa thread separada
		String c = cgui.getBoardClient().getLastPressedDirection();
		System.out.println(c);
		out.writeObject(c);
		out.flush();
		cgui.getBoardClient().getLastPressedDirection();
		System.out.println("enviei");

	}

	public static void main(String[] args) throws IOException {
		new Client(InetAddress.getByName(null), false).runClient();
	}

}
