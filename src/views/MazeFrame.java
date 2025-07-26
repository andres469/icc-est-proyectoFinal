package src.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import src.controllers.MazeController;
import src.models.Cell;

public class MazeFrame extends JFrame {

    private MazePanel mazePanel;
    private JComboBox<String> algorithmComboBox;
    private MazeController controller;
    private JButton toggleWallButton; // Declarar aquí para poder cambiar su texto/estado

    public MazeFrame(int numRows, int numCols) {
        super("Maze Creator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initComponents(numRows, numCols);
        controller = new MazeController(mazePanel, this);
    }

    private void initComponents(int numRows, int numCols) {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton setStartButton = new JButton("Set Start");
        JButton setEndButton = new JButton("Set End");
        toggleWallButton = new JButton("Toggle Wall"); // Asignar al atributo
        topPanel.add(setStartButton);
        topPanel.add(setEndButton);
        topPanel.add(toggleWallButton);
        add(topPanel, BorderLayout.NORTH);

        mazePanel = new MazePanel(numRows, numCols);
        add(new JScrollPane(mazePanel), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(new JLabel("Algoritmo:"));
        String[] algorithms = {"BFS", "DFS", "Recursivo", "Recursivo Completo", "Recursivo Completo BT"};
        algorithmComboBox = new JComboBox<>(algorithms);
        algorithmComboBox.setSelectedItem("BFS");
        bottomPanel.add(algorithmComboBox);

        JButton solveButton = new JButton("Resolver");
        JButton stepByStepButton = new JButton("Paso a paso");
        JButton clearButton = new JButton("Limpiar");
        bottomPanel.add(solveButton);
        bottomPanel.add(stepByStepButton);
        bottomPanel.add(clearButton);
        add(bottomPanel, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu archivoMenu = new JMenu("Archivo");
        JMenuItem salirItem = new JMenuItem("Salir");
        salirItem.addActionListener(e -> System.exit(0));
        archivoMenu.add(salirItem);
        JMenu ayudaMenu = new JMenu("Ayuda");
        JMenuItem acercaDeItem = new JMenuItem("Acerca de...");
        acercaDeItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Creador de Laberintos v1.0", "Acerca de", JOptionPane.INFORMATION_MESSAGE));
        ayudaMenu.add(acercaDeItem);
        menuBar.add(archivoMenu);
        menuBar.add(ayudaMenu);
        setJMenuBar(menuBar);

        // --- Manejo de la lógica de botones de interacción ---

        // Función auxiliar para resetear el modo actual del panel
        // y opcionalmente cambiar el texto del botón de pared
        ActionListener interactionModeSetter = e -> {
            JButton source = (JButton) e.getSource();
            if (source == setStartButton) {
                mazePanel.setInteractionMode(MazePanel.Interaction_Mode.SET_START);
                System.out.println("Modo: Establecer Inicio");
            } else if (source == setEndButton) {
                mazePanel.setInteractionMode(MazePanel.Interaction_Mode.SET_END);
                System.out.println("Modo: Establecer Fin");
            }
            // Si cualquier otro botón de interacción es presionado, desactivar el modo TOGGLE_WALL
            if (mazePanel.currentMode != MazePanel.Interaction_Mode.TOGGLE_WALL) {
                toggleWallButton.setText("Toggle Wall"); // Resetear el texto del botón de pared
            }
        };

        setStartButton.addActionListener(interactionModeSetter);
        setEndButton.addActionListener(interactionModeSetter);

        // Lógica para el botón Toggle Wall: Alterna el modo
        toggleWallButton.addActionListener(e -> {
            if (mazePanel.currentMode == MazePanel.Interaction_Mode.TOGGLE_WALL) {
                mazePanel.setInteractionMode(MazePanel.Interaction_Mode.NONE); // Desactivar el modo
                toggleWallButton.setText("Toggle Wall"); // Restaurar texto original
                System.out.println("Modo: Ninguno (Toggle Wall desactivado)");
            } else {
                mazePanel.setInteractionMode(MazePanel.Interaction_Mode.TOGGLE_WALL); // Activar el modo
                toggleWallButton.setText("Drawing Walls..."); // Cambiar texto para indicar que está activo
                System.out.println("Modo: Alternar Pared (activo)");
            }
        });

        // Asegurarse de que al presionar Resolver o Paso a paso, también se desactive el modo de dibujar paredes
        solveButton.addActionListener(e -> {
            mazePanel.setInteractionMode(MazePanel.Interaction_Mode.NONE); // Desactivar modo de dibujo
            toggleWallButton.setText("Toggle Wall"); // Restablecer el texto del botón
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
            controller.solveMazeAutomatically(selectedAlgorithm);
        });

        stepByStepButton.addActionListener(e -> {
            mazePanel.setInteractionMode(MazePanel.Interaction_Mode.NONE); // Desactivar modo de dibujo
            toggleWallButton.setText("Toggle Wall"); // Restablecer el texto del botón
            if (controller.currentAlgorithmVisitedSteps == null || controller.currentStepIndex == 0 || controller.currentStepIndex >= controller.currentAlgorithmVisitedSteps.size()) {
                String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
                controller.prepareStepByStep(selectedAlgorithm);
            } else {
                controller.advanceStep();
            }
        });

        clearButton.addActionListener(e -> {
            mazePanel.clearMaze();
            controller.resetSimulationState();
            mazePanel.setInteractionMode(MazePanel.Interaction_Mode.NONE); // Limpiar también el modo
            toggleWallButton.setText("Toggle Wall"); // Restaurar el texto del botón de pared
            System.out.println("Laberinto limpiado.");
        });

        algorithmComboBox.addActionListener(e -> {
            // Cuando se cambia el algoritmo, también se desactiva el modo de dibujo
            mazePanel.setInteractionMode(MazePanel.Interaction_Mode.NONE);
            toggleWallButton.setText("Toggle Wall");
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
            System.out.println("Algoritmo seleccionado: " + selectedAlgorithm);
        });
    }
}