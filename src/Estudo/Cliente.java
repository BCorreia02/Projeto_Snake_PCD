package Estudo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/* 1 STREAM --> Thread Cliente (Send/Receive)
 * 2 STREAMS --> Thread Cliente (Receive) , Thread Sender (Send)
 * 
 * 
 * 
 * 
 */

public class Cliente extends Thread {

    InetAddress addr;
    static final int porto = 8081;
    Socket clientSocket;
    ObjectInputStream in;

    public Cliente() throws UnknownHostException {
        this.addr = InetAddress.getByName("127.0.0.1");
    }

    @Override
    public void run() {
        try {
            clientSocket = new Socket(addr, porto);
            StringSender sender = new StringSender();
            sender.start();
            in = new ObjectInputStream(clientSocket.getInputStream());
            while (!clientSocket.isClosed()) {
                System.out.println((String) in.readObject());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]) throws UnknownHostException {
        Cliente client = new Cliente();
        client.start();
    }

    public class StringSender extends Thread {

        ObjectOutputStream out;

        public StringSender() {
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                while (!clientSocket.isClosed()) {
                    System.out.println("Cliente Pacote enviado");
                    out.writeObject("Cliente Pacote recebido");
                    out.flush();
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
