package src;

import src.views.MazeFrame;
import javax.swing.*;

public class MazeApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int numRows = 0;
            int numCols = 0;

            String rowsStr = JOptionPane.showInputDialog(null, "Ingrese número de filas:", "Input", JOptionPane.QUESTION_MESSAGE);
            if (rowsStr != null && !rowsStr.trim().isEmpty()) {
                try {
                    numRows = Integer.parseInt(rowsStr.trim());
                    if (numRows <= 0) {
                        JOptionPane.showMessageDialog(null, "El número de filas debe ser mayor que 0.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                        numRows = 0;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Entrada inválida. Ingrese un número entero para las filas.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                    numRows = 0;
                }
            }

            if (numRows > 0) {
                String colsStr = JOptionPane.showInputDialog(null, "Ingrese número de columnas:", "Input", JOptionPane.QUESTION_MESSAGE);
                if (colsStr != null && !colsStr.trim().isEmpty()) {
                    try {
                        numCols = Integer.parseInt(colsStr.trim());
                        if (numCols <= 0) {
                            JOptionPane.showMessageDialog(null, "El número de columnas debe ser mayor que 0.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                            numCols = 0;
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Entrada inválida. Ingrese un número entero para las columnas.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                        numCols = 0;
                    }
                }
            }

            if (numRows > 0 && numCols > 0) {
                MazeFrame mazeFrame = new MazeFrame(numRows, numCols);
                mazeFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Dimensiones de laberinto no válidas. Saliendo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}