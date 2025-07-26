package src.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import src.models.Cell; // Asegúrate de importar tus clases de modelo
// import src.models.Maze; // Si tienes una clase Maze para el modelo completo

public class MazeFrame extends JFrame {

    private MazePanel mazePanel;
    private JComboBox<String> algorithmComboBox;
    // ... otros botones y componentes

    // Aquí podrías tener una referencia a tu objeto Maze (el modelo)
    // private Maze currentMaze;

    public MazeFrame(int numRows, int numCols) {
        super("Maze Creator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Inicializar tu modelo de laberinto aquí
        // currentMaze = new Maze(numRows, numCols);
        // O pasar las dimensiones al MazePanel para que lo inicialice

        initComponents(numRows, numCols); // Pasar dimensiones para que MazePanel las use
    }

    private void initComponents(int numRows, int numCols) {
        // Panel superior con botones
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton setStartButton = new JButton("Set Start");
        JButton setEndButton = new JButton("Set End");
        JButton toggleWallButton = new JButton("Toggle Wall");
        topPanel.add(setStartButton);
        topPanel.add(setEndButton);
        topPanel.add(toggleWallButton);
        add(topPanel, BorderLayout.NORTH);

        // MazePanel para dibujar el laberinto
        // Le pasamos las dimensiones para que él se encargue de crear la cuadrícula visual
        mazePanel = new MazePanel(numRows, numCols);
        add(new JScrollPane(mazePanel), BorderLayout.CENTER);

        // Panel inferior con controles de algoritmo y resolución
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(new JLabel("Algoritmo:"));
        String[] algorithms = {"Recursivo", "Recursivo Completo", "Recursivo Completo BT", "BFS", "DFS", "Backtracking"};
        algorithmComboBox = new JComboBox<>(algorithms);
        algorithmComboBox.setSelectedItem("Recursivo");
        bottomPanel.add(algorithmComboBox);

        JButton solveButton = new JButton("Resolver");
        JButton stepByStepButton = new JButton("Paso a paso");
        JButton clearButton = new JButton("Limpiar");
        bottomPanel.add(solveButton);
        bottomPanel.add(stepByStepButton);
        bottomPanel.add(clearButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Barra de menú
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

        // --- ActionListeners ---
        setStartButton.addActionListener(e -> {
            // Indicar al MazePanel que estamos en modo "Set Start"
            mazePanel.setInteractionMode(MazePanel.Interaction_Mode.SET_START);
            System.out.println("Modo: Establecer Inicio");
        });

        setEndButton.addActionListener(e -> {
            // Indicar al MazePanel que estamos en modo "Set End"
            mazePanel.setInteractionMode(MazePanel.Interaction_Mode.SET_END);
            System.out.println("Modo: Establecer Fin");
        });

        toggleWallButton.addActionListener(e -> {
            // Indicar al MazePanel que estamos en modo "Toggle Wall"
            mazePanel.setInteractionMode(MazePanel.Interaction_Mode.TOGGLE_WALL);
            System.out.println("Modo: Alternar Pared");
        });

        solveButton.addActionListener(e -> {
            System.out.println("Resolver clicked");
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
            // Lógica para resolver:
            // 1. Obtener el laberinto actual del mazePanel (o de tu modelo central)
            // 2. Seleccionar el MazeSolver apropiado según 'selectedAlgorithm'
            // 3. Llamar al método solve()
            // 4. Obtener el camino y pasárselo al mazePanel para dibujar
            // 5. Mostrar resultados en ResultadosDialog
            JOptionPane.showMessageDialog(this, "Resolviendo con " + selectedAlgorithm, "Resolver", JOptionPane.INFORMATION_MESSAGE);
            // Ejemplo de cómo podrías mostrar un diálogo de resultados:
            // ResultadosDialog dialog = new ResultadosDialog(this, "Resultados de la Solución", "Camino encontrado, nodos visitados, etc.");
            // dialog.setVisible(true);
        });

        stepByStepButton.addActionListener(e -> {
            System.out.println("Paso a paso clicked");
            // Lógica para resolución paso a paso (más compleja, requiere Timer o similar)
        });

        clearButton.addActionListener(e -> {
            System.out.println("Limpiar clicked");
            mazePanel.clearMaze(); // Indicar al panel que limpie el laberinto
            // Aquí podrías preguntar al usuario si quiere cambiar las dimensiones
            // si numRows o numCols se vuelven 0, volver a pedir como en MazeApp
        });

        algorithmComboBox.addActionListener(e -> {
            String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
            System.out.println("Algoritmo seleccionado: " + selectedAlgorithm);
        });
    }

    // Métodos para interactuar con el MazePanel o actualizar la vista
    // public void updateMazeDisplay(List<Cell> path) {
    //     mazePanel.drawPath(path);
    // }
}