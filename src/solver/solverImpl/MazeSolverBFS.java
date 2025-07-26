package src.solver.solverImpl;

import src.models.Cell;
import src.models.CellState;
import src.solver.MazeSolver;

import java.util.*;

public class MazeSolverBFS implements MazeSolver {
    @Override
    public String getName() {
        return "BFS (Breadth-First Search)";
    }

    @Override
    public List<Cell> solve(Cell[][] maze, int startRow, int startCol, int endRow, int endCol) {
        int rows = maze.length;
        int cols = maze[0].length;
        Queue<Cell> queue = new LinkedList<>();
        Map<Cell, Cell> cameFrom = new HashMap<>();
        List<Cell> path = new ArrayList<>();

        Cell start = maze[startRow][startCol];
        Cell end = maze[endRow][endCol];

        queue.add(start);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            if (current.getState() == CellState.WALL || current.getState() == CellState.VISITED) continue;

            current.setState(CellState.VISITED);

            if (current == end) {
                while (current != null) {
                    path.add(0, current);
                    current = cameFrom.get(current);
                }
                return path;
            }

            for (Cell neighbor : getNeighbors(maze, current)) {
                if (!cameFrom.containsKey(neighbor)) {
                    queue.add(neighbor);
                    cameFrom.put(neighbor, current);
                }
            }
        }

        return path;
    }

    private List<Cell> getNeighbors(Cell[][] maze, Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int r = cell.getRow();
        int c = cell.getCol();
        int rows = maze.length;
        int cols = maze[0].length;

        if (r > 0) neighbors.add(maze[r - 1][c]);
        if (r < rows - 1) neighbors.add(maze[r + 1][c]);
        if (c > 0) neighbors.add(maze[r][c - 1]);
        if (c < cols - 1) neighbors.add(maze[r][c + 1]);

        return neighbors;
    }
}
