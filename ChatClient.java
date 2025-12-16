import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClient {
    private JFrame frame;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton sendButton;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String name;

    public ChatClient(String serverAddress, int port) {
        initializeGUI();
        connectToServer(serverAddress, port);
    }

    private void initializeGUI() {
        frame = new JFrame("Chat Application| Distributed System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(new Color(66, 133, 244));
        sendButton.setForeground(Color.WHITE);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
    }

    private void connectToServer(String serverAddress, int port) {
    try {
        socket = new Socket(serverAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        name = JOptionPane.showInputDialog(frame, in.readLine(), "Enter your name", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Name cannot be empty. Exiting.");
            System.exit(0);
        }

        out.println(name);

        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    String[] parts = message.split("\\|", 2);
                    if (parts.length == 2) {
                        String sender = parts[0];
                        String text = parts[1];
                        boolean isSelf = sender.equals(name);
                        addMessage(sender + ": " + text, isSelf);
                    } else {
                        // system/broadcast messages like "🔴 Alice has left the chat"
                        addMessage(message, false);
                    }
                }
            } catch (IOException e) {
                addMessage("❌ Connection lost.", false);
            }
        }).start();

    } catch (IOException e) {
        JOptionPane.showMessageDialog(frame, "🚫 Cannot connect to server: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            inputField.setText("");
        }
    }

    private void addMessage(String message, boolean isSelf) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(Color.WHITE);

        String time = new SimpleDateFormat("HH:mm").format(new Date());
        JLabel messageLabel = new JLabel("<html><p style='width: 200px'>" + message + "</p></html>");
        messageLabel.setOpaque(true);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Color.BLACK);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        timeLabel.setForeground(Color.GRAY);

        if (isSelf) {
            messageLabel.setBackground(new Color(220, 248, 198));  // Light green
            messagePanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        } else {
            messageLabel.setBackground(new Color(240, 240, 240));  // Light gray
            messagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        messagePanel.add(messageLabel);
        messagePanel.add(timeLabel);

        SwingUtilities.invokeLater(() -> {
            chatPanel.add(messagePanel);
            chatPanel.add(Box.createVerticalStrut(10));
            chatPanel.revalidate();
            scrollToBottom();
        });
    }

    private void scrollToBottom() {
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClient("localhost", 12345));
    }
}
