package src.controllers;

import src.models.Cell;
import src.models.CellState;
import src.solver.MazeSolver;
import src.solver.solverImpl.MazeSolverBFS;
import src.solver.solverImpl.MazeSolverDFS;
import src.solver.solverImpl.MazeSolverRecursivo;
import src.solver.solverImpl.MazeSolverRecursivoCompleto;
import src.solver.solverImpl.MazeSolverRecursivoCompletoBT;
import src.views.MazePanel;
import src.views.ResultadosDialog;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class MazeController {

    private MazePanel mazePanel;
    private JFrame parentFrame;

    // Estos atributos son públicos para que MazeFrame pueda verificar su estado
    public List<Cell> currentAlgorithmVisitedSteps; // Usado para la animación de exploración (gris)
    public List<Cell> currentAlgorithmFinalPath;   // Usado para el camino final (azul)
    public int currentStepIndex;

    private Timer animationTimer; // Un solo Timer para ambas animaciones automáticas/semi-automáticas

    public MazeController(MazePanel mazePanel, JFrame parentFrame) {
        this.mazePanel = mazePanel;
        this.parentFrame = parentFrame;
    }

    // Método para la resolución automática y animación completa (Botón "Resolver")
    // Ahora funciona como una versión automática del "Paso a paso"
    public void solveMazeAutomatically(String algorithmName) {
        // Asegúrate de detener cualquier animación previa si está corriendo
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        resetSimulationState(); // Limpiar el estado de simulación anterior

        Cell[][] mazeData = mazePanel.getMazeData();

        int startRow = -1, startCol = -1;
        int endRow = -1, endCol = -1;
        for (int r = 0; r < mazeData.length; r++) {
            for (int c = 0; c < mazeData[0].length; c++) {
                if (mazeData[r][c].getState() == CellState.START) {
                    startRow = r;
                    startCol = c;
                } else if (mazeData[r][c].getState() == CellState.END) {
                    endRow = r;
                    endCol = c;
                }
            }
        }

        if (startRow == -1 || endRow == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Debe establecer el punto de inicio y fin del laberinto.", "Error de Solución", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MazeSolver solver = getSolverInstance(algorithmName);
        if (solver == null) {
            JOptionPane.showMessageDialog(parentFrame, "Algoritmo no soportado: " + algorithmName, "Error de Algoritmo", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mazePanel.resetPathAndVisitedStates(); // Limpiar cualquier rastro de caminos o visitas anteriores

        // Obtener la lista COMPLETA de pasos de exploración Y el camino final
        currentAlgorithmVisitedSteps = solver.solveAndGetSteps(mazeData, startRow, startCol, endRow, endCol);
        currentAlgorithmFinalPath = solver.solve(mazeData, startRow, startCol, endRow, endCol);

        currentStepIndex = 0;

        if (currentAlgorithmVisitedSteps.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "No se encontró un camino o no hay pasos para mostrar.", "Sin Solución", JOptionPane.INFORMATION_MESSAGE);
            currentAlgorithmFinalPath = null;
            return;
        }

        // Iniciar el Timer para la animación automática paso a paso
        animationTimer = new Timer(100, e -> { // Velocidad de la animación (ej. 100ms por celda)
            if (currentStepIndex < currentAlgorithmVisitedSteps.size()) {
                Cell stepCell = currentAlgorithmVisitedSteps.get(currentStepIndex);

                // La lógica de pintado es la misma que en advanceStep()
                if (isCellInPath(stepCell, currentAlgorithmFinalPath) && stepCell.getState() != CellState.START && stepCell.getState() != CellState.END) {
                    mazePanel.updateCellState(stepCell.getRow(), stepCell.getCol(), CellState.PATH); // Azul si es parte del camino
                } else if (stepCell.getState() != CellState.START && stepCell.getState() != CellState.END) {
                    mazePanel.updateCellState(stepCell.getRow(), stepCell.getCol(), CellState.VISITED); // Gris si es solo visitado
                }
                currentStepIndex++;
            } else {
                animationTimer.stop();
                mazePanel.drawPath(currentAlgorithmFinalPath); // Asegurar que el camino final esté completamente en azul
                JOptionPane.showMessageDialog(parentFrame, "Resolución automática completada.", "Fin de Resolución", JOptionPane.INFORMATION_MESSAGE);
                resetSimulationState(); // Limpiar el estado después de la animación
            }
        });
        animationTimer.start();
    }


    // Método para iniciar la preparación para el paso a paso manual (Botón "Paso a paso")
    // Este método solo prepara los datos, no inicia un Timer.
    public void prepareStepByStep(String algorithmName) {
        // Asegúrate de detener cualquier animación automática en curso
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        resetSimulationState(); // Limpiar el estado de simulación anterior

        Cell[][] mazeData = mazePanel.getMazeData();

        int startRow = -1, startCol = -1;
        int endRow = -1, endCol = -1;
        for (int r = 0; r < mazeData.length; r++) {
            for (int c = 0; c < mazeData[0].length; c++) {
                if (mazeData[r][c].getState() == CellState.START) {
                    startRow = r;
                    startCol = c;
                } else if (mazeData[r][c].getState() == CellState.END) {
                    endRow = r;
                    endCol = c;
                }
            }
        }

        if (startRow == -1 || endRow == -1) {
            JOptionPane.showMessageDialog(parentFrame, "Debe establecer el punto de inicio y fin del laberinto.", "Error de Solución", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MazeSolver solver = getSolverInstance(algorithmName);
        if (solver == null) {
            JOptionPane.showMessageDialog(parentFrame, "Algoritmo no soportado: " + algorithmName, "Error de Algoritmo", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mazePanel.resetPathAndVisitedStates(); // Limpiar cualquier rastro de caminos o visitas anteriores

        // Obtener la lista COMPLETA de pasos de exploración Y el camino final
        currentAlgorithmVisitedSteps = solver.solveAndGetSteps(mazeData, startRow, startCol, endRow, endCol);
        currentAlgorithmFinalPath = solver.solve(mazeData, startRow, startCol, endRow, endCol);

        currentStepIndex = 0; // Reiniciar índice para el paso a paso
        if (currentAlgorithmVisitedSteps.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "No hay pasos para mostrar para este laberinto.", "Sin Pasos", JOptionPane.INFORMATION_MESSAGE);
            currentAlgorithmFinalPath = null;
            return;
        }
        JOptionPane.showMessageDialog(parentFrame, "Listo para simulación paso a paso. Haga clic en 'Paso a paso' para avanzar.", "Paso a Paso", JOptionPane.INFORMATION_MESSAGE);
    }

    // Método para avanzar un paso en la simulación manual (Cada clic en "Paso a paso")
    public void advanceStep() {
        if (currentAlgorithmVisitedSteps == null || currentStepIndex >= currentAlgorithmVisitedSteps.size()) {
            JOptionPane.showMessageDialog(parentFrame, "Simulación paso a paso completada. El camino final ya está resaltado o no hay pasos para iniciar. Presione 'Resolver' o 'Limpiar' para iniciar una nueva.", "Fin de Pasos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Cell stepCell = currentAlgorithmVisitedSteps.get(currentStepIndex);

        // La lógica de pintado es la misma que en solveMazeAutomatically()
        if (isCellInPath(stepCell, currentAlgorithmFinalPath) && stepCell.getState() != CellState.START && stepCell.getState() != CellState.END) {
            mazePanel.updateCellState(stepCell.getRow(), stepCell.getCol(), CellState.PATH); // Azul si es parte del camino
        } else if (stepCell.getState() != CellState.START && stepCell.getState() != CellState.END) {
            mazePanel.updateCellState(stepCell.getRow(), stepCell.getCol(), CellState.VISITED); // Gris si es solo visitado
        }
        currentStepIndex++;

        // Si es el último paso o se llega al final de los pasos de exploración,
        // entonces mostramos el camino final completo en azul.
        if (currentStepIndex == currentAlgorithmVisitedSteps.size()) {
            mazePanel.drawPath(currentAlgorithmFinalPath); // Asegura que el camino final esté completamente azul
            JOptionPane.showMessageDialog(parentFrame, "Simulación paso a paso completada. Camino final resaltado.", "Fin de Simulación", JOptionPane.INFORMATION_MESSAGE);
            resetSimulationState(); // Limpiar el estado después de que la simulación manual ha terminado
        }
    }

    // Método para resetear el estado interno del controlador
    public void resetSimulationState() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        currentAlgorithmVisitedSteps = null;
        currentAlgorithmFinalPath = null;
        currentStepIndex = 0;
        mazePanel.resetPathAndVisitedStates(); // Limpiar la UI también
    }


    private MazeSolver getSolverInstance(String algorithmName) {
        if (Objects.equals(algorithmName, "BFS")) {
            return new MazeSolverBFS();
        } else if (Objects.equals(algorithmName, "DFS")) {
            return new MazeSolverDFS();
        } else if (Objects.equals(algorithmName, "Recursivo")) {
            return new MazeSolverRecursivo();
        } else if (Objects.equals(algorithmName, "Recursivo Completo")) {
            return new MazeSolverRecursivoCompleto();
        } else if (Objects.equals(algorithmName, "Recursivo Completo BT")) {
            return new MazeSolverRecursivoCompletoBT();
        }
        return null;
    }

    private boolean isCellInPath(Cell cell, List<Cell> path) {
        if (path == null) return false;
        return path.contains(cell);
    }
}