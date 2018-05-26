package game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cell extends Rectangle {
    private Checker checker;


    boolean existChecker() {
        return checker != null;
    }

    public void setChecker(Checker checker) {
        this.checker = checker;
    }

    public Checker getBoard() {
        return checker;
    }

    public Cell(int x, int y, boolean color) {
        setWidth(Checkers.CELL_SIZE);
        setHeight(Checkers.CELL_SIZE);
        relocate(x * Checkers.CELL_SIZE, y * Checkers.CELL_SIZE);
        setFill(color ? Color.DARKGRAY:Color.GRAY);
    }

}
