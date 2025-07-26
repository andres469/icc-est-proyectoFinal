package src.views;

import javax.swing.*;
import java.awt.*;

public class ResultadosDialog extends JDialog {

    public ResultadosDialog(JFrame parent, String title, String message) {
        super(parent, title, true);
        setLayout(new BorderLayout());
        setSize(400, 200);
        setLocationRelativeTo(parent);

        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}