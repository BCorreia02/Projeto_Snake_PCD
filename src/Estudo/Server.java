package Estudo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* 1 STREAM --> Thread Sender (Send), Thread Server (New Connections)
 * 2 STREAMS --> Thread Cliente (Receive) , Thread Sender (Send), Thread Server (New Connections)
 */

public class Server extends Thread {

    // ATRIBUTOS
    // ServerSocket, list ins e outs = Collections.synchronizedList(new List)
    private ServerSocket ss;
    List<ObjectOutputStream> outs = Collections.synchronizedList(new ArrayList<ObjectOutputStream>());
    List<ObjectInputStream> ins = Collections.synchronizedList(new ArrayList<ObjectInputStream>());

    // CONSTRUTOR: criar server socket, catch: IOException
    public Server() {
        try {
            ss = new ServerSocket(8081);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // MAIN: criar Server e correr thread
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.run();
    }

    // RUN:
    // Num while criar socket = ss.accept();
    // Criar threads para in e out com a socket e inciar
    // catch IOException
    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Socket cliSocket = ss.accept();
                new StringReceiver(cliSocket).start();
                new ClientHandler(cliSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // OUT CLASS
    public class ClientHandler extends Thread {

        // ATRIBUTOS:
        // ObjectOutPutStream
        ObjectOutputStream out;

        // CONSTUTOR criar canal out e adicionar a lista de outs
        // catch: IOException
        public ClientHandler(Socket s) throws IOException {
            try {
                out = new ObjectOutputStream(s.getOutputStream());
                outs.add(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // RUN:
        // num while enviar outs = out.writeObject(), out.flush()
        // catch IOException, InterrupptedException
        @Override
        public void run() {

            while (!Thread.interrupted()) {
                try {
                    System.out.println("Server Pacote enviado");
                    for (ObjectOutputStream out : outs) {
                        out.writeObject("Server Pacote recebido");
                        out.flush();
                    }
                    Thread.sleep(2000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // IN CLASS
    public class StringReceiver extends Thread {

        ObjectInputStream in;

        // CONSTRUTOR:
        // criar canal in = new InputObjectStream(socket.getInputStream())
        // Adicionar a lista de ins
        // catch: IOException
        public StringReceiver(Socket socket) {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                ins.add(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // RUN:
        // while for (ObjectInputStream a : ins)
        // catch IOExcepion, ClassNotFoundException
        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    for (ObjectInputStream a : ins)
                        System.out.println((String) a.readObject());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
