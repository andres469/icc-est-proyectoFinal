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
    // Esto es aceptable para la comunicación Controller-View en Swing.
    public List<Cell> currentAlgorithmVisitedSteps; // Usado solo para "Paso a paso"
    public List<Cell> currentAlgorithmFinalPath;   // Usado para ambos, pero la animación del camino solo para "Resolver"
    public int currentStepIndex;

    private Timer autoPathTimer; // Renombrado para claridad: para la animación automática del camino

    public MazeController(MazePanel mazePanel, JFrame parentFrame) {
        this.mazePanel = mazePanel;
        this.parentFrame = parentFrame;
    }

    // Método para la resolución automática y animación del CAMINO FINAL (Botón "Resolver")
    public void solveMazeAndAnimatePath(String algorithmName) {
        // Detener cualquier animación previa si está corriendo
        if (autoPathTimer != null && autoPathTimer.isRunning()) {
            autoPathTimer.stop();
        }
        resetSimulationState(); // Limpiar el estado de simulación anterior

        Cell[][] mazeData = mazePanel.getMazeData(); // Obtener el laberinto actual

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

        mazePanel.resetPathAndVisitedStates(); // Limpiar solo los estados de recorrido (gris/azul)

        // *** AHORA SOLO OBTENEMOS EL CAMINO FINAL PARA ESTA FUNCIÓN ***
        currentAlgorithmFinalPath = solver.solve(mazeData, startRow, startCol, endRow, endCol);

        currentStepIndex = 0; // Reiniciar índice para la animación

        if (currentAlgorithmFinalPath.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "No se encontró un camino para el laberinto.", "Sin Solución", JOptionPane.INFORMATION_MESSAGE);
            currentAlgorithmFinalPath = null; // Asegurarse de limpiar
            return;
        }

        // Si hay un camino, iniciamos la animación del camino
        // El camino ya incluye START y END, pero queremos evitar pintarlos de azul
        // La animación debe comenzar después de START y terminar antes de END
        List<Cell> pathForAnimation = new ArrayList<>(currentAlgorithmFinalPath);
        if (!pathForAnimation.isEmpty()) {
            // Eliminar inicio y fin de la lista para la animación si existen en los extremos
            if (pathForAnimation.get(0).getState() == CellState.START) {
                pathForAnimation.remove(0);
            }
            if (!pathForAnimation.isEmpty() && pathForAnimation.get(pathForAnimation.size() - 1).getState() == CellState.END) {
                pathForAnimation.remove(pathForAnimation.size() - 1);
            }
        }

        // Guardar esta lista ajustada para el timer
        final List<Cell> finalPathToAnimate = pathForAnimation;

        autoPathTimer = new Timer(500, e -> { // 500ms (0.5 segundos) de delay
            if (currentStepIndex < finalPathToAnimate.size()) {
                Cell pathCell = finalPathToAnimate.get(currentStepIndex);
                // SOLO pintamos de PATH (azul), protegiendo START/END/WALL
                mazePanel.updateCellState(pathCell.getRow(), pathCell.getCol(), CellState.PATH);
                currentStepIndex++;
            } else {
                autoPathTimer.stop();
                // Una vez que la animación termina, aseguramos que todo el camino final (incluyendo START/END) esté pintado.
                // Sin embargo, MazePanel.drawPath ya protege START/END, así que esto es seguro.
                mazePanel.drawPath(currentAlgorithmFinalPath);
                JOptionPane.showMessageDialog(parentFrame, "Resolución automática completada. Camino encontrado.", "Fin de Resolución", JOptionPane.INFORMATION_MESSAGE);
                resetSimulationState(); // Limpiar el estado después de la animación automática
            }
        });
        autoPathTimer.start();
    }


    // Prepara los datos para la simulación paso a paso manual (Botón "Paso a paso")
    public void prepareStepByStep(String algorithmName) {
        // Asegúrate de detener cualquier animación automática en curso
        if (autoPathTimer != null && autoPathTimer.isRunning()) {
            autoPathTimer.stop();
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

        mazePanel.resetPathAndVisitedStates(); // Limpiar solo los estados de recorrido

        // Para paso a paso, necesitamos TODOS los pasos de la exploración (visitedSteps)
        currentAlgorithmVisitedSteps = solver.solveAndGetSteps(mazeData, startRow, startCol, endRow, endCol);
        currentAlgorithmFinalPath = solver.solve(mazeData, startRow, startCol, endRow, endCol); // También el camino final para el resaltado

        currentStepIndex = 0;
        if (currentAlgorithmVisitedSteps.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "No hay pasos para mostrar para este laberinto.", "Sin Pasos", JOptionPane.INFORMATION_MESSAGE);
            currentAlgorithmFinalPath = null;
            return;
        }
        JOptionPane.showMessageDialog(parentFrame, "Listo para simulación paso a paso. Haga clic en 'Paso a paso' para avanzar.", "Paso a Paso", JOptionPane.INFORMATION_MESSAGE);
    }

    // Avanza un solo paso en la simulación manual (Botón "Paso a paso")
    public void advanceStep() {
        if (currentAlgorithmVisitedSteps == null || currentStepIndex >= currentAlgorithmVisitedSteps.size()) {
            JOptionPane.showMessageDialog(parentFrame, "Simulación paso a paso completada. El camino final ya está resaltado o no hay pasos para iniciar. Presione 'Resolver' o 'Limpiar' para iniciar una nueva.", "Fin de Pasos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Cell stepCell = currentAlgorithmVisitedSteps.get(currentStepIndex);

        // Si la celda es parte del camino final Y NO ES START/END, la marcamos como PATH (azul)
        if (isCellInPath(stepCell, currentAlgorithmFinalPath) && stepCell.getState() != CellState.START && stepCell.getState() != CellState.END) {
            mazePanel.updateCellState(stepCell.getRow(), stepCell.getCol(), CellState.PATH);
        } else if (stepCell.getState() != CellState.START && stepCell.getState() != CellState.END) {
            // Si NO es parte del camino final Y NO ES START/END, la marcamos como VISITED (gris)
            mazePanel.updateCellState(stepCell.getRow(), stepCell.getCol(), CellState.VISITED);
        }
        // Incrementamos el índice para el siguiente paso
        currentStepIndex++;

        // Cuando todos los pasos de exploración han sido mostrados
        if (currentStepIndex == currentAlgorithmVisitedSteps.size()) {
            mazePanel.drawPath(currentAlgorithmFinalPath); // Asegura que el camino final esté completamente azul
            JOptionPane.showMessageDialog(parentFrame, "Simulación paso a paso completada. Camino final resaltado.", "Fin de Simulación", JOptionPane.INFORMATION_MESSAGE);
            resetSimulationState(); // Limpiar el estado después de que la simulación manual ha terminado
        }
    }

    // Método para resetear el estado interno del controlador
    public void resetSimulationState() {
        if (autoPathTimer != null && autoPathTimer.isRunning()) {
            autoPathTimer.stop();
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