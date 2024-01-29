package remote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import game.Server;
import gui.SnakeGui;

/**
 * 
 *
 * 
 * @author bcorreia02
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
	private Thread keySenderThread;

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
			keySenderThread = new Thread(new KeySender()); // Create and start the KeySender thread
			keySenderThread.start();
			handleConnection();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} finally {
			in.close();
			out.close();
			this.cliente.close();
			keySenderThread.interrupt();
		}
	}

	private class KeySender implements Runnable {
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted() && !cliente.isClosed()) {
					String c = board.getBoardComponentClient().getLastPressedDirection();
					if (c != null) {
						synchronized (out) {
							out.reset();
							out.writeObject(c);
							out.flush();
						}
						System.out.println("Tecla enviada " + c);
					}
					// Optional: add some sleep time to avoid busy waiting
					Thread.sleep(100); // 100 ms sleep
				}
			} catch (IOException | InterruptedException e) {
				System.out.println("KeySender interrupted or I/O error occurred.");
			}
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
		}
	}

	public void handleRcvPacote() throws ClassNotFoundException, IOException {
		Object received = in.readObject();
		if (received != null && received instanceof Mensagem) {
			Mensagem mapa = (Mensagem) received;
			gui.getBoardComponent().setNewMap(mapa.getMap(), mapa.getSnakeList());
			gui.getBoardComponent().repaint();
		}
	}

}
