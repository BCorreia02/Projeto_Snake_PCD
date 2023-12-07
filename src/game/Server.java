package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	private static final int PORT = 9081;
	private ExecutorService pool = Executors.newCachedThreadPool();

	public void startServer() throws IOException {
		try (ServerSocket listener = new ServerSocket(PORT)) {
			System.out.println("O servidor do Snake está rodando...");
			while (true) {
				Socket socket = listener.accept();
				pool.execute(new ClientHandler(socket)); // ClientHandler é uma classe que você precisará criar.
			}
		}
	}

	public static void main(String[] args) throws IOException {
		new Server().startServer();
	}
}
