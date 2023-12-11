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
	private boolean method;
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
		new Client(InetAddress.getByName(null),Server.port,new BoardComponentClient(null)).runClient();
	}

	public void runClient() throws IOException {
		try {
			connectToServer();
			gui.init();
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
		System.out.println("Socket " + cliente);

		out = new ObjectOutputStream(cliente.getOutputStream()); //trocar para mandar strings
		out.flush();
		System.out.println("Cliente passou output");

		in = new ObjectInputStream(cliente.getInputStream());
		System.out.println("client passou input ");
	}

	public void handleConnection() throws IOException, ClassNotFoundException {

		
		

		while (true) { // resolver exercicio exame da 1a epoca separar dados do client em duas threads
			
			
			handleRcvPacote();
			handleSend();
			System.out.println("GameState");
			
		}
	}

	

	public void handleRcvPacote() throws ClassNotFoundException, IOException {
		 // le GameState recebido e atualiza a gui consoante os dados de
														// cada Pacote incluidos na GameState recebida
		System.out.println("Recebi gamestate");
		Object recieved = in.readObject();
		if (recieved!=null && recieved instanceof ConcurrentHashMap){
			ConcurrentHashMap<BoardPosition, CellContent> mapa = (ConcurrentHashMap<BoardPosition, CellContent>) in.readObject();
			bcc.setNewMap(mapa);
			bcc.repaint();
			this.cgui = new RemoteBoard(bcc, true);
		}
	}

	public void handleSend() throws IOException { // envio numa thread separada
		String c = cgui.getBoardClient().getLastPressedDirection();
		System.out.println(c);
		out.writeObject(c);
		out.flush();
		cgui.getBoardClient().getLastPressedDirection();
		System.out.println("enviei");

	}

	

}
