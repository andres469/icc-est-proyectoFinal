package src.solver.solverImpl;

import src.models.Cell;
import src.models.CellState;
import src.solver.MazeSolver;

import java.util.*;

public class MazeSolverDFS implements MazeSolver {

    @Override
    public String getName() {
        return "DFS";
    }

    @Override
    public List<Cell> solve(Cell[][] maze, int startRow, int startCol, int endRow, int endCol) {
        Cell[][] tempMaze = deepCopyMaze(maze);
        Cell start = tempMaze[startRow][startCol];
        Cell end = tempMaze[endRow][endCol];

        Stack<Cell> stack = new Stack<>();
        Map<Cell, Cell> cameFrom = new HashMap<>();

        stack.push(start);
        cameFrom.put(start, null); // El inicio no tiene predecesor

        while (!stack.isEmpty()) {
            Cell current = stack.pop();

            if (current.equals(end)) {
                return reconstructPath(cameFrom, start, end);
            }

            if (current.getState() != CellState.START && current.getState() != CellState.END) {
                current.setState(CellState.VISITED); // Marcar como visitado en la copia
            }

            // Para DFS, el orden de los vecinos importa para la "profundidad".
            // Podríamos invertir la lista de vecinos para simular un comportamiento más "profundo"
            // si getNeighbors devuelve los vecinos en un orden específico.
            List<Cell> neighbors = getNeighbors(tempMaze, current);
            Collections.reverse(neighbors); // A menudo se revierte para un comportamiento más "Stack-like"

            for (Cell neighbor : neighbors) {
                if (neighbor.getState() != CellState.WALL && !cameFrom.containsKey(neighbor)) {
                    cameFrom.put(neighbor, current);
                    stack.push(neighbor);
                }
            }
        }
        return new ArrayList<>(); // No se encontró camino
    }

    @Override
    public List<Cell> solveAndGetSteps(Cell[][] maze, int startRow, int startCol, int endRow, int endCol) {
        Cell[][] tempMaze = deepCopyMaze(maze);
        Cell start = tempMaze[startRow][startCol];
        Cell end = tempMaze[endRow][endCol];

        Stack<Cell> stack = new Stack<>();
        Map<Cell, Cell> cameFrom = new HashMap<>();
        List<Cell> visitedSteps = new ArrayList<>();

        stack.push(start);
        cameFrom.put(start, null);
        visitedSteps.add(start); // El inicio es el primer paso

        while (!stack.isEmpty()) {
            Cell current = stack.pop();

            // Si ya lo visitamos (a través de otro camino que no pasó por la pila), lo saltamos
            // Esto es importante si el DFS puede visitar la misma celda múltiples veces en la pila
            // pero solo queremos registrarla una vez como "explorada".
            // Para DFS recursivo, es más fácil manejar esto. Para iterativo, `cameFrom` sirve como `visited`.
            if (current.getState() == CellState.VISITED && current != start && current != end) {
                continue;
            }


            if (current.equals(end)) {
                return visitedSteps; // Encontró el fin, devuelve los pasos de exploración hasta aquí
            }

            if (current.getState() != CellState.START && current.getState() != CellState.END) {
                current.setState(CellState.VISITED); // Marcar como visitado en la copia
            }

            List<Cell> neighbors = getNeighbors(tempMaze, current);
            Collections.reverse(neighbors); // Para que se comporte más como DFS (explorar un camino hasta el fondo)

            for (Cell neighbor : neighbors) {
                if (neighbor.getState() != CellState.WALL && !cameFrom.containsKey(neighbor)) {
                    cameFrom.put(neighbor, current);
                    stack.push(neighbor);
                    // Añadir a visitedSteps solo si no es START/END y se acaba de "descubrir"
                    if (neighbor.getState() != CellState.START && neighbor.getState() != CellState.END) {
                        visitedSteps.add(neighbor);
                    }
                }
            }
        }
        return visitedSteps; // Devuelve los pasos incluso si no se encontró camino
    }

    // Reutilizar métodos auxiliares de BFS
    private List<Cell> getNeighbors(Cell[][] maze, Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int r = cell.getRow();
        int c = cell.getCol();
        int rows = maze.length;
        int cols = maze[0].length;

        // Orden de exploración para DFS (ejemplo: siempre ir arriba, luego abajo, etc.)
        if (r > 0) neighbors.add(maze[r - 1][c]);     // Arriba
        if (r < rows - 1) neighbors.add(maze[r + 1][c]); // Abajo
        if (c > 0) neighbors.add(maze[r][c - 1]);     // Izquierda
        if (c < cols - 1) neighbors.add(maze[r][c + 1]); // Derecha

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