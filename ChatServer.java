import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        System.out.println("✅ Server started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Server error: " + e.getMessage());
        }
    }

    public static void broadcast(String sender, String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(sender + "|" + message);
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Enter your name:");
                clientName = in.readLine();

                broadcast("Server", "🔵 " + clientName + " has joined the chat!");

                String message;
                while ((message = in.readLine()) != null) {
                    broadcast(clientName, message);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + clientName);
            } finally {
                try {
                    socket.close();
                } catch (IOException ignored) {}
                removeClient(this);
                broadcast("Server", "🔴 " + clientName + " has left the chat.");
            }
        }
    }
}
