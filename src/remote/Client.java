package remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import game.Server;
import gui.BoardComponent;
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

	Client(InetAddress ip, boolean method) {
		this.ip = ip;
		this.method = method;
		this.cgui = new RemoteBoard(new BoardComponentClient(method));
	}

	public void runClient() throws IOException {
		try {
			connectToServer();

			cgui.init();
			System.out.println("GUI INICIADA");
			handleConnection();
		} catch (ClassNotFoundException | IOException e) {
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

		while (true) { // resolver exercicio exame da 1a epoca separar dados do client em duas threads
			handleRcvPacote();
			handleSend();
		}
	}

	public void handleRcvPacote() throws ClassNotFoundException, IOException {
		GameState estado = (GameState) in.readObject(); // le GameState recebido e atualiza a gui consoante os dados de
														// cada Pacote incluidos na GameState recebida
		List<Pacote> recebido = estado.getPacotes();
		cgui.atualiza(recebido);
		System.out.println("atualizei");

	}

	public void handleSend() throws IOException { // envio numa thread separada
		Direction d = cgui.getBoardClient().getLastPressedDirection();
		System.out.println(d);
		out.writeObject(d);
		out.flush();
		cgui.getBoardClient().getLastPressedDirection();

	}

	public static void main(String[] args) throws IOException {
		new Client(InetAddress.getByName(null), false).runClient();
	}

}
