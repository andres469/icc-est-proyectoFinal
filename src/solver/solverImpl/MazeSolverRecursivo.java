package src.solver.solverImpl;

import src.models.Cell;
import src.models.CellState;
import src.solver.MazeSolver;

import java.util.*;

public class MazeSolverRecursivo implements MazeSolver {
    private List<Cell> path = new ArrayList<>();
    private int rows, cols;

    @Override
    public String getName() {
        return "Recursivo (2 direcciones)";
    }

    @Override
    public List<Cell> solve(Cell[][] maze, int startRow, int startCol, int endRow, int endCol) {
        rows = maze.length;
        cols = maze[0].length;
        path.clear();
        if (dfs(maze, startRow, startCol, endRow, endCol)) return path;
        return new ArrayList<>();
    }

    private boolean dfs(Cell[][] maze, int r, int c, int endRow, int endCol) {
        if (r < 0 || c < 0 || r >= rows || c >= cols) return false;
        if (maze[r][c].getState() == CellState.WALL || maze[r][c].getState() == CellState.VISITED) return false;

        maze[r][c].setState(CellState.VISITED);
        path.add(maze[r][c]);

        if (r == endRow && c == endCol) return true;

        if (dfs(maze, r, c + 1, endRow, endCol)) return true;
        if (dfs(maze, r + 1, c, endRow, endCol)) return true;

        path.remove(path.size() - 1);
        return false;
    }
}