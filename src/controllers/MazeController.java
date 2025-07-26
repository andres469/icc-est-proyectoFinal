package src.controllers;

import src.dao.AlgorithmResultDAO;
import src.models.AlgorithmResult;
import src.models.Cell;
import src.models.SolveResults;
import src.solver.MazeSolver;

import java.util.ArrayList;
import java.util.List;

public class MazeController {
    private List<MazeSolver> solvers;
    private AlgorithmResultDAO resultDAO;
    private SolveResults solveResults;

    public MazeController(List<MazeSolver> solvers, AlgorithmResultDAO resultDAO) {
        this.solvers = solvers;
        this.resultDAO = resultDAO;
        this.solveResults = new SolveResults();
    }

    public SolveResults getSolveResults() {
        return solveResults;
    }

    public void solveMazeWithAll(Cell[][] maze, int startRow, int startCol, int endRow, int endCol) {
        solveResults = new SolveResults();

        for (MazeSolver solver : solvers) {
            Cell[][] mazeCopy = copyMaze(maze); // Para no modificar el original

            long startTime = System.currentTimeMillis();
            List<Cell> path = solver.solve(mazeCopy, startRow, startCol, endRow, endCol);
            long endTime = System.currentTimeMillis();

            AlgorithmResult result = new AlgorithmResult(
                solver.getName(),
                path.size(),
                endTime - startTime
            );

            resultDAO.saveResult(result);
            solveResults.addResult(result);
        }
    }

    private Cell[][] copyMaze(Cell[][] original) {
        int rows = original.length;
        int cols = original[0].length;
        Cell[][] copy = new Cell[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                copy[i][j] = new Cell(i, j, original[i][j].getState());
            }
        }

        return copy;
    }

    public List<AlgorithmResult> loadSavedResults() {
        return resultDAO.loadResults();
    }
}
