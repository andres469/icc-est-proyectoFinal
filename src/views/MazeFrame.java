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
        JButton toggleWallButton = new JButton("Toggle Wall");
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

        setStartButton.addActionListener(e -> {
            mazePanel.setInteractionMode(MazePanel.Interaction_Mode.SET_START);
            System.out.println("Modo: Establecer Inicio");
        });

        setEndButton.addActionListener(e -> {
            mazePanel.setInteractionMode(MazePanel.Interaction_Mode.SET_END);
            System.out.println("Modo: Establecer Fin");
        });

        toggleWallButton.addActionListener(e -> {
            mazePanel.setInteractionMode(MazePanel.Interaction_Mode.TOGGLE_WALL);
            System.out.println("Modo: Alternar Pared");
        });

        // Lógica para el botón "Resolver" (automático, animación solo del camino final)
        solveButton.addActionListener(e -> {
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
            controller.solveMazeAndAnimatePath(selectedAlgorithm); // <--- CAMBIO AQUÍ: Llamar al nuevo método
        });

        // Lógica para el botón "Paso a paso" (manual, con exploración gris y camino azul)
        stepByStepButton.addActionListener(e -> {
            if (controller.currentAlgorithmVisitedSteps == null || controller.currentStepIndex == 0) {
                String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
                controller.prepareStepByStep(selectedAlgorithm);
            } else {
                controller.advanceStep();
            }
        });

        clearButton.addActionListener(e -> {
            mazePanel.clearMaze();
            controller.resetSimulationState();
            System.out.println("Laberinto limpiado.");
        });

        algorithmComboBox.addActionListener(e -> {
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
            System.out.println("Algoritmo seleccionado: " + selectedAlgorithm);
        });
    }
}