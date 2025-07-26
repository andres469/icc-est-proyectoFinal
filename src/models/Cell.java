package src.models;

public class Cell {
    private int row;
    private int col;
    private CellState state;

    public Cell(int row, int col, CellState state) {
        this.row = row;
        this.col = col;
        this.state = state;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }
}
