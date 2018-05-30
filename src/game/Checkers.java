package game;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Collections;


public class Checkers extends Application {

    public static final int CELL_SIZE = 87;
    public static final int HEIGHT = 8;
    public static final int WIDTH = 8;
    private Group boardCell = new Group();
    private Group boardChecker = new Group();
    private Cell[][] cells = new Cell[WIDTH][HEIGHT];
    private int isBlack = -1;

    private Checker checkerToGo = null;

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
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            if (cells[i][j].existChecker()
                                    && cells[i][j].getBoard().getCheckerCondition() == checker.getCheckerCondition()
                                    && checkMove(cells[i][j].getBoard())) {
                                checker.beforeMove();
                                return;
                            }
                        }
                    }

                    checker.move(x1, y1);
                    cells[x0][y0].setChecker(null);
                    if((checker.getCheckerCondition().moveType==1&&y1==7 ||
                            checker.getCheckerCondition().moveType==-1&&y1==0)&& !checker.isQueen()) {
                        checker.setQueen(true);
                        checker.queendraw();

                    }
                    cells[x1][y1].setChecker(checker);
                    isBlack *= -1;
                break;
                case KILL:
                    if (checkerToGo == null || checkerToGo == checker) {
                        checker.move(x1, y1);
                        cells[x0][y0].setChecker(null);
                        if ((checker.getCheckerCondition().moveType == 1 && y1 == 7 ||
                                checker.getCheckerCondition().moveType == -1 && y1 == 0) && !checker.isQueen()) {
                            checker.setQueen(true);
                            checker.queendraw();
                        }
                        cells[x1][y1].setChecker(checker);
                        Checker newChecker = condition.getChecker();
                        cells[boardCoordinates(newChecker.getX0())][boardCoordinates(newChecker.getY0())].setChecker(null);
                        boardChecker.getChildren().remove(newChecker);

                        if (checkMove(checker)) {
                            checkerToGo = checker;
                        } else {
                            isBlack *= -1;
                            checkerToGo = null;
                        }
                    } else {
                        checker.beforeMove();
                    }
                    break;
            }
        });
        return checker;
    }

    private boolean checkMove(Checker checker) {
        int x0 = boardCoordinates(checker.getX0());
        int y0 = boardCoordinates(checker.getY0());

        int end = (checker.isQueen()) ? 6 : 3;

        for (int i = 2; i < end; i++) {
            MoveCondition condition1 = tryMove(x0 - i, y0 - i, checker);
            MoveCondition condition2 = tryMove(x0 + i, y0 - i, checker);
            MoveCondition condition3 = tryMove(x0 - i, y0 + i, checker);
            MoveCondition condition4 = tryMove(x0 + i, y0 + i, checker);

            boolean b = condition1.getMoveResult() == MoveCondition.MoveResult.KILL
                    || condition2.getMoveResult() == MoveCondition.MoveResult.KILL
                    || condition3.getMoveResult() == MoveCondition.MoveResult.KILL
                    || condition4.getMoveResult() == MoveCondition.MoveResult.KILL;
            if (b) return b;
        }
        return false;
    }

    private MoveCondition tryMove(int x, int y, Checker checker) {

        if (x < 0 || y < 0 || x > WIDTH - 1 || y > WIDTH - 1)
            return new MoveCondition(MoveCondition.MoveResult.NONE);

        int x0 = boardCoordinates(checker.getX0());
        int y0 = boardCoordinates(checker.getY0());
        if (cells[x][y].existChecker() || (Math.abs(x - x0) != Math.abs(y - y0)))
            return new MoveCondition(MoveCondition.MoveResult.NONE);

        if (checker.isQueen()) {
            int x_dir = (x - x0) / Math.abs(x - x0);
            int y_dir = (y - y0) / Math.abs(y - y0);

            int xt = -1;
            int yt = -1;

            if (isBlack != checker.getCheckerCondition().moveType)
                return new MoveCondition(MoveCondition.MoveResult.NONE);

            int countEnemy = 0;
            Pair<Integer, Integer> checkerToDelete = null;

            while (xt != x && yt != y) {
                xt = x0 + x_dir;
                yt = y0 + y_dir;

                if (cells[xt][yt].existChecker()) {
                    if (cells[xt][yt].getBoard().getCheckerCondition() == checker.getCheckerCondition())
                        return new MoveCondition(MoveCondition.MoveResult.NONE);
                    else {
                        if (countEnemy > 0)
                            return new MoveCondition(MoveCondition.MoveResult.NONE);
                        countEnemy++;
                        checkerToDelete = new Pair<>(Integer.valueOf(xt), Integer.valueOf(yt));
                    }
                }

                x0 = xt;
                y0 = yt;
            }

            //isBlack *= -1;
            if (countEnemy == 0)
                return new MoveCondition(MoveCondition.MoveResult.NORMAL);
            else
                return new MoveCondition(MoveCondition.MoveResult.KILL,
                        cells[checkerToDelete.getKey().intValue()][checkerToDelete.getValue().intValue()].getBoard());
        } else {
            Pair<Integer, Integer> checkResult = checker.validXY(x, x0, y, y0, 1);
            if (/*Math.abs((x - x0)) == 1 checker.validX(x,x0,1) && checker.validY(y, y0, 1) &&*/
                    checkResult != null && checkResult.getKey().equals(Integer.valueOf(-1))
                            && isBlack == checker.getCheckerCondition().moveType) {
                //isBlack *= -1;
                return new MoveCondition(MoveCondition.MoveResult.NORMAL);
            } else {
                checkResult = checker.validXY(x, x0, y, y0, 2);
                if (/*Math.abs(x - x0) == 2*//**//* checker.validX(x,x0,2) && checker.validY(y, y0, 2)*/
                        checkResult != null
                                && isBlack == checker.getCheckerCondition().moveType) {
                    int x1 = checkResult.getKey().intValue();
                    int y1 = checkResult.getValue().intValue();
                    if (cells[x1][y1].existChecker() && cells[x1][y1].getBoard().getCheckerCondition() != checker.getCheckerCondition()) {
                        //isBlack *= -1;
                        return new MoveCondition(MoveCondition.MoveResult.KILL, cells[x1][y1].getBoard());
                    }
                }
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
