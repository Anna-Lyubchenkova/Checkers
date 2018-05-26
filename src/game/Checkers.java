package game;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Checkers extends Application {

    public static final int CELL_SIZE = 87;
    public static final int HEIGHT = 8;
    public static final int WIDTH = 8;
    private Group boardCell = new Group();
    private Group boardChecker = new Group();
    private Cell[][] cells = new Cell[WIDTH][HEIGHT];
    private int isBlack = -1;

    private Parent createWindow() {

        Pane pane = new Pane();
        pane.setPrefSize(CELL_SIZE * WIDTH, CELL_SIZE * HEIGHT);
        pane.getChildren().addAll(boardCell, boardChecker);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                Cell cell = new Cell(i, j, (i + j) % 2 == 0);
                cells[i][j] = cell;

                boardCell.getChildren().add(cell);

                Checker checker = null;

                if (j < 3 && (i + j) % 2 != 0) {
                    checker = takeChecker(i, j, Checker.CheckerCondition.WHITE);
                }

                if (j > 4 && (i + j) % 2 != 0) {
                    checker = takeChecker(i, j, Checker.CheckerCondition.BLACK);
                }
//                if(i==0 && j==1){
//                    checker = takeChecker(i,j, Checker.CheckerCondition.BLACK);
//                }
//                if (i==7 && j==0){
//                    checker = takeChecker(i,j, Checker.CheckerCondition.WHITE);
//                }
                if (checker != null) {
                    cell.setChecker(checker);
                    boardChecker.getChildren().add(checker);
                }
            }
        }
        return pane;
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createWindow());
        primaryStage.setTitle("Шашки");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Checker takeChecker(int x, int y, Checker.CheckerCondition checkerCondition) {

        Checker checker = new Checker(x, y, checkerCondition);
        checker.setOnMouseReleased(e -> {
            int x1 = boardCoordinates(checker.getLayoutX());
            int y1 = boardCoordinates(checker.getLayoutY());
            MoveCondition condition;
            if (x1 < 0 || y1 < 0 || x1 >= WIDTH || y1 >= HEIGHT) {
                condition = new MoveCondition(MoveCondition.MoveResult.NONE);
            } else condition = tryMove(x1, y1, checker);
            int x0 = boardCoordinates(checker.getX0());
            int y0 = boardCoordinates(checker.getY0());
            switch (condition.getMoveResult()) {
                case NONE:
                    checker.beforeMove();
                    break;
                case NORMAL:
                    checker.move(x1, y1);
                    cells[x0][y0].setChecker(null);
                    if((checker.getCheckerCondition().moveType==1&&y1==7 ||
                            checker.getCheckerCondition().moveType==-1&&y1==0)&& !checker.isQueen()) {
                        checker.setQueen(true);
                        checker.queendraw();

                    }
                    cells[x1][y1].setChecker(checker);
                break;
                case KILL:
                    checker.move(x1, y1);
                    cells[x0][y0].setChecker(null);
                    if((checker.getCheckerCondition().moveType==1&&y1==7 ||
                            checker.getCheckerCondition().moveType==-1&&y1==0)&& !checker.isQueen()){
                        checker.setQueen(true);
                        checker.queendraw();
                    }
                    cells[x1][y1].setChecker(checker);
                    Checker newChecker = condition.getChecker();
                    cells[boardCoordinates(newChecker.getX0())][boardCoordinates(newChecker.getY0())].setChecker(null);
                    boardChecker.getChildren().remove(newChecker);
                    break;
            }
        });
        return checker;
    }

    private MoveCondition tryMove(int x, int y, Checker checker) {

        if (cells[x][y].existChecker() || ((x + y) % 2 == 0)) return new MoveCondition(MoveCondition.MoveResult.NONE);
        int x0 = boardCoordinates(checker.getX0());
        int y0 = boardCoordinates(checker.getY0());
        if (Math.abs((x - x0)) == 1 && checker.validY(y, y0, 1) && isBlack == checker.getCheckerCondition().moveType) {
            isBlack *= -1;
            return new MoveCondition(MoveCondition.MoveResult.NORMAL);
        } else if (Math.abs(x - x0) == 2 && checker.validY(y, y0, 2) && isBlack == checker.getCheckerCondition().moveType) {
            int x1 = x0 + (x - x0) / 2;
            int y1 = y0 + (y - y0) / 2;
            if (cells[x1][y1].existChecker() && cells[x1][y1].getBoard().getCheckerCondition() != checker.getCheckerCondition()) {
                isBlack *= -1;
                return new MoveCondition(MoveCondition.MoveResult.KILL, cells[x1][y1].getBoard());
            }
        }
        return new MoveCondition(MoveCondition.MoveResult.NONE);
    }

    private int boardCoordinates(double coordinate) {
        return (int) (coordinate + CELL_SIZE / 2) / CELL_SIZE;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
