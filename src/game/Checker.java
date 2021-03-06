package game;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

import static game.Checkers.CELL_SIZE;

public class Checker extends StackPane {

    public enum CheckerCondition {
        WHITE(1), BLACK(-1);
        final int moveType;

        CheckerCondition(int moveType) {
            this.moveType = moveType;
        }
    }

    private boolean isQueen;
    private double x0, y0;
    private double mX, mY;



    public double getX0() {
        return x0;
    }

    public double getY0() {
        return y0;
    }

    private CheckerCondition checkerCondition;

    public CheckerCondition getCheckerCondition() {
        return checkerCondition;
    }

    public boolean validY(int y, int y0, int moveOrKill) {
        //return (isQueen )? Math.abs(y - y0) == Math.abs(checkerCondition.moveType) * moveOrKill : y - y0 == checkerCondition.moveType * moveOrKill;
        if (!isQueen) {
            return (moveOrKill == 2)
                    ? Math.abs(y - y0) == Math.abs(checkerCondition.moveType) * moveOrKill
                    : y - y0 == checkerCondition.moveType * moveOrKill;
        } else {
            int y_diff = Math.abs(y - y0);

            return false;
        }
    }

    public boolean validX(int x, int x0, int moveOrKill) {
        if (!isQueen) {
            return Math.abs(x - x0) == moveOrKill;
        } else {
            return false;
        }
    }

    public Pair<Integer, Integer> validXY(int x, int x0, int y, int y0, int moveOrKill) {
        if (moveOrKill == 1) {
            boolean isValid = (Math.abs(x - x0) == moveOrKill) && (y - y0 == checkerCondition.moveType);
            return (isValid) ? new Pair<>(Integer.valueOf(-1), Integer.valueOf(-1)) : null;
        } else {
            boolean isValid = (Math.abs(x - x0) == moveOrKill) && (Math.abs(y - y0) == moveOrKill);
            return (isValid) ? new Pair<>(Integer.valueOf(x0 + (x - x0) / 2), Integer.valueOf(y0 + (y - y0) / 2)) : null;
        }
    }

    public Checker(int x, int y, CheckerCondition checkerCondition) {

        this.checkerCondition = checkerCondition;
        move(x, y);
        Circle checker = new Circle(CELL_SIZE * 0.35);
        checker.setTranslateX((CELL_SIZE - CELL_SIZE * 0.35 * 2) / 2);
        checker.setTranslateY((CELL_SIZE - CELL_SIZE * 0.35 * 2) / 2);
        checker.setFill(checkerCondition == CheckerCondition.BLACK ? Color.BLACK : Color.WHITE);
            getChildren().addAll(checker);
        setOnMousePressed(e -> {
            mX = e.getSceneX();
            mY = e.getSceneY();
        });
        setOnMouseDragged((MouseEvent e) -> {
            relocate(e.getSceneX() - mX + x0, e.getSceneY() - mY + y0);
        });
    }

    public void queendraw() {

        Circle checker1 = new Circle(CELL_SIZE * 0.15);
        checker1.setTranslateX((CELL_SIZE - CELL_SIZE * 0.35 * 2) / 2);
        checker1.setTranslateY((CELL_SIZE - CELL_SIZE * 0.35 * 2) / 2);
        checker1.setFill(Color.RED);
        getChildren().add(checker1);
    }

    public void beforeMove() {
        relocate(x0, y0);
    }

    public void move(int x, int y) {
        x0 = x * CELL_SIZE;
        y0 = y * CELL_SIZE;
        relocate(x0, y0);
    }
    public boolean isQueen() {
        return isQueen;
    }
    public void setQueen(boolean queen) {
        isQueen = queen;
    }
}
