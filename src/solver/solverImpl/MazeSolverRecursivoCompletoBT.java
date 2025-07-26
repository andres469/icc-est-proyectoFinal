package src.solver.solverImpl;

import src.models.Cell;
import src.models.CellState;
import src.solver.MazeSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MazeSolverRecursivoCompletoBT implements MazeSolver {

    @Override
    public String getName() {
        return "Recursivo Completo BT";
    }

    private List<Cell> finalPath; // Para solve()
    private List<Cell> allVisitedSteps; // Para solveAndGetSteps()
    private Cell[][] tempMaze;
    private Cell endCell;
    private boolean pathFound; // Para detener la búsqueda una vez que se encuentra un camino

    @Override
    public List<Cell> solve(Cell[][] maze, int startRow, int startCol, int endRow, int endCol) {
        finalPath = new ArrayList<>();
        allVisitedSteps = new ArrayList<>(); // No usado para `solve` directamente
        tempMaze = deepCopyMaze(maze);
        endCell = tempMaze[endRow][endCol];
        pathFound = false;

        Cell startCell = tempMaze[startRow][startCol];

        // Usamos un mapa para reconstruir el camino con el backtracking
        Map<Cell, Cell> cameFrom = new HashMap<>();
        cameFrom.put(startCell, null);

        findPathDFSWithBacktracking(startCell, cameFrom);

        if (pathFound) {
            return reconstructPath(cameFrom, startCell, endCell);
        }
        return new ArrayList<>(); // No se encontró camino
    }

    @Override
    public List<Cell> solveAndGetSteps(Cell[][] maze, int startRow, int startCol, int endRow, int endCol) {
        finalPath = new ArrayList<>();
        allVisitedSteps = new ArrayList<>();
        tempMaze = deepCopyMaze(maze);
        endCell = tempMaze[endRow][endCol];
        pathFound = false;

        Cell startCell = tempMaze[startRow][startCol];

        Map<Cell, Cell> cameFrom = new HashMap<>();
        cameFrom.put(startCell, null);

        allVisitedSteps.add(startCell); // El inicio es el primer paso
        findPathDFSWithBacktrackingAndCollectSteps(startCell, cameFrom);

        return allVisitedSteps;
    }

    // Auxiliar recursivo para `solve` con backtracking
    private void findPathDFSWithBacktracking(Cell current, Map<Cell, Cell> cameFrom) {
        if (pathFound) {
            return; // Ya encontramos el camino, no sigas explorando
        }

        if (current.equals(endCell)) {
            pathFound = true;
            return;
        }

        // Marcar como visitado en la copia temporal
        if (current.getState() != CellState.START && current.getState() != CellState.END) {
            current.setState(CellState.VISITED);
        }

        List<Cell> neighbors = getNeighbors(tempMaze, current);
        // Podríamos barajar los vecinos para un comportamiento más "aleatorio" o probar diferentes caminos
        // Collections.shuffle(neighbors); // Descomentar si quieres un comportamiento de "exploración" más dinámico

        for (Cell neighbor : neighbors) {
            if (neighbor.getState() != CellState.WALL && !cameFrom.containsKey(neighbor)) {
                cameFrom.put(neighbor, current); // Marcar como visitado y registrar predecesor
                findPathDFSWithBacktracking(neighbor, cameFrom);
                if (pathFound) {
                    return; // Propagar la señal de que el camino fue encontrado
                }
            }
        }

        // Si esta celda no llevó a la solución, "desmarcarla" (backtracking)
        // Esto es importante para algoritmos que retroceden y prueban otras ramas.
        // Pero para la visualización de "pasos visitados", no solemos "despintar" la celda.
        // Aquí, simplemente no la mantenemos en el "cameFrom" si no lleva a la solución.
        // NO cambiamos el estado VISITED aquí para la animación, la celda *fue* visitada.
        // cameFrom.remove(current); // Solo si queremos una búsqueda de múltiples caminos o la más corta
    }


    // Auxiliar recursivo para `solveAndGetSteps` con backtracking y recolección de pasos
    private void findPathDFSWithBacktrackingAndCollectSteps(Cell current, Map<Cell, Cell> cameFrom) {
        if (pathFound) {
            return;
        }

        // Registrar la visita si no es START/END y no ha sido visitada previamente en allVisitedSteps
        if (current.getState() != CellState.START && current.getState() != CellState.END) {
            if (current.getState() != CellState.VISITED) {
                current.setState(CellState.VISITED);
                allVisitedSteps.add(current);
            }
        }

        if (current.equals(endCell)) {
            pathFound = true;
            return;
        }

        List<Cell> neighbors = getNeighbors(tempMaze, current);
        // Collections.shuffle(neighbors); // Opcional: para un comportamiento de exploración diferente

        for (Cell neighbor : neighbors) {
            if (neighbor.getState() != CellState.WALL && !cameFrom.containsKey(neighbor)) {
                cameFrom.put(neighbor, current);
                findPathDFSWithBacktrackingAndCollectSteps(neighbor, cameFrom);
                if (pathFound) {
                    return;
                }
            }
        }
        // No necesitamos "desmarcar" VISITED en `tempMaze` ni remover de `allVisitedSteps` aquí,
        // ya que `allVisitedSteps` registra el historial de exploración, no el camino activo.
    }

    // Reutilizar métodos auxiliares
    private List<Cell> getNeighbors(Cell[][] maze, Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int r = cell.getRow();
        int c = cell.getCol();
        int rows = maze.length;
        int cols = maze[0].length;

        // Orden de exploración: Arriba, Derecha, Abajo, Izquierda
        if (r > 0) neighbors.add(maze[r - 1][c]);
        if (c < cols - 1) neighbors.add(maze[r][c + 1]);
        if (r < rows - 1) neighbors.add(maze[r + 1][c]);
        if (c > 0) neighbors.add(maze[r][c - 1]);

        return neighbors;
    }

    protected List<Cell> reconstructPath(Map<Cell, Cell> cameFrom, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        Cell current = end;
        while (current != null && cameFrom.containsKey(current) || current.equals(start)) {
            path.add(0, current);
            if (current.equals(start)) break;
            current = cameFrom.get(current);
        }
        if (!path.contains(start) && start != null) {
            path.add(0, start);
        }
        return path;
    }

    protected Cell[][] deepCopyMaze(Cell[][] originalMaze) {
        int rows = originalMaze.length;
        int cols = originalMaze[0].length;
        Cell[][] copy = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                copy[r][c] = new Cell(originalMaze[r][c].getRow(), originalMaze[r][c].getCol(), originalMaze[r][c].getState());
            }
        }
        return copy;
    }
}