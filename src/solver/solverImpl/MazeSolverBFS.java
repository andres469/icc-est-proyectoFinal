package src.solver.solverImpl;

import src.models.Cell;
import src.models.CellState;
import src.solver.MazeSolver;

import java.util.*;

public class MazeSolverBFS implements MazeSolver {

    @Override
    public String getName() {
        return "BFS";
    }

    // Método que devuelve SOLO el camino final
    @Override
    public List<Cell> solve(Cell[][] maze, int startRow, int startCol, int endRow, int endCol) {
        int rows = maze.length;
        int cols = maze[0].length;

        // Crear una copia del laberinto para el solver
        Cell[][] tempMaze = deepCopyMaze(maze);

        Cell start = tempMaze[startRow][startCol];
        Cell end = tempMaze[endRow][endCol];

        Queue<Cell> queue = new LinkedList<>();
        Map<Cell, Cell> cameFrom = new HashMap<>(); // Para reconstruir el camino

        queue.add(start);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            if (current.equals(end)) {
                return reconstructPath(cameFrom, start, end);
            }

            // Marcar como visitado en la copia para evitar ciclos en la búsqueda
            if (current.getState() != CellState.START && current.getState() != CellState.END) {
                current.setState(CellState.VISITED);
            }

            for (Cell neighbor : getNeighbors(tempMaze, current)) {
                // Solo añadir a la cola si no es muro y no ha sido visitado
                // También manejar START/END para que no se consideren "visitados" en la exploración
                if (neighbor.getState() != CellState.WALL &&
                        !cameFrom.containsKey(neighbor)) { // Si ya se llegó a él, no lo procesamos de nuevo
                    cameFrom.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return new ArrayList<>(); // No se encontró camino
    }

    // Método que devuelve TODOS los pasos de la exploración
    @Override
    public List<Cell> solveAndGetSteps(Cell[][] maze, int startRow, int startCol, int endRow, int endCol) {
        int rows = maze.length;
        int cols = maze[0].length;

        Cell[][] tempMaze = deepCopyMaze(maze);

        Cell start = tempMaze[startRow][startCol];
        Cell end = tempMaze[endRow][endCol];

        Queue<Cell> queue = new LinkedList<>();
        Map<Cell, Cell> cameFrom = new HashMap<>();
        List<Cell> visitedSteps = new ArrayList<>(); // Para almacenar el orden de visita

        queue.add(start);
        visitedSteps.add(start); // El inicio es el primer paso visitado

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            // Si llegamos al destino, la búsqueda termina, pero aún debemos reconstruir el camino
            // Esto es solo para la condición de parada de la búsqueda.
            if (current.equals(end)) {
                // Aquí, podrías añadir los pasos restantes del camino final si lo deseas
                // para la animación paso a paso, pero normalmente se hace una segunda pasada
                // con solve() para obtener el camino final.
                return visitedSteps; // Retorna los pasos hasta que se encontró el fin
            }

            // Marcar celda actual como VISITED si no es START/END, solo en la copia
            if (current.getState() != CellState.START && current.getState() != CellState.END) {
                current.setState(CellState.VISITED);
            }


            for (Cell neighbor : getNeighbors(tempMaze, current)) {
                if (neighbor.getState() != CellState.WALL &&
                        !cameFrom.containsKey(neighbor)) { // Si no ha sido visitado antes
                    cameFrom.put(neighbor, current);
                    queue.add(neighbor);
                    // Añadir el vecino a la lista de pasos para la animación
                    if (neighbor.getState() != CellState.START && neighbor.getState() != CellState.END) {
                        visitedSteps.add(neighbor);
                    }
                }
            }
        }
        return visitedSteps; // Devuelve todos los pasos de la búsqueda, incluso si no se encontró camino
    }

    // --- Métodos Auxiliares Comunes a varios Solvers ---

    private List<Cell> getNeighbors(Cell[][] maze, Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int r = cell.getRow();
        int c = cell.getCol();
        int rows = maze.length;
        int cols = maze[0].length;

        // Orden de exploración: Arriba, Abajo, Izquierda, Derecha (común para BFS)
        if (r > 0) neighbors.add(maze[r - 1][c]);     // Arriba
        if (r < rows - 1) neighbors.add(maze[r + 1][c]); // Abajo
        if (c > 0) neighbors.add(maze[r][c - 1]);     // Izquierda
        if (c < cols - 1) neighbors.add(maze[r][c + 1]); // Derecha

        return neighbors;
    }

    protected List<Cell> reconstructPath(Map<Cell, Cell> cameFrom, Cell start, Cell end) {
        List<Cell> path = new ArrayList<>();
        Cell current = end;
        while (current != null && cameFrom.containsKey(current) || current.equals(start)) { // Asegurarse de incluir el inicio
            path.add(0, current); // Reconstruir el camino desde el final al inicio
            if (current.equals(start)) break; // Ya llegamos al inicio
            current = cameFrom.get(current);
        }
        // Si el inicio no estaba en cameFrom (caso de un solo paso o inicio), lo añadimos
        if (!path.contains(start) && start != null) {
            path.add(0, start);
        }
        return path;
    }

    // Copia profunda del laberinto para que los solvers no modifiquen el original
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