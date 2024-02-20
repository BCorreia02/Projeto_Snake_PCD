package Estudo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/* 1 STREAM --> Thread Cliente (Send/Receive)
 * 2 STREAMS --> Thread Cliente (Receive) , Thread Sender (Send)
 */

// Cliente corre a thread the entrada
public class Cliente extends Thread {

    // ATRIBUTOS: address, port, socket, objectInputStream, SenderThreadClass
    InetAddress addr;
    static final int porto = 8081;
    Socket clientSocket;
    ObjectInputStream in;
    StringSender sender;

    // CONSTRUTOR:
    // address = InetAddress.getByName("127.0.0.1");
    // socket = new Socket(addr, porto);
    // criar thread out
    // catch: UnknownHostException, IOException
    public Cliente() throws UnknownHostException {
        try {
            this.addr = InetAddress.getByName("127.0.0.1");
            this.clientSocket = new Socket(addr, porto);
            sender = new StringSender();
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // RUN:
    // Iniciar thread de envio
    // Criar ObjectInputStream
    // ObjectInputStream(clientSocket.getInputStream());
    // Receber num while (!clientSocket.isClosed())
    // catch IOException, lassNotFoundException
    @Override
    public void run() {
        try {
            sender.start();

            while (!clientSocket.isClosed()) {
                System.out.println((String) in.readObject());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // MAIN: Criar cliente e iniciar thread
    public static void main(String args[]) throws UnknownHostException {
        Cliente client = new Cliente();
        client.start();

    }

    // OUT CLASS
    public class StringSender extends Thread {

        // ATRIBUTOS: ObjectOutputStream
        ObjectOutputStream out;

        // CONTSTRUTOR: criar canal
        public StringSender() {
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // RUN:
        // Num while (!clientSocket.isClosed()) out.writeObject(), out.flush();
        // catch: IOException, InterruptedException
        public void run() {
            try {
                while (!clientSocket.isClosed()) {
                    System.out.println("Cliente Pacote enviado");
                    out.writeObject("Cliente Pacote recebido");
                    out.flush();
                    Thread.sleep(2000);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
