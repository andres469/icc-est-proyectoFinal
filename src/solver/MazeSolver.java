package src.solver;

import src.models.Cell;
import java.util.List;

public interface MazeSolver {
    String getName();
    List<Cell> solve(Cell[][] maze, int startRow, int startCol, int endRow, int endCol);
    List<Cell> solveAndGetSteps(Cell[][] maze, int startRow, int startCol, int endRow, int endCol);
}