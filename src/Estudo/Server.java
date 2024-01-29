package Estudo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {

    private ServerSocket ss;
    List<ObjectOutputStream> outs = Collections.synchronizedList(new ArrayList<ObjectOutputStream>());
    List<ObjectInputStream> ins = Collections.synchronizedList(new ArrayList<ObjectInputStream>());

    public Server() throws IOException {
        ss = new ServerSocket(8081);
    }

    public static void main(String[] args) throws IOException {
        try {
            Server server = new Server();
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() throws IOException {
        try {
            while (true) {
                Socket cliSocket = ss.accept();
                new ClientHandler(cliSocket).start();
                new StringReceiver(cliSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ClientHandler extends Thread {

        ObjectOutputStream out;

        public ClientHandler(Socket s) throws IOException {
            try {
                out = new ObjectOutputStream(s.getOutputStream());
                outs.add(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("Server Pacote enviado");
                    out.writeObject("Server Pacote recebido");
                    out.flush();
                    Thread.sleep(2000);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class StringReceiver extends Thread {

        ObjectInputStream in;

        public StringReceiver(Socket socket) {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                ins.add(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (!ss.isClosed()) {
                    for (ObjectInputStream a : ins)
                        System.out.println((String) a.readObject());
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
