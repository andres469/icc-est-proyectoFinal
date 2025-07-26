package src.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import src.models.Cell; // Asegúrate de importar tus clases de modelo
import src.models.CellState; // Y tus estados de celda
// import src.models.Maze; // Si tienes un objeto Maze central

public class MazePanel extends JPanel {

    private int numRows;
    private int numCols;
    private Cell[][] mazeData; // Tu modelo de datos del laberinto (de src.models)

    // Enum para el modo de interacción actual
    public enum Interaction_Mode {
        NONE, SET_START, SET_END, TOGGLE_WALL
    }
    private Interaction_Mode currentMode = Interaction_Mode.NONE;

    public MazePanel(int rows, int cols) {
        this.numRows = rows;
        this.numCols = cols;
        setPreferredSize(new Dimension(cols * 30, rows * 30)); // Tamaño preferido (ej. 30px por celda)
        setBackground(Color.WHITE); // Fondo por defecto

        // Inicializar el modelo del laberinto
        this.mazeData = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                mazeData[r][c] = new Cell(r, c, CellState.EMPTY); // Todas las celdas vacías al inicio
            }
        }

        // Añadir el MouseListener para manejar clics en las celdas
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cellWidth = getWidth() / numCols;
                int cellHeight = getHeight() / numRows;

                int clickedCol = e.getX() / cellWidth;
                int clickedRow = e.getY() / cellHeight;

                if (clickedRow >= 0 && clickedRow < numRows && clickedCol >= 0 && clickedCol < numCols) {
                    handleCellClick(clickedRow, clickedCol);
                }
            }
        });
    }

    // Método para dibujar el laberinto
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Siempre llama a super.paintComponent

        int cellWidth = getWidth() / numCols;
        int cellHeight = getHeight() / numRows;

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                int x = c * cellWidth;
                int y = r * cellHeight;

                // Dibujar el fondo de la celda según su estado
                Color cellColor;
                CellState state = mazeData[r][c].getState();
                switch (state) {
                    case WALL:
                        cellColor = Color.BLACK;
                        break;
                    case START:
                        cellColor = Color.GREEN;
                        break;
                    case END:
                        cellColor = Color.RED;
                        break;
                    case VISITED: // Para visualización paso a paso o al final
                        cellColor = Color.LIGHT_GRAY;
                        break;
                    case PATH: // Para el camino final
                        cellColor = Color.BLUE;
                        break;
                    default: // EMPTY
                        cellColor = Color.WHITE;
                        break;
                }
                g.setColor(cellColor);
                g.fillRect(x, y, cellWidth, cellHeight);

                // Dibujar el borde de la celda
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellWidth, cellHeight);
            }
        }
    }

    // Método para manejar el clic en una celda
    private void handleCellClick(int row, int col) {
        Cell clickedCell = mazeData[row][col];

        switch (currentMode) {
            case SET_START:
                // Limpiar cualquier inicio anterior y establecer el nuevo
                clearPreviousState(CellState.START);
                clickedCell.setState(CellState.START);
                break;
            case SET_END:
                // Limpiar cualquier fin anterior y establecer el nuevo
                clearPreviousState(CellState.END);
                clickedCell.setState(CellState.END);
                break;
            case TOGGLE_WALL:
                // Alternar entre PARED y VACÍA
                if (clickedCell.getState() == CellState.WALL) {
                    clickedCell.setState(CellState.EMPTY);
                } else if (clickedCell.getState() == CellState.EMPTY) {
                    clickedCell.setState(CellState.WALL);
                }
                break;
            case NONE:
            default:
                // No hacer nada si no hay modo seleccionado o es desconocido
                System.out.println("Modo de interacción no seleccionado.");
                break;
        }
        currentMode = Interaction_Mode.NONE; // Resetear el modo después de la interacción
        repaint(); // Redibujar el panel para mostrar los cambios
    }

    // Método auxiliar para limpiar estados START/END previos
    private void clearPreviousState(CellState stateToClear) {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (mazeData[r][c].getState() == stateToClear) {
                    mazeData[r][c].setState(CellState.EMPTY);
                    return; // Asumimos un solo inicio/fin
                }
            }
        }
    }

    // Métodos públicos para ser llamados desde MazeFrame
    public void setInteractionMode(Interaction_Mode mode) {
        this.currentMode = mode;
    }

    public void clearMaze() {
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                mazeData[r][c].setState(CellState.EMPTY);
            }
        }
        repaint();
    }

    // Método para dibujar el camino después de la resolución
    public void drawPath(List<Cell> path) {
        if (path == null) return;
        for (Cell cell : path) {
            // Asegúrate de no sobrescribir START/END si es parte del camino
            if (cell.getState() != CellState.START && cell.getState() != CellState.END) {
                cell.setState(CellState.PATH);
            }
        }
        repaint();
    }

    // Getter para que MazeFrame pueda obtener los datos del laberinto para los algoritmos
    public Cell[][] getMazeData() {
        return mazeData;
    }
}