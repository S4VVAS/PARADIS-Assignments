// Peter Idestam-Almquist, 2021-03-07.
// Server, multi-threaded, accepting several simultaneous clients.

package assignment4;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class ChatServer implements Runnable {
	private final static int PORT = 8000;
	//private final static int MAX_CLIENTS = 5;
	//private final static Executor executor = Executors.newFixedThreadPool(MAX_CLIENTS);
	private static ServerSocket serverSocket;
	private static ClientHandler clientHandler;
	private static Thread cht;
	
	private static CopyOnWriteArrayList<Client> clients = new CopyOnWriteArrayList<Client>(); //For quick iteration
	private static Deque<String> messageQueue = new LinkedList<String>();
	private static boolean run = true;
	
	private ChatServer() {
		try {
			serverSocket = new ServerSocket(PORT);
			clientHandler = new ClientHandler();
			cht = new Thread(clientHandler);
			cht.start();
			new Thread(() -> {while(run){acceptClient();}}).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			run = false;
		}
	}
	
	private void acceptClient() {
		try {
			Socket clientSocket = serverSocket.accept();
			String clientName = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
			clients.add(new Client(clientSocket, clientName));
		} catch (IOException e) {
			e.printStackTrace();
			run = false;
		}
	}
	
	

	public void run() {
		Socket clientSocket = serverSocket.accept();
		
		SocketAddress remoteSocketAddress = clientSocket.getRemoteSocketAddress();
		SocketAddress localSocketAddress = clientSocket.getLocalSocketAddress();
		System.out.println("Accepted client " + remoteSocketAddress + " (" + localSocketAddress + ").");

		PrintWriter socketWriter = null;
		BufferedReader socketReader = null;

		try {
			socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
			socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			String threadInfo = " (" + Thread.currentThread().getName() + ").";
			String inputLine = socketReader.readLine();
			System.out.println("Received: \"" + inputLine + "\" from " + remoteSocketAddress + threadInfo);

			// First message is client name.
			//clientName = inputLine;

			while (inputLine != null) {
				socketWriter.println(inputLine);
				System.out.println("Sent: \"" + inputLine + "\" to " + clientName + " " + remoteSocketAddress + threadInfo);
				inputLine = socketReader.readLine();
				System.out.println("Received: \"" + inputLine + "\" from " + clientName + " " + remoteSocketAddress + threadInfo);
			}
			System.out.println("Closing connection " + remoteSocketAddress + " (" + localSocketAddress + ").");
		} catch (Exception exception) {
			System.out.println(exception);
		} finally {
			try {
				if (socketWriter != null)
					socketWriter.close();
				if (socketReader != null)
					socketReader.close();
				if (clientSocket != null)
					clientSocket.close();
			} catch (Exception exception) {
				System.out.println(exception);
			}
		}
	}
	
	

	public static void main(String[] args) {
		System.out.println("EchoServer started.");

		
		try {
			

			while (true) {
				serverSocket.accept();
				executor.execute(new ChatServer());
			}
		} catch (Exception exception) {
			System.out.println(exception);
		} finally {
			try {
				if (serverSocket != null)
					serverSocket.close();
			} catch (Exception exception) {
				System.out.println(exception);
			}
		}
	}
	
	private class Client{
		public final Socket clientSocket;
		public final String clientName;
		
		public Client(Socket socket, String name) {
			clientSocket = socket;
			clientName = name;
		}
	}
	
	private class ClientHandler implements Runnable{ //Accepts clients and sends messages to clients
		
		@Override
		public void run() {
			while(run) {
				//Check if message in queue
			}
			//Send closing message to clients;
		}
		
	}
	
}
