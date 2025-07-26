package src.solver.solverImpl;

import src.models.Cell;
import src.models.CellState;
import src.solver.MazeSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MazeSolverRecursivo implements MazeSolver {

    @Override
    public String getName() {
        return "Recursivo";
    }

    // Para el método recursivo, usaremos variables de instancia para el camino
    // y los pasos durante la recursión, y luego las devolveremos.
    // Esto es un poco más complejo que en BFS/DFS iterativos.
    private List<Cell> currentPath;
    private List<Cell> currentVisitedSteps;
    private Cell[][] tempMaze;
    private Cell endCell;
    private boolean pathFound;

    @Override
    public List<Cell> solve(Cell[][] maze, int startRow, int startCol, int endRow, int endCol) {
        currentPath = new ArrayList<>();
        currentVisitedSteps = new ArrayList<>(); // No se usa para solve(), pero por si acaso.
        tempMaze = deepCopyMaze(maze);
        endCell = tempMaze[endRow][endCol];
        pathFound = false;

        Cell startCell = tempMaze[startRow][startCol];

        // Llamada a la función recursiva
        solveRecursiveHelper(startCell);

        // Si se encontró el camino, currentPath contiene el camino del inicio al fin
        return currentPath;
    }

    @Override
    public List<Cell> solveAndGetSteps(Cell[][] maze, int startRow, int startCol, int endRow, int endCol) {
        currentPath = new ArrayList<>();
        currentVisitedSteps = new ArrayList<>();
        tempMaze = deepCopyMaze(maze);
        endCell = tempMaze[endRow][endCol];
        pathFound = false;

        Cell startCell = tempMaze[startRow][startCol];

        // La llamada a la función recursiva también llenará currentVisitedSteps
        solveRecursiveHelper(startCell);

        // Devolvemos todos los pasos visitados.
        // Si no se encontró un camino, solo contendrá la exploración.
        return currentVisitedSteps;
    }

    // Método auxiliar recursivo
    private void solveRecursiveHelper(Cell current) {
        if (pathFound) { // Si ya encontramos el camino, detenemos más exploración recursiva
            return;
        }

        if (current.getState() == CellState.WALL) {
            return; // No se puede pasar por una pared
        }

        // Marcar como visitado en la copia temporal, si no es START/END
        if (current.getState() != CellState.START && current.getState() != CellState.END) {
            // Solo añadir si no ha sido visitado ya para evitar ciclos en visitedSteps para la animación
            if (current.getState() != CellState.VISITED) {
                current.setState(CellState.VISITED);
                currentVisitedSteps.add(current); // Añadir a los pasos de exploración
            } else {
                return; // Ya visitado en este camino recursivo
            }
        } else {
            // Si es START/END, solo lo añadimos a visitedSteps si no está ya
            if (!currentVisitedSteps.contains(current)) {
                currentVisitedSteps.add(current);
            }
        }


        currentPath.add(current); // Añadir la celda actual al camino temporal

        if (current.equals(endCell)) {
            pathFound = true;
            return; // Se encontró el destino
        }

        // Obtener vecinos y explorar recursivamente
        // El orden de los vecinos influye en el comportamiento de la "profundidad"
        List<Cell> neighbors = getNeighbors(tempMaze, current);

        for (Cell neighbor : neighbors) {
            // Asegurarse de que no sea una pared y que no haya sido visitado en este recorrido
            // (o si es START/END, que no se haya procesado como "visitado" en la copia)
            if (neighbor.getState() != CellState.WALL &&
                    (neighbor.getState() != CellState.VISITED || neighbor.equals(endCell))) { // Puede volver a un VISITADO si es el END
                solveRecursiveHelper(neighbor);
                if (pathFound) {
                    return; // Propagar la señal de que el camino fue encontrado
                }
            }
        }

        // Si llegamos aquí y no se encontró el camino, hacemos "backtrack"
        // Quitar la celda actual del camino si no condujo a una solución
        if (!pathFound && !currentPath.isEmpty() && currentPath.get(currentPath.size() - 1).equals(current)) {
            currentPath.remove(currentPath.size() - 1);
        }
    }


    // Reutilizar métodos auxiliares
    private List<Cell> getNeighbors(Cell[][] maze, Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int r = cell.getRow();
        int c = cell.getCol();
        int rows = maze.length;
        int cols = maze[0].length;

        // Orden de exploración: Arriba, Derecha, Abajo, Izquierda (típico para recursivos)
        if (r > 0) neighbors.add(maze[r - 1][c]);
        if (c < cols - 1) neighbors.add(maze[r][c + 1]);
        if (r < rows - 1) neighbors.add(maze[r + 1][c]);
        if (c > 0) neighbors.add(maze[r][c - 1]);

        return neighbors;
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