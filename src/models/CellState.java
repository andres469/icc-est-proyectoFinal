package src.models;

public enum CellState {
    EMPTY,
    WALL,
    START,
    END,
    VISITED, // Para los algoritmos que marcan celdas visitadas
    PATH     // Para el camino de la solución
}