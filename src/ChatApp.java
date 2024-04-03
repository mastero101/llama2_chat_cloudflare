import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatApp extends JFrame {
    private JTextField messageField;
    private JTextPane chatPane;
    private StringBuilder chatContent;

    public ChatApp() {
        setTitle("Chat App");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        messageField = new JTextField();
        JButton sendButton = new JButton("Enviar");
        sendButton.addActionListener(new SendButtonListener());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setContentType("text/html");
        chatContent = new StringBuilder();

        JScrollPane scrollPane = new JScrollPane(chatPane);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                String response = sendMessageToApi(message);
                displayChatMessage("User", message);
                displayChatMessage("Bot", extractResponse(response));
                messageField.setText("");
            }
        }
    }

    private String extractResponse(String jsonResponse) {
        JSONObject jsonObject = new JSONArray(jsonResponse).getJSONObject(0);
        JSONObject responseObj = jsonObject.getJSONObject("response");
        return responseObj.getString("response");
    }

    private void displayChatMessage(String sender, String message) {
        String htmlMessage = "<div style=\"margin: 5px 0; padding: 5px; background-color: ";
        htmlMessage += sender.equals("User") ? "#D3D3D3" : "#ADD8E6";
        htmlMessage += "; border-radius: 10px;\">";
        htmlMessage += "<strong>" + sender + ":</strong> " + message + "</div>";

        chatContent.append(htmlMessage);
        chatPane.setText(chatContent.toString());
    }

    private String sendMessageToApi(String message) {
        try {
            URL url = new URL("https://boldaiapi.castro-alejandro17.workers.dev");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String requestBody = "{\"message\": \"" + message + "\"}";
            OutputStream os = con.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error al enviar el mensaje";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatApp chatApp = new ChatApp();
            chatApp.setVisible(true);
        });
    }
}



