package src.solver.solverImpl;

import src.models.Cell;
import src.models.CellState;
import src.solver.MazeSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MazeSolverRecursivoCompleto implements MazeSolver {

    @Override
    public String getName() {
        return "Recursivo Completo";
    }

    private List<Cell> finalPath;
    private List<Cell> allVisitedSteps;
    private Cell[][] tempMaze;
    private Cell endCell;

    @Override
    public List<Cell> solve(Cell[][] maze, int startRow, int startCol, int endRow, int endCol) {
        finalPath = new ArrayList<>();
        allVisitedSteps = new ArrayList<>(); // No se usa para `solve` pero se inicializa
        tempMaze = deepCopyMaze(maze);
        endCell = tempMaze[endRow][endCol];

        Cell startCell = tempMaze[startRow][startCol];

        // Usamos un mapa para reconstruir el camino, similar a BFS/DFS iterativo
        Map<Cell, Cell> cameFrom = new HashMap<>();
        cameFrom.put(startCell, null); // El inicio no tiene predecesor

        boolean found = findPathDFS(startCell, cameFrom);

        if (found) {
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

        Cell startCell = tempMaze[startRow][startCol];

        Map<Cell, Cell> cameFrom = new HashMap<>(); // Para la reconstrucción del camino interno
        cameFrom.put(startCell, null);

        // Inicia la exploración y guarda los pasos
        // La primera celda visitada es el inicio
        allVisitedSteps.add(startCell);

        findPathDFSAndCollectSteps(startCell, cameFrom);

        return allVisitedSteps;
    }

    // Auxiliar recursivo para `solve` (encuentra el camino y lo reconstruye)
    private boolean findPathDFS(Cell current, Map<Cell, Cell> cameFrom) {
        if (current.equals(endCell)) {
            return true; // Se encontró el destino
        }

        // Marcar como visitado en la copia temporal para evitar ciclos
        if (current.getState() != CellState.START && current.getState() != CellState.END) {
            current.setState(CellState.VISITED);
        }

        for (Cell neighbor : getNeighbors(tempMaze, current)) {
            if (neighbor.getState() != CellState.WALL &&
                    !cameFrom.containsKey(neighbor)) { // No visitado aún en este recorrido (o es el inicio)
                cameFrom.put(neighbor, current); // Registrar el predecesor
                if (findPathDFS(neighbor, cameFrom)) {
                    return true;
                }
            }
        }
        return false; // No se encontró camino desde esta celda
    }

    // Auxiliar recursivo para `solveAndGetSteps` (colecciona todos los pasos)
    private void findPathDFSAndCollectSteps(Cell current, Map<Cell, Cell> cameFrom) {
        if (current.equals(endCell)) {
            // Ya se encontró el final, no hay necesidad de explorar más allá desde esta rama
            return;
        }

        // Marcar como visitado en la copia temporal si no es START/END, y añadir a pasos
        // Importante: No añadir START/END si ya están como visitedSteps
        if (current.getState() != CellState.START && current.getState() != CellState.END) {
            if (current.getState() != CellState.VISITED) { // Evitar duplicados en allVisitedSteps si se revisita
                current.setState(CellState.VISITED);
                allVisitedSteps.add(current);
            } else {
                // Si ya fue visitado, pero llegamos por otro camino, no lo volvemos a añadir a allVisitedSteps,
                // pero podemos continuar la exploración si es relevante (para DFS completo, sí lo es).
                // Sin embargo, `cameFrom.containsKey` ya lo maneja al no volver a procesar.
            }
        }


        List<Cell> neighbors = getNeighbors(tempMaze, current);
        // Para comportamiento DFS, a veces se invierte el orden para simular la recursión
        // Collections.reverse(neighbors);

        for (Cell neighbor : neighbors) {
            if (neighbor.getState() != CellState.WALL && !cameFrom.containsKey(neighbor)) {
                cameFrom.put(neighbor, current);
                findPathDFSAndCollectSteps(neighbor, cameFrom);
            }
        }
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